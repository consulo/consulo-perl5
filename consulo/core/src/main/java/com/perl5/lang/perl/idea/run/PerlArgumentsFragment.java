package com.perl5.lang.perl.idea.run;

import com.perl5.PerlBundle;
import consulo.disposer.Disposable;
import consulo.execution.configuration.fragment.RunConfigurationFragment;
import consulo.execution.configuration.fragment.RunConfigurationFragmentEditor;
import consulo.execution.configuration.fragment.RunConfigurationFragmentLayout;
import consulo.localize.LocalizeValue;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.process.cmd.ParametersListUtil;
import consulo.project.Project;
import consulo.ui.TextBoxWithExpandAction;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.util.lang.StringUtil;

import static com.perl5.lang.perl.idea.run.PerlConfigurationUIUtil.fragmentName;
import static com.perl5.lang.perl.idea.run.PerlConfigurationUIUtil.labelText;

public class PerlArgumentsFragment<T extends GenericPerlRunConfiguration> implements RunConfigurationFragment<T> {
  @Override
  public String getId() {
    return "perlArguments";
  }

  @Override
  public LocalizeValue getName() {
    return fragmentName(PerlBundle.message("perl.run.option.perl.arguments"));
  }

  @Override
  public boolean isVisibleByDefault(T target) {
    return !StringUtil.isEmptyOrSpaces(target.getPerlArguments());
  }

  @Override
  public void applyHidden(T target) {
    target.setPerlArguments("");
  }

  @RequiredUIAccess
  @Override
  public RunConfigurationFragmentEditor<T> createEditor(Project project, Disposable parent) {
    TextBoxWithExpandAction text = TextBoxWithExpandAction.create(
      PlatformIconGroup.actionsShow(), PerlBundle.message("perl.run.option.perl.arguments"), ParametersListUtil.DEFAULT_LINE_PARSER, ParametersListUtil.DEFAULT_LINE_JOINER);

    return new RunConfigurationFragmentEditor<>() {
      @RequiredUIAccess
      @Override
      public void addRows(RunConfigurationFragmentLayout layout) {
        layout.addLabeled(labelText(PerlBundle.message("perl.run.option.perl.arguments")), text);
      }

      @RequiredUIAccess
      @Override
      public void reset(T target) {
        text.setValue(StringUtil.notNullize(target.getPerlArguments()), false);
      }

      @RequiredUIAccess
      @Override
      public void apply(T target) {
        target.setPerlArguments(StringUtil.notNullize(text.getValue()));
      }
    };
  }
}
