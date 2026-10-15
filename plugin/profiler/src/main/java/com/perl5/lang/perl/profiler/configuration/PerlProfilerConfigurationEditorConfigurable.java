/*
 * Copyright 2015-2025 Alexandr Evstigneev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.perl5.lang.perl.profiler.configuration;

import consulo.configurable.ConfigurationException;
import consulo.configurable.UnnamedConfigurable;
import consulo.util.lang.lazy.ClearableLazyValue;
import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.awtUnsafe.TargetAWT;
import com.perl5.lang.perl.profiler.PerlProfilerBundle;
import com.perl5.lang.perl.profiler.run.PerlProfilerStartupMode;
import consulo.disposer.Disposable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

class PerlProfilerConfigurationEditorConfigurable implements UnnamedConfigurable {
  private final ClearableLazyValue<PerlProfilerConfigurationEditor> myEditorProvider =
    ClearableLazyValue.notNull(PerlProfilerConfigurationEditor::new);
  private final @NotNull PerlProfilerConfigurationState myState;

  public PerlProfilerConfigurationEditorConfigurable(@NotNull PerlProfilerConfigurationState state) {
    myState = state;
  }

  // consulo: the Swing form wrapped into uni UI component, not used in Consulo: PerlProfilerConfigurationType creates unified UI
  //  PerlProfilerConfigurationUIConfigurable instead, kept for easier upstream merges
  @RequiredUIAccess
  @Override
  public @Nullable Component createUIComponent(Disposable parentDisposable) {
    return TargetAWT.wrap(getEditor().myPanel);
  }

  private @NotNull PerlProfilerConfigurationEditor getEditor() {
    return myEditorProvider.get();
  }

  @Override
  public boolean isModified() {
    var editor = getEditor();
    return myState.isOptimizerDisabled() != editor.myOptimizerCheckBox.isSelected() ||
           myState.getStartupMode() != editor.myStartModeComboBox.getSelectedItem();
  }

  @Override
  public void apply() throws ConfigurationException {
    var editor = getEditor();
    var startMode = editor.myStartModeComboBox.getSelectedItem();
    if (startMode == null) {
      throw new ConfigurationException(PerlProfilerBundle.message("dialog.message.please.select.startup.mode"));
    }
    myState.setStartupMode((PerlProfilerStartupMode)startMode);
    myState.setOptimizerDisabled(editor.myOptimizerCheckBox.isSelected());
  }

  @Override
  public void reset() {
    var editor = getEditor();
    editor.myStartModeComboBox.setSelectedItem(myState.getStartupMode());
    editor.myOptimizerCheckBox.setSelected(myState.isOptimizerDisabled());
  }
}
