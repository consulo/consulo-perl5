package com.perl5.lang.perl.profiler.configuration;

import com.perl5.lang.perl.profiler.PerlProfilerBundle;
import com.perl5.lang.perl.profiler.run.PerlProfilerStartupMode;
import consulo.configurable.ConfigurationException;
import consulo.configurable.UnnamedConfigurable;
import consulo.disposer.Disposable;
import consulo.localize.LocalizeValue;
import consulo.ui.CheckBox;
import consulo.ui.ComboBox;
import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.util.FormBuilder;
import org.jspecify.annotations.Nullable;

/**
 * Unified UI version of upstream {@link PerlProfilerConfigurationEditorConfigurable} with {@link PerlProfilerConfigurationEditor} form
 */
class PerlProfilerConfigurationUIConfigurable implements UnnamedConfigurable {
  private final PerlProfilerConfigurationState myState;

  private @Nullable ComboBox<PerlProfilerStartupMode> myStartModeComboBox;
  private @Nullable CheckBox myOptimizerCheckBox;

  PerlProfilerConfigurationUIConfigurable(PerlProfilerConfigurationState state) {
    myState = state;
  }

  @RequiredUIAccess
  @Override
  public @Nullable Component createUIComponent(Disposable parentDisposable) {
    ComboBox<PerlProfilerStartupMode> startModeComboBox = ComboBox.create(PerlProfilerStartupMode.values());
    startModeComboBox.setTextRenderer(mode -> mode == null ? LocalizeValue.empty() : LocalizeValue.of(mode.getExplanation()));
    CheckBox optimizerCheckBox = CheckBox.create(LocalizeValue.empty());

    myStartModeComboBox = startModeComboBox;
    myOptimizerCheckBox = optimizerCheckBox;

    return FormBuilder.create()
      .addLabeled(LocalizeValue.of(PerlProfilerBundle.message("label.profiler.startup.mode")), startModeComboBox)
      .addLabeled(LocalizeValue.of(PerlProfilerBundle.message("label.disable.perl.optimizer")), optimizerCheckBox)
      .build();
  }

  @RequiredUIAccess
  @Override
  public boolean isModified() {
    ComboBox<PerlProfilerStartupMode> startModeComboBox = myStartModeComboBox;
    CheckBox optimizerCheckBox = myOptimizerCheckBox;
    if (startModeComboBox == null || optimizerCheckBox == null) {
      return false;
    }
    return myState.isOptimizerDisabled() != Boolean.TRUE.equals(optimizerCheckBox.getValue()) ||
           myState.getStartupMode() != startModeComboBox.getValue();
  }

  @RequiredUIAccess
  @Override
  public void apply() throws ConfigurationException {
    ComboBox<PerlProfilerStartupMode> startModeComboBox = myStartModeComboBox;
    CheckBox optimizerCheckBox = myOptimizerCheckBox;
    if (startModeComboBox == null || optimizerCheckBox == null) {
      return;
    }
    PerlProfilerStartupMode startMode = startModeComboBox.getValue();
    if (startMode == null) {
      throw new ConfigurationException(LocalizeValue.of(PerlProfilerBundle.message("dialog.message.please.select.startup.mode")));
    }
    myState.setStartupMode(startMode);
    myState.setOptimizerDisabled(Boolean.TRUE.equals(optimizerCheckBox.getValue()));
  }

  @RequiredUIAccess
  @Override
  public void reset() {
    ComboBox<PerlProfilerStartupMode> startModeComboBox = myStartModeComboBox;
    CheckBox optimizerCheckBox = myOptimizerCheckBox;
    if (startModeComboBox == null || optimizerCheckBox == null) {
      return;
    }
    startModeComboBox.setValue(myState.getStartupMode());
    optimizerCheckBox.setValue(myState.isOptimizerDisabled());
  }

  @RequiredUIAccess
  @Override
  public void disposeUIResources() {
    myStartModeComboBox = null;
    myOptimizerCheckBox = null;
  }
}
