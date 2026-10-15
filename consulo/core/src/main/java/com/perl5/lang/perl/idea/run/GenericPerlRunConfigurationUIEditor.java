package com.perl5.lang.perl.idea.run;

import com.perl5.PerlBundle;
import consulo.configurable.ConfigurationException;
import consulo.execution.configuration.ui.SettingsEditor;
import consulo.project.Project;
import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.layout.TabbedLayout;
import org.jspecify.annotations.Nullable;

/**
 * Unified UI version of upstream {@link GenericPerlRunConfigurationEditor} (Swing editors can't be shown in Consulo Web): "General" tab
 * with {@link GenericPerlRunConfigurationLayout} and "Debugging" tab with {@link PerlDebugOptionsLayout}.
 */
public abstract class GenericPerlRunConfigurationUIEditor<Configuration extends GenericPerlRunConfiguration>
  extends SettingsEditor<Configuration> {
  protected final Project myProject;

  private @Nullable GenericPerlRunConfigurationLayout<Configuration> myGeneralLayout;
  private @Nullable PerlDebugOptionsLayout<Configuration> myDebuggingLayout;

  protected GenericPerlRunConfigurationUIEditor(Project project) {
    myProject = project;
  }

  /**
   * @return layout for the "General" tab, {@code this} editor may be used as ui disposable
   */
  @RequiredUIAccess
  protected abstract GenericPerlRunConfigurationLayout<Configuration> createGeneralLayout();

  @RequiredUIAccess
  @Override
  protected Component createUIComponent() {
    GenericPerlRunConfigurationLayout<Configuration> generalLayout = createGeneralLayout();
    generalLayout.build();
    myGeneralLayout = generalLayout;

    PerlDebugOptionsLayout<Configuration> debuggingLayout = new PerlDebugOptionsLayout<>(myProject);
    debuggingLayout.build();
    myDebuggingLayout = debuggingLayout;

    TabbedLayout tabbedLayout = TabbedLayout.create();
    tabbedLayout.addTab(PerlBundle.message("perl.run.option.tab.general"), generalLayout.getComponent());
    tabbedLayout.addTab(PerlBundle.message("perl.run.option.tab.debugging"), debuggingLayout.getComponent());
    return tabbedLayout;
  }

  @RequiredUIAccess
  @Override
  protected void resetEditorFrom(Configuration configuration) {
    GenericPerlRunConfigurationLayout<Configuration> generalLayout = myGeneralLayout;
    if (generalLayout != null) {
      generalLayout.reset(configuration);
    }
    PerlDebugOptionsLayout<Configuration> debuggingLayout = myDebuggingLayout;
    if (debuggingLayout != null) {
      debuggingLayout.reset(configuration);
    }
  }

  @RequiredUIAccess
  @Override
  protected void applyEditorTo(Configuration configuration) throws ConfigurationException {
    GenericPerlRunConfigurationLayout<Configuration> generalLayout = myGeneralLayout;
    if (generalLayout != null) {
      generalLayout.apply(configuration);
    }
    PerlDebugOptionsLayout<Configuration> debuggingLayout = myDebuggingLayout;
    if (debuggingLayout != null) {
      debuggingLayout.apply(configuration);
    }
  }
}
