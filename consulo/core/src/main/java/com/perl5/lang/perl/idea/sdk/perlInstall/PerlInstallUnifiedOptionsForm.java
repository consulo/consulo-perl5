package com.perl5.lang.perl.idea.sdk.perlInstall;

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
 * Unified UI (works in desktop and web) counterpart of {@link PerlInstallOptionsForm}
 */
class PerlInstallUnifiedOptionsForm extends PerlUnifiedInstallFormOptions {
  private final ComboBox<Integer> myThreads;
  private final TextBox myTargetName;
  private final CheckBox mySkipTestingCheckBox;
  private final CheckBox myManCheckbox;
  private final CheckBox myNoPatch;

  @RequiredUIAccess
  PerlInstallUnifiedOptionsForm() {
    myThreads = PerlUnifiedInstallForm.createThreadsComboBox();
    myTargetName = TextBox.create();
    mySkipTestingCheckBox = CheckBox.create(LocalizeValue.of(PerlBundle.message("perl.vm.install.form.no.testing")), true);
    myManCheckbox = CheckBox.create(LocalizeValue.of(PerlBundle.message("perl.vm.install.form.bulid.man")), false);
    myNoPatch = CheckBox.create(LocalizeValue.of(PerlBundle.message("perl.vm.perlbrew.install.form.no.patchperl")), false);
  }

  @RequiredUIAccess
  @Override
  public Component createComponent() {
    FormBuilder formBuilder = FormBuilder.create()
      .addLabeled(LocalizeValue.of(PerlBundle.message("perl.vm.install.form.threads.label")), myThreads)
      .addLabeled(LocalizeValue.of(PerlBundle.message("perl.vm.install.form.install.as")), myTargetName);
    formBuilder.addBottom(mySkipTestingCheckBox);
    formBuilder.addBottom(myManCheckbox);
    formBuilder.addBottom(myNoPatch);
    return PerlUnifiedInstallForm.createOptionsLayout(formBuilder.build());
  }

  @Override
  public String getTargetName(String distributionId) {
    String userText = StringUtil.notNullize(myTargetName.getValue()).trim();
    return StringUtil.isEmpty(userText) ? distributionId : userText;
  }

  @Override
  public List<String> buildParametersList() {
    List<String> buildParams = new ArrayList<>();

    Integer threads = myThreads.getValue();
    if (threads != null && threads > 1) {
      buildParams.add("-j");
      buildParams.add(threads.toString());
    }

    if (!mySkipTestingCheckBox.getValue()) {
      buildParams.add("--test");
    }

    if (myManCheckbox.getValue()) {
      buildParams.add("--man");
    }

    String targetName = StringUtil.notNullize(myTargetName.getValue()).trim();
    if (StringUtil.isNotEmpty(targetName)) {
      buildParams.add("--as=" + targetName);
    }

    if (myNoPatch.getValue()) {
      buildParams.add("--nopatch");
    }

    return buildParams;
  }
}
