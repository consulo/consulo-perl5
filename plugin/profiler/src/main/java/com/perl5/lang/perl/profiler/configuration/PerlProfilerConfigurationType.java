/*
 * Copyright 2015-2026 Alexandr Evstigneev
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

import consulo.annotation.component.ExtensionImpl;
import consulo.configurable.UnnamedConfigurable;
import consulo.execution.profiler.configuration.ProfilerAttacher;
import consulo.execution.profiler.configuration.ProfilerConfigurationTypeBase;
import consulo.execution.profiler.configuration.ProfilerStarter;
import consulo.execution.profiler.icon.ExecutionProfilerIconGroup;
import consulo.localize.LocalizeValue;
import consulo.ui.image.Image;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// consulo: upstream <profiler.profilerConfigurationType> registration in perl5.plugin.profiler.main.xml
@ExtensionImpl
public class PerlProfilerConfigurationType extends ProfilerConfigurationTypeBase<PerlProfilerConfigurationState> {
  static final @NonNls @NotNull String ID = "com.perl5.profiler.nytprof";
  static final @NonNls String PERL_PROFILER_LANGUAGE_SETTINGS_GROUP = "profiler.perl5.group";
  private static final String CONFIGURATION_TYPE_NAME = "NYTProf";

  @Override
  public @NotNull LocalizeValue getDisplayName() {
    return LocalizeValue.of(CONFIGURATION_TYPE_NAME);
  }

  @Override
  public @Nullable String getHelpTopic() {
    return null;
  }

  @Override
  public @NotNull Image getIcon() {
    return ExecutionProfilerIconGroup.profile();
  }

  @Override
  public @NotNull String getId() {
    return ID;
  }

  @Override
  public @NotNull String getLanguageSettingsGroup() {
    return PERL_PROFILER_LANGUAGE_SETTINGS_GROUP;
  }

  @Override
  public @Nullable ProfilerAttacher createAttacher(@NotNull PerlProfilerConfigurationState state) {
    return null;
  }

  @Override
  public @NotNull UnnamedConfigurable createConfigurable(@NotNull PerlProfilerConfigurationState state) {
    // consulo: unified UI configurable, upstream Swing PerlProfilerConfigurationEditorConfigurable can't be shown in Consulo Web
    return new PerlProfilerConfigurationUIConfigurable(state);
  }

  @Override
  public @Nullable ProfilerStarter createStarter(@NotNull PerlProfilerConfigurationState profilerConfigurationState) {
    return new PerlProfilerStarter();
  }

  @Override
  public @NotNull PerlProfilerConfigurationState getTemplateState() {
    var state = new PerlProfilerConfigurationState();
    state.setDisplayName(CONFIGURATION_TYPE_NAME);
    return state;
  }
}
