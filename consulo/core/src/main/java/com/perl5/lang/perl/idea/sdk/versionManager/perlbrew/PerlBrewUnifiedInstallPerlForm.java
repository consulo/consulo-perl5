package com.perl5.lang.perl.idea.sdk.versionManager.perlbrew;

import com.perl5.PerlBundle;
import com.perl5.lang.perl.idea.sdk.versionManager.PerlUnifiedInstallForm;
import com.perl5.lang.perl.idea.sdk.versionManager.PerlUnifiedInstallFormOptions;
import consulo.localize.LocalizeValue;
import consulo.ui.CheckBox;
import consulo.ui.ComboBox;
import consulo.ui.Component;
import consulo.ui.TextBox;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.util.FormBuilder;
import consulo.util.lang.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Unified UI (works in desktop and web) counterpart of {@link PerlBrewInstallPerlForm}
 */
class PerlBrewUnifiedInstallPerlForm extends PerlUnifiedInstallFormOptions {
  private final ComboBox<Integer> myThreadsComboBox;
  private final TextBox myNameTextField;
  private final CheckBox myForceInstallationCheckBox;
  private final CheckBox mySkipTestingCheckBox;
  private final CheckBox myNoManpagesCheckBox;
  private final CheckBox myWithUsethreadsCheckBox;
  private final CheckBox myWithUsemultiplicityCheckBox;
  private final CheckBox myWithUse64bitintCheckBox;
  private final CheckBox myWithUse64bitallCheckBox;
  private final CheckBox myWithUselongdoubleCheckBox;
  private final CheckBox myWithDEBUGGINGEnabledCheckBox;
  private final CheckBox myUseClangCheckBox;
  private final CheckBox myDonTCallPatchperlCheckBox;

  @RequiredUIAccess
  PerlBrewUnifiedInstallPerlForm() {
    myThreadsComboBox = PerlUnifiedInstallForm.createThreadsComboBox();
    myNameTextField = TextBox.create();
    myForceInstallationCheckBox = createCheckBox("perl.vm.perlbrew.install.form.force", false);
    mySkipTestingCheckBox = createCheckBox("perl.vm.install.form.no.testing", true);
    myNoManpagesCheckBox = createCheckBox("perl.vm.install.form.no.man", true);
    myWithUsethreadsCheckBox = createCheckBox("perl.vm.perlbrew.install.form.usethreads", false);
    myWithUsemultiplicityCheckBox = createCheckBox("perl.vm.perlbrew.install.form.multiplicity", false);
    myWithUse64bitintCheckBox = createCheckBox("perl.vm.perlbrew.install.form.use64int", false);
    myWithUse64bitallCheckBox = createCheckBox("perl.vm.perlbrew.install.form.use64all", false);
    myWithUselongdoubleCheckBox = createCheckBox("perl.vm.perlbrew.install.form.longdouble", false);
    myWithDEBUGGINGEnabledCheckBox = createCheckBox("perl.vm.perlbrew.install.form.debugging", false);
    myUseClangCheckBox = createCheckBox("perl.vm.perlbrew.install.form.clang", false);
    myDonTCallPatchperlCheckBox = createCheckBox("perl.vm.perlbrew.install.form.no.patchperl", false);
  }

  @RequiredUIAccess
  private static CheckBox createCheckBox(String messageKey, boolean selected) {
    return CheckBox.create(LocalizeValue.of(PerlBundle.message(messageKey)), selected);
  }

  @RequiredUIAccess
  @Override
  public Component createComponent() {
    FormBuilder formBuilder = FormBuilder.create()
      .addLabeled(LocalizeValue.of(PerlBundle.message("perl.vm.install.form.threads.label")), myThreadsComboBox)
      .addLabeled(LocalizeValue.of(PerlBundle.message("perl.vm.install.form.install.as")), myNameTextField);
    formBuilder.addBottom(myForceInstallationCheckBox);
    formBuilder.addBottom(mySkipTestingCheckBox);
    formBuilder.addBottom(myNoManpagesCheckBox);
    formBuilder.addBottom(myWithUsethreadsCheckBox);
    formBuilder.addBottom(myWithUsemultiplicityCheckBox);
    formBuilder.addBottom(myWithUse64bitintCheckBox);
    formBuilder.addBottom(myWithUse64bitallCheckBox);
    formBuilder.addBottom(myWithUselongdoubleCheckBox);
    formBuilder.addBottom(myWithDEBUGGINGEnabledCheckBox);
    formBuilder.addBottom(myUseClangCheckBox);
    formBuilder.addBottom(myDonTCallPatchperlCheckBox);
    return PerlUnifiedInstallForm.createOptionsLayout(formBuilder.build());
  }

  @Override
  public String getTargetName(String distributionId) {
    String userText = StringUtil.notNullize(myNameTextField.getValue()).trim();
    return StringUtil.isEmpty(userText) ? distributionId : userText;
  }

  @Override
  public List<String> buildParametersList() {
    List<String> buildParams = new ArrayList<>();
    if (myForceInstallationCheckBox.getValue()) {
      buildParams.add("--force");
    }
    Integer threads = myThreadsComboBox.getValue();
    if (threads != null && threads > 1) {
      buildParams.add("-j");
      buildParams.add(threads.toString());
    }
    if (mySkipTestingCheckBox.getValue()) {
      buildParams.add("--notest");
    }

    String targetName = StringUtil.notNullize(myNameTextField.getValue());
    if (StringUtil.isNotEmpty(targetName)) {
      buildParams.add("--as");
      buildParams.add(targetName);
    }

    if (myNoManpagesCheckBox.getValue()) {
      buildParams.add("--noman");
    }

    if (myWithUsethreadsCheckBox.getValue()) {
      buildParams.add("--thread");
    }

    if (myWithUsemultiplicityCheckBox.getValue()) {
      buildParams.add("--multi");
    }

    if (myWithUse64bitintCheckBox.getValue()) {
      buildParams.add("--64int");
    }

    if (myWithUse64bitallCheckBox.getValue()) {
      buildParams.add("--64all");
    }

    if (myWithUselongdoubleCheckBox.getValue()) {
      buildParams.add("--ld");
    }

    if (myWithDEBUGGINGEnabledCheckBox.getValue()) {
      buildParams.add("--debug");
    }

    if (myUseClangCheckBox.getValue()) {
      buildParams.add("--clang");
    }

    if (myDonTCallPatchperlCheckBox.getValue()) {
      buildParams.add("--no-patchperl");
    }
    return buildParams;
  }
}
