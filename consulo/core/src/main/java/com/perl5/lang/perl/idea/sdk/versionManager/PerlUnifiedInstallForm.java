package com.perl5.lang.perl.idea.sdk.versionManager;

import com.perl5.PerlBundle;
import consulo.disposer.Disposable;
import consulo.localize.LocalizeValue;
import consulo.perl5.core.ui.PerlDialogUtil;
import consulo.project.Project;
import consulo.ui.CheckBox;
import consulo.ui.ComboBox;
import consulo.ui.Component;
import consulo.ui.Label;
import consulo.ui.TextAttribute;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.dialog.DialogDescriptor;
import consulo.ui.ex.dialog.action.DialogOkAction;
import consulo.ui.layout.VerticalLayout;
import consulo.ui.util.FormBuilder;
import consulo.util.lang.StringUtil;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Unified UI (works in desktop and web) counterpart of {@link PerlInstallForm} and the Swing dialog of {@link InstallPerlHandler}:
 * the distribution to install, what to do with the installed perl and the {@link PerlUnifiedInstallFormOptions options} of the
 * version manager.
 */
public final class PerlUnifiedInstallForm extends DialogDescriptor {
  private final InstallPerlHandler myHandler;
  private final PerlInstallFormOptions myOptionsForm;
  private final List<String> myDistributions;
  private final boolean myCanChooseInstalledPerl;

  private @Nullable ComboBox<String> myDistributionsComboBox;
  private @Nullable CheckBox myAddInstalledPerl5ToCheckBox;
  private @Nullable CheckBox mySetInstalledPerl5ForCheckBox;

  private PerlUnifiedInstallForm(InstallPerlHandler handler,
                                 PerlInstallFormOptions optionsForm,
                                 List<String> distributions,
                                 boolean canChooseInstalledPerl) {
    super(LocalizeValue.of(PerlBundle.message("perl.vm.install.form.title")));
    myHandler = handler;
    myOptionsForm = optionsForm;
    myDistributions = new ArrayList<>(distributions);
    myDistributions.sort(handler::doCompareVersions);
    myCanChooseInstalledPerl = canChooseInstalledPerl;
  }

  /**
   * Shows the install dialog. The {@code installConsumer} is invoked on the UI thread, only if the user asked to install
   *
   * @param project project the installed perl may be chosen for, the choice is not offered without one
   */
  @RequiredUIAccess
  public static void show(InstallPerlHandler handler,
                          @Nullable Project project,
                          PerlInstallFormOptions optionsForm,
                          List<String> distributions,
                          Consumer<? super PerlUnifiedInstallForm> installConsumer) {
    PerlUnifiedInstallForm form = new PerlUnifiedInstallForm(handler, optionsForm, distributions, project != null);
    PerlDialogUtil.showAndThen(() -> PerlDialogUtil.showAsync(project, form), value -> {
      if (value != null) {
        installConsumer.accept(form);
      }
    });
  }

  @RequiredUIAccess
  @Override
  public Component createCenterComponent(Disposable uiDisposable) {
    ComboBox<String> distributionsComboBox = ComboBox.create(myDistributions);
    distributionsComboBox.setRenderer((presentation, item) -> {
      String value = item.getValue();
      if (value == null) {
        return;
      }
      presentation.withIcon(myHandler.doGetIcon(value));
      if (myHandler.doIsInstalled(value)) {
        presentation.append(myHandler.doCleanDistributionItem(value));
        presentation.append(PerlBundle.message("installed"), TextAttribute.GRAYED);
      }
      else {
        presentation.append(value);
      }
    });
    distributionsComboBox.setSpeedSearchConverter(it -> it);
    distributionsComboBox.selectFirst();
    distributionsComboBox.addValueListener(event -> updateOkButtonState());
    myDistributionsComboBox = distributionsComboBox;

    CheckBox addInstalledPerl5ToCheckBox = CheckBox.create(LocalizeValue.of(PerlBundle.message("perl.vm.perlbrew.install.form.add")), true);
    addInstalledPerl5ToCheckBox.addValueListener(event -> updateState());
    myAddInstalledPerl5ToCheckBox = addInstalledPerl5ToCheckBox;

    mySetInstalledPerl5ForCheckBox = CheckBox.create(LocalizeValue.of(PerlBundle.message("perl.vm.perlbrew.install.form.select")));

    FormBuilder formBuilder = FormBuilder.create()
      .addLabeled(LocalizeValue.of(PerlBundle.message("perl.vm.perlbrew.install.form.select.distribution")), distributionsComboBox);
    formBuilder.addBottom(addInstalledPerl5ToCheckBox);
    formBuilder.addBottom(mySetInstalledPerl5ForCheckBox);
    if (myOptionsForm instanceof PerlUnifiedInstallFormOptions unifiedOptionsForm) {
      formBuilder.addBottom(unifiedOptionsForm.createComponent());
    }
    updateState();
    return formBuilder.build();
  }

  @RequiredUIAccess
  private void updateState() {
    if (mySetInstalledPerl5ForCheckBox != null) {
      mySetInstalledPerl5ForCheckBox.setEnabled(myCanChooseInstalledPerl && isAddInstalledPerl());
    }
    updateOkButtonState();
  }

  @Override
  public boolean doUpdateOkButtonState() {
    return StringUtil.isNotEmpty(getSelectedDistributionId());
  }

  @Override
  protected DialogOkAction createOkAction() {
    return new DialogOkAction(LocalizeValue.of(PerlBundle.message("perl.vm.perlbrew.install.form.button")));
  }

  @RequiredUIAccess
  @Override
  public @Nullable Component getPreferredFocusedComponent() {
    return myDistributionsComboBox;
  }

  public boolean isAddInstalledPerl() {
    return myAddInstalledPerl5ToCheckBox != null && myAddInstalledPerl5ToCheckBox.getValue();
  }

  public boolean isChooseInstalledPerl() {
    return mySetInstalledPerl5ForCheckBox != null && mySetInstalledPerl5ForCheckBox.isEnabled() && mySetInstalledPerl5ForCheckBox.getValue();
  }

  public String getSelectedDistributionId() {
    String selected = myDistributionsComboBox == null ? null : myDistributionsComboBox.getValue();
    return myHandler.doCleanDistributionItem(StringUtil.notNullize(selected));
  }

  /**
   * Counterpart of {@link PerlInstallForm#configureThreadsCombobox}: the number of build threads, up to the number of cores
   */
  @RequiredUIAccess
  public static ComboBox<Integer> createThreadsComboBox() {
    int coresCount = Runtime.getRuntime().availableProcessors();
    List<Integer> threadsList = new ArrayList<>(coresCount);
    for (int i = 1; i <= coresCount; i++) {
      threadsList.add(i);
    }
    ComboBox<Integer> threadsComboBox = ComboBox.create(threadsList);
    threadsComboBox.setRenderer((presentation, item) -> {
      Integer value = item.getValue();
      if (value == null) {
        return;
      }
      if (value == 1) {
        presentation.append(PerlBundle.message("single.thread"));
      }
      else {
        presentation.append(Integer.toString(value));
        presentation.append(PerlBundle.message("threads"));
      }
    });
    threadsComboBox.setSpeedSearchConverter(Object::toString);
    threadsComboBox.selectFirst();
    return threadsComboBox;
  }

  /**
   * The explanation the version manager options start with
   */
  @RequiredUIAccess
  public static VerticalLayout createOptionsLayout(Component options) {
    VerticalLayout layout = VerticalLayout.create();
    // a label is a single line
    layout.add(Label.create(LocalizeValue.of(PerlBundle.message("perl.vm.install.form.message").replace('\n', ' '))));
    layout.add(options);
    return layout;
  }
}
