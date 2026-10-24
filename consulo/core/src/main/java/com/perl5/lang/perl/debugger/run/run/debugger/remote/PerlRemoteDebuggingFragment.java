package com.perl5.lang.perl.debugger.run.run.debugger.remote;

import com.perl5.lang.perl.debugger.PerlDebuggerBundle;
import consulo.disposer.Disposable;
import consulo.execution.configuration.fragment.RunConfigurationFragment;
import consulo.execution.configuration.fragment.RunConfigurationFragmentEditor;
import consulo.execution.configuration.fragment.RunConfigurationFragmentLayout;
import consulo.execution.configuration.fragment.StandardRunConfigurationFragmentOrder;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;

import static com.perl5.lang.perl.idea.run.PerlConfigurationUIUtil.fragmentName;

public class PerlRemoteDebuggingFragment implements RunConfigurationFragment<PerlRemoteDebuggingConfiguration> {
  @Override
  public String getId() {
    return "perlRemoteDebugging";
  }

  @Override
  public LocalizeValue getName() {
    return fragmentName(PerlDebuggerBundle.message("perl.run.option.debugger.connection.mode"));
  }

  @Override
  public int getOrder() {
    return StandardRunConfigurationFragmentOrder.FIRST;
  }

  @Override
  public boolean isRemovable() {
    return false;
  }

  @RequiredUIAccess
  @Override
  public RunConfigurationFragmentEditor<PerlRemoteDebuggingConfiguration> createEditor(Project project, Disposable parent) {
    PerlRemoteDebuggingLayout remoteDebugging = new PerlRemoteDebuggingLayout(project, parent);
    remoteDebugging.build();

    return new RunConfigurationFragmentEditor<>() {
      @RequiredUIAccess
      @Override
      public void addRows(RunConfigurationFragmentLayout layout) {
        layout.addBottom(remoteDebugging.getComponent());
      }

      @RequiredUIAccess
      @Override
      public void reset(PerlRemoteDebuggingConfiguration target) {
        remoteDebugging.reset(target);
      }

      @RequiredUIAccess
      @Override
      public void apply(PerlRemoteDebuggingConfiguration target) {
        remoteDebugging.apply(target);
      }
    };
  }
}
