package com.perl5.lang.perl.idea.run.prove;

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

public class PerlTestScriptArgumentsFragment implements RunConfigurationFragment<PerlAbstractTestRunConfiguration> {
  @Override
  public String getId() {
    return "perlTestScriptArguments";
  }

  @Override
  public LocalizeValue getName() {
    return fragmentName(PerlBundle.message("perl.run.option.test.script.arguments"));
  }

  @Override
  public boolean isVisibleByDefault(PerlAbstractTestRunConfiguration target) {
    return !StringUtil.isEmptyOrSpaces(target.getTestScriptParameters());
  }

  @Override
  public void applyHidden(PerlAbstractTestRunConfiguration target) {
    target.setTestScriptParameters("");
  }

  @RequiredUIAccess
  @Override
  public RunConfigurationFragmentEditor<PerlAbstractTestRunConfiguration> createEditor(Project project, Disposable parent) {
    String label = PerlBundle.message("perl.run.option.test.script.arguments");
    TextBoxWithExpandAction arguments = TextBoxWithExpandAction.create(
      PlatformIconGroup.actionsShow(), label, ParametersListUtil.DEFAULT_LINE_PARSER, ParametersListUtil.DEFAULT_LINE_JOINER);

    return new RunConfigurationFragmentEditor<>() {
      @RequiredUIAccess
      @Override
      public void addRows(RunConfigurationFragmentLayout layout) {
        layout.addLabeled(labelText(label), arguments);
      }

      @RequiredUIAccess
      @Override
      public void reset(PerlAbstractTestRunConfiguration target) {
        arguments.setValue(StringUtil.notNullize(target.getTestScriptParameters()), false);
      }

      @RequiredUIAccess
      @Override
      public void apply(PerlAbstractTestRunConfiguration target) {
        target.setTestScriptParameters(arguments.getValue());
      }
    };
  }
}
