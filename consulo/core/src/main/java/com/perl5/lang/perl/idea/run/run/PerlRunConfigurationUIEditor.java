package com.perl5.lang.perl.idea.run.run;

import com.perl5.lang.perl.idea.run.GenericPerlRunConfigurationLayout;
import com.perl5.lang.perl.idea.run.GenericPerlRunConfigurationProducer;
import com.perl5.lang.perl.idea.run.GenericPerlRunConfigurationUIEditor;
import consulo.disposer.Disposable;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;

/**
 * Unified UI version of upstream {@link PerlRunConfigurationEditor}
 */
class PerlRunConfigurationUIEditor extends GenericPerlRunConfigurationUIEditor<PerlRunConfiguration> {
  PerlRunConfigurationUIEditor(Project project) {
    super(project);
  }

  @RequiredUIAccess
  @Override
  protected GenericPerlRunConfigurationLayout<PerlRunConfiguration> createGeneralLayout() {
    return new ParametersLayout(myProject, this);
  }

  private static class ParametersLayout extends GenericPerlRunConfigurationLayout<PerlRunConfiguration> {
    @RequiredUIAccess
    ParametersLayout(Project project, Disposable uiDisposable) {
      super(project, uiDisposable);
    }

    @Override
    protected GenericPerlRunConfigurationProducer<PerlRunConfiguration> getRunConfigurationProducer() {
      return PerlRunConfigurationProducer.getInstance();
    }
  }
}
