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

import consulo.application.AllIcons;
import consulo.logging.Logger;
import consulo.ui.ex.awt.ComboBox;
import consulo.ui.ex.popup.JBPopupFactory;
import consulo.ui.ex.popup.ListPopup;
import consulo.ui.ex.RelativePoint;
import consulo.ui.ex.awt.JBUI;
import consulo.ui.ex.awt.UIUtil;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.idea.actions.PerlDumbAwareAction;
import com.perl5.lang.perl.idea.configuration.settings.sdk.wrappers.Perl5SdkWrapper;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import consulo.ui.ex.action.ActionManager;
import consulo.ui.ex.action.ActionPlaces;
import consulo.ui.ex.action.ActionToolbar;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.DefaultActionGroup;
import consulo.ui.ex.action.Presentation;

// TODO consulo: Swing GUI designer form (Perl5SdkPanel.form) kept as is, to be rewritten with uni UI
public class Perl5SdkPanel {
  private static final Logger LOG = Logger.getInstance(Perl5SdkPanel.class);
  private @NotNull ComboBox<Perl5SdkWrapper> mySdkComboBox;
  private @NotNull JPanel myMainPanel;
  private final @NotNull DefaultActionGroup myActionGroup = new DefaultActionGroup();
  private @NotNull JPanel myToolBar;
  private @NotNull JLabel myLabel;

  public Perl5SdkPanel() {
    //noinspection ConstantValue
    if (myMainPanel == null) {
      // TODO consulo: GUI designer form is not instrumented in the Consulo build, same layout as Perl5SdkPanel.form is created in code
      createUIComponents();
      myLabel = new JLabel(PerlBundle.message("perl.config.interpreter.label"));
      mySdkComboBox = new ComboBox<>();
      myMainPanel = new JPanel(new BorderLayout(5, 0));
      myMainPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
      myMainPanel.add(myLabel, BorderLayout.WEST);
      myMainPanel.add(mySdkComboBox, BorderLayout.CENTER);
      myToolBar.setBorder(JBUI.Borders.emptyTop(3));
      myMainPanel.add(myToolBar, BorderLayout.EAST);
    }
  }

  public @NotNull ComboBox<Perl5SdkWrapper> getSdkComboBox() {
    return mySdkComboBox;
  }

  public @NotNull JPanel getMainPanel() {
    return myMainPanel;
  }

  public @NotNull DefaultActionGroup getActionGroup() {
    return myActionGroup;
  }

  /**
   * popup taken from {@link com.intellij.application.options.schemes.AbstractSchemesPanel}
   */
  private void createUIComponents() {
    DefaultActionGroup toolbarActionGroup = new DefaultActionGroup();

    ActionToolbar toolbar =
      ActionManager.getInstance().createActionToolbar(ActionPlaces.NAVIGATION_BAR_TOOLBAR, toolbarActionGroup, true);
    toolbar.setTargetComponent(null);
    // TODO consulo: ActionToolbarImpl#setReservePlaceAutoPopupIcon(false) is not an API in Consulo
    toolbar.setLayoutPolicy(ActionToolbar.NOWRAP_LAYOUT_POLICY);
    toolbar.getComponent().setBorder(JBUI.Borders.empty());

    toolbarActionGroup.add(new PerlDumbAwareAction() {
      @Override
      public void update(@NotNull AnActionEvent e) {
        Presentation p = e.getPresentation();
        p.setIcon(AllIcons.General.GearPlain);
        p.setVisible(true);
        p.setEnabled(toolbar.getComponent().isEnabled());
      }

      @Override
      public void actionPerformed(@NotNull AnActionEvent e) {
        ListPopup actionGroupPopup = JBPopupFactory.getInstance().
          createActionGroupPopup(null, myActionGroup, e.getDataContext(), true, null, Integer.MAX_VALUE);

        var inputEvent = e.getInputEvent();
        if (inputEvent == null) {
          LOG.debug("No input event in action event");
        }
        // TODO consulo: HelpTooltip.setMasterPopup(inputEvent.getComponent(), actionGroupPopup) has no Consulo counterpart
        actionGroupPopup.show(new RelativePoint(toolbar.getComponent(), getPopupPoint()));
      }

      /**
       * @implNote this is a copypaste from {@link consulo.ui.ex.action.DumbAwareAction#getPopupPoint} and seems there is no replacement.
       */
      private Point getPopupPoint() {
        // TODO consulo: UIUtil.isUnderWin10LookAndFeel() is missing in Consulo
        int dH = 0;
        return new Point(JBUI.scale(2), toolbar.getComponent().getHeight() - dH);
      }
    });
    // upstream: ActionToolbarImpl is a JPanel itself
    myToolBar = new JPanel(new BorderLayout());
    myToolBar.add(toolbar.getComponent(), BorderLayout.CENTER);
  }

  public void setEnabled(boolean isEnabled) {
    myLabel.setEnabled(isEnabled);
    mySdkComboBox.setEnabled(isEnabled);
    myToolBar.setEnabled(isEnabled);
  }

  public void setLabelText(@Nls String text) {
    myLabel.setText(text);
  }
}
