/*
 * Copyright 2015-2026 Alexandr Evstigneev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.perl5.lang.perl.idea.configuration.settings.sdk;

import consulo.execution.ui.awt.CommonProgramParametersPanel;
import consulo.disposer.Disposable;
import consulo.ui.ex.action.ActionGroup;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.DefaultActionGroup;
import consulo.application.ApplicationManager;
import consulo.configurable.UnnamedConfigurable;
import consulo.application.progress.ProgressIndicator;
import consulo.application.progress.Task;
import consulo.ui.ex.action.DumbAwareAction;
import consulo.project.Project;
import consulo.content.bundle.Sdk;
import consulo.content.bundle.event.SdkTableListener;
import consulo.content.bundle.SdkModificator;
import com.intellij.openapi.projectRoots.impl.PerlSdkTable;
import consulo.ui.ex.awt.ComboBox;
import consulo.ui.ex.InputValidator;
import consulo.ui.ex.awt.Messages;
import consulo.disposer.Disposer;
import consulo.util.lang.StringUtil;
import consulo.ui.ex.awt.CollectionComboBoxModel;
import consulo.ui.ex.awt.ColoredListCellRenderer;
import consulo.component.messagebus.MessageBusConnection;
import com.perl5.PerlBundle;
import com.perl5.PerlIcons;
import consulo.ui.ex.awtUnsafe.TargetAWT;
import com.perl5.lang.perl.idea.actions.PerlDumbAwareAction;
import com.perl5.lang.perl.idea.configuration.settings.sdk.wrappers.Perl5RealSdkWrapper;
import com.perl5.lang.perl.idea.configuration.settings.sdk.wrappers.Perl5SdkWrapper;
import com.perl5.lang.perl.idea.configuration.settings.sdk.wrappers.Perl5TextSdkWrapper;
import com.perl5.lang.perl.idea.sdk.host.PerlHostHandler;
import com.perl5.lang.perl.idea.sdk.versionManager.PerlVersionManagerHandler;
import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Perl5SdkConfigurable implements UnnamedConfigurable, SdkTableListener {
  public static final Perl5SdkWrapper DISABLE_PERL_ITEM = new Perl5TextSdkWrapper(PerlBundle.message("perl.settings.disable.perl.support"));
  private Perl5SdkPanel myPanel;
  private final @NotNull MessageBusConnection myConnection;
  private final @NotNull Perl5SdkManipulator mySdkManipulator;
  private @Nullable Project myProject;

  private final Disposable myDisposable = Disposable.newDisposable();

  /**
   * @apiNote if you are passing null to the constructor, you MUST then call {@link #setProject(Project)}
   * with non null project value. This is a hack, introduced to fix problem in {@link CommonProgramParametersPanel}
   * which invokes some overridable methods from the constructor, namely - {@link CommonProgramParametersPanel#addComponents()}
   */
  public Perl5SdkConfigurable(@NotNull Perl5SdkManipulator sdkManipulator, @Nullable Project project) {
    myConnection = ApplicationManager.getApplication().getMessageBus().connect();
    mySdkManipulator = sdkManipulator;
    myProject = project;
  }

  public void setProject(@NotNull Project project) {
    myProject = project;
  }

  @RequiredUIAccess
  @Override
  public Component createUIComponent(Disposable parentDisposable) {
    return TargetAWT.wrap(getComponent());
  }

  public @NotNull JComponent getComponent() {
    if (myPanel == null) {
      initPanel();
    }
    return myPanel.getMainPanel();
  }

  private void initPanel() {
    myPanel = new Perl5SdkPanel();

    configureSdkCombobox();
    myConnection.subscribe(PerlSdkTable.PERL_TABLE_TOPIC, this);

    // add sdk button
    DefaultActionGroup panelActionGroup = myPanel.getActionGroup();

    List<ActionGroup> groups = PerlHostHandler.stream()
      .filter(PerlHostHandler::isApplicable)
      .map(hostHandler -> {
        List<DumbAwareAction> groupItems = PerlVersionManagerHandler.stream()
          .filter(it -> it.isApplicable(hostHandler.getOsHandler()))
          .map(versionManagerHandler -> new PerlDumbAwareAction(versionManagerHandler.getMenuItemTitle()) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
              new Task.Modal(myProject, PerlBundle.message("perl.create.interpreter.progress"), false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                  // consulo: Task has own myProject field
                  versionManagerHandler.createSdkInteractively(
                    Perl5SdkConfigurable.this.myProject, hostHandler, sdk -> updateSdkModel(new Perl5RealSdkWrapper(sdk)), myDisposable);
                }
              }.queue();
            }

            @Override
            public void update(@NotNull AnActionEvent e) {
              e.getPresentation().setEnabledAndVisible(true);
            }
          }).collect(Collectors.toList());

        ActionGroup hostActionGroup = new ActionGroup(hostHandler.getMenuItemTitle(), true) {
          @Override
          public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
            return groupItems.toArray(AnAction.EMPTY_ARRAY);
          }
        };
        hostActionGroup.getTemplatePresentation().setIcon(TargetAWT.from(hostHandler.getIcon()));
        return hostActionGroup;
      }).toList();

    if (groups.size() == 1) {
      for (AnAction action : groups.getFirst().getChildren(null)) {
        panelActionGroup.add(action);
      }
    }
    else {
      groups.forEach(panelActionGroup::add);
    }

    panelActionGroup.add(new PerlDumbAwareAction(PerlBundle.message("perl.interpreter.edit")) {
      @Override
      public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(getSelectedSdk() != null);
      }

      @Override
      public void actionPerformed(@NotNull AnActionEvent e) {
        renameSdk();
      }
    });
    panelActionGroup.add(new PerlDumbAwareAction(PerlBundle.message("perl.interpreter.delete")) {
      @Override
      public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabledAndVisible(getSelectedSdk() != null);
      }

      @Override
      public void actionPerformed(@NotNull AnActionEvent e) {
        removeSdk();
      }
    });
    myPanel.getSdkComboBox().registerKeyboardAction(e -> {
      if (getSelectedSdk() != null) {
        PerlSdkTable.getInstance().removeJdk(getSelectedSdk());
      }
    }, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), 0);
  }

  private void configureSdkCombobox() {
    JComboBox<Perl5SdkWrapper> sdkComboBox = myPanel.getSdkComboBox();
    sdkComboBox.setModel(new CollectionComboBoxModel<>(mySdkManipulator.getAllSdkWrappers()));
    sdkComboBox.setRenderer(new ColoredListCellRenderer<>() {
      @Override
      protected void customizeCellRenderer(@NotNull JList<? extends Perl5SdkWrapper> list,
                                           Perl5SdkWrapper wrapper,
                                           int index,
                                           boolean selected,
                                           boolean hasFocus) {
        if (wrapper != null) {
          wrapper.customizeRenderer(this);
        }
      }
    });
    sdkComboBox.addItemListener(e -> {
      if (e.getStateChange() == ItemEvent.SELECTED) {
        mySdkManipulator.selectionChanged((Perl5SdkWrapper)e.getItem());
      }
    });
  }

  private void updateSdkModel(@Nullable Perl5SdkWrapper itemToSelect) {
    JComboBox<Perl5SdkWrapper> sdkComboBox = myPanel.getSdkComboBox();
    List<Perl5SdkWrapper> allItems = mySdkManipulator.getAllSdkWrappers();
    if (itemToSelect == null || !allItems.contains(itemToSelect)) {
      itemToSelect = allItems.isEmpty() ? null : allItems.getFirst();
    }
    sdkComboBox.setModel(new CollectionComboBoxModel<>(allItems, itemToSelect));
    mySdkManipulator.selectionChanged(itemToSelect);
  }

  public @Nullable Sdk getSelectedSdk() {
    Perl5SdkWrapper selectedWrapper = getSelectedSdkWrapper();
    return selectedWrapper instanceof Perl5RealSdkWrapper realSdkWrapper ? realSdkWrapper.getSdk() : null;
  }

  public @Nullable Perl5SdkWrapper getSelectedSdkWrapper() {
    return (Perl5SdkWrapper)myPanel.getSdkComboBox().getSelectedItem();
  }

  private void removeSdk() {
    Sdk selectedSdk = getSelectedSdk();
    if (selectedSdk != null) {
      PerlSdkTable.getInstance().removeJdk(selectedSdk);
    }
  }

  private void renameSdk() {
    Sdk selectedSdk = getSelectedSdk();
    if (selectedSdk == null) {
      return;
    }
    PerlSdkTable perlSdkTable = PerlSdkTable.getInstance();
    Messages.showInputDialog(
      myPanel.getSdkComboBox(),
      PerlBundle.message("perl.rename.sdk.text"),
      PerlBundle.message("perl.rename.sdk.title"),
      PerlIcons.PERL_LANGUAGE_ICON,
      selectedSdk.getName(),
      new InputValidator() {
        @Override
        public boolean checkInput(String inputString) {
          if (StringUtil.isEmpty(inputString)) {
            return false;
          }
          Sdk perlSdk = perlSdkTable.findJdk(inputString);
          return perlSdk == null || perlSdk.equals(selectedSdk);
        }

        @Override
        public boolean canClose(String inputString) {
          if (StringUtil.equals(selectedSdk.getName(), inputString)) {
            return true;
          }
          SdkModificator modificator = selectedSdk.getSdkModificator();
          modificator.setName(inputString);
          assert modificator instanceof Sdk;
          perlSdkTable.updateJdk(selectedSdk, (Sdk)modificator);
          myPanel.getSdkComboBox().repaint();
          return true;
        }
      }
    );
  }

  private boolean isSdkModified() {
    return !Objects.equals(mySdkManipulator.getCurrentSdkWrapper(), getSelectedSdkWrapper());
  }


  @Override
  public boolean isModified() {
    return isSdkModified();
  }

  @Override
  public void apply() {
    if (isSdkModified()) {
      mySdkManipulator.setSdk(getSelectedSdk());
    }
  }

  @Override
  public void reset() {
    myPanel.getSdkComboBox().setSelectedItem(mySdkManipulator.getCurrentSdkWrapper());
    mySdkManipulator.selectionChanged(mySdkManipulator.getCurrentSdkWrapper());
  }

  @Override
  public void disposeUIResources() {
    myConnection.deliverImmediately();
    myConnection.disconnect();
    myPanel = null;
    Disposer.dispose(myDisposable);
  }

  // TODO consulo: platform SdkTableListener fires for all bundles, not only perl interpreters
  @Override
  public void sdkAdded(@NotNull Sdk sdk) {
    updateSdkModel(getSelectedSdkWrapper());
  }

  @Override
  public void sdkRemoved(@NotNull Sdk sdk) {
    // fixme we should create a wrapper here and find it in combobox
    ComboBox<Perl5SdkWrapper> comboBox = myPanel.getSdkComboBox();
    int selectedIndex = comboBox.getSelectedIndex();
    int totalItems = comboBox.getItemCount();

    Perl5SdkWrapper itemToSelect = null;
    if (selectedIndex >= 0 && selectedIndex < totalItems - 1) {
      itemToSelect = comboBox.getItemAt(selectedIndex + 1);
    }
    else if (selectedIndex > 0) {
      itemToSelect = comboBox.getItemAt(selectedIndex - 1);
    }
    else if (totalItems > 0) {
      itemToSelect = comboBox.getItemAt(0);
    }

    updateSdkModel(itemToSelect);
  }

  public void setEnabled(boolean isEnabled) {
    myPanel.setEnabled(isEnabled);
  }

  public void setLabelText(@NotNull String text) {
    myPanel.setLabelText(text);
  }
}
