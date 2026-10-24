package com.perl5.lang.perl.idea.run;

import com.perl5.PerlBundle;
import consulo.disposer.Disposable;
import consulo.execution.configuration.fragment.RunConfigurationFragment;
import consulo.execution.configuration.fragment.RunConfigurationFragmentEditor;
import consulo.execution.configuration.fragment.RunConfigurationFragmentLayout;
import consulo.localize.LocalizeValue;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.project.Project;
import consulo.ui.TextBoxWithExpandAction;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.util.lang.StringUtil;

import static com.perl5.lang.perl.idea.run.PerlConfigurationUIUtil.fragmentName;
import static com.perl5.lang.perl.idea.run.PerlConfigurationUIUtil.labelText;

public class PerlRequiredModulesFragment<T extends GenericPerlRunConfiguration> implements RunConfigurationFragment<T> {
  @Override
  public String getId() {
    return "perlRequiredModules";
  }

  @Override
  public LocalizeValue getName() {
    return fragmentName(PerlBundle.message("label.required.modules"));
  }

  @Override
  public boolean isVisibleByDefault(T target) {
    return !StringUtil.isEmptyOrSpaces(target.getRequiredModules());
  }

  @Override
  public void applyHidden(T target) {
    target.setRequiredModules("");
  }

  @RequiredUIAccess
  @Override
  public RunConfigurationFragmentEditor<T> createEditor(Project project, Disposable parent) {
    TextBoxWithExpandAction text = TextBoxWithExpandAction.create(
      PlatformIconGroup.actionsShow(), PerlBundle.message("label.required.modules"), GenericPerlRunConfiguration.PREREQUISITES_PARSER, GenericPerlRunConfiguration.PREREQUISITES_JOINER);
    text.setToolTipText(LocalizeValue.of(PerlBundle.message("tooltip.comma.separated.list.modules.required.to.run.configuration")));

    return new RunConfigurationFragmentEditor<>() {
      @RequiredUIAccess
      @Override
      public void addRows(RunConfigurationFragmentLayout layout) {
        layout.addLabeled(labelText(PerlBundle.message("label.required.modules")), text);
      }

      @RequiredUIAccess
      @Override
      public void reset(T target) {
        text.setValue(StringUtil.notNullize(target.getRequiredModules()), false);
      }

      @RequiredUIAccess
      @Override
      public void apply(T target) {
        target.setRequiredModules(StringUtil.notNullize(text.getValue()));
      }
    };
  }
}
