package com.perl5.lang.perl.idea.run;

import com.perl5.PerlBundle;
import com.perl5.lang.perl.idea.run.debugger.PerlDebugOptions;
import consulo.disposer.Disposable;
import consulo.execution.configuration.fragment.RunConfigurationFragment;
import consulo.execution.configuration.fragment.RunConfigurationFragmentEditor;
import consulo.execution.configuration.fragment.RunConfigurationFragmentLayout;
import consulo.execution.configuration.fragment.StandardRunConfigurationFragmentOrder;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.util.lang.StringUtil;

import static com.perl5.lang.perl.idea.run.PerlConfigurationUIUtil.fragmentName;

/**
 * Script encoding, debugger startup mode, debugger flags and the initialization code of a configuration started by the debugger.
 */
public class PerlDebugOptionsFragment<T extends PerlDebugOptions> implements RunConfigurationFragment<T> {
  private static final String DEFAULT_SCRIPT_CHARSET = "utf8";
  private static final String DEFAULT_START_MODE = "RUN";

  @Override
  public String getId() {
    return "perlDebugOptions";
  }

  @Override
  public LocalizeValue getName() {
    return fragmentName(PerlBundle.message("perl.run.option.tab.debugging"));
  }

  @Override
  public int getOrder() {
    return StandardRunConfigurationFragmentOrder.LOGS - 10;
  }

  @Override
  public boolean isVisibleByDefault(T target) {
    return !DEFAULT_SCRIPT_CHARSET.equals(target.getScriptCharset()) ||
           !DEFAULT_START_MODE.equals(target.getStartMode()) ||
           target.isNonInteractiveModeEnabled() ||
           target.isCompileTimeBreakpointsEnabled() ||
           !StringUtil.isEmptyOrSpaces(target.getInitCode());
  }

  @Override
  public void applyHidden(T target) {
    target.setScriptCharset(DEFAULT_SCRIPT_CHARSET);
    target.setStartMode(DEFAULT_START_MODE);
    target.setNonInteractiveModeEnabled(false);
    target.setCompileTimeBreakpointsEnabled(false);
    target.setInitCode("");
  }

  @RequiredUIAccess
  @Override
  public RunConfigurationFragmentEditor<T> createEditor(Project project, Disposable parent) {
    PerlDebugOptionsLayout<T> options = new PerlDebugOptionsLayout<>(project);
    options.build();

    return new RunConfigurationFragmentEditor<>() {
      @RequiredUIAccess
      @Override
      public void addRows(RunConfigurationFragmentLayout layout) {
        layout.addBottom(options.getComponent());
      }

      @RequiredUIAccess
      @Override
      public void reset(T target) {
        options.reset(target);
      }

      @RequiredUIAccess
      @Override
      public void apply(T target) {
        options.apply(target);
      }
    };
  }
}
