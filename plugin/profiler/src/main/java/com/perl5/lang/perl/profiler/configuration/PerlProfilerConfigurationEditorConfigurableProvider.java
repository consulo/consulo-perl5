/*
 * Copyright 2015-2020 Alexandr Evstigneev
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

import consulo.configurable.Configurable;
import consulo.configurable.ConfigurableProvider;
import consulo.configurable.SearchableConfigurable;
import consulo.execution.profiler.configuration.EditProfilerConfigurationComponent;
import consulo.localize.LocalizeValue;
import com.perl5.lang.perl.profiler.PerlProfilerBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PerlProfilerConfigurationEditorConfigurableProvider extends ConfigurableProvider {
  public static final String PERL_PROFILER_CONFIGURABLE_ID = "profiler.perl5";

  @Override
  public @Nullable Configurable createConfigurable() {
    // consulo: Consulo has no SearchableConfigurable.Delegate, the profiler configurations component is extended directly
    return new PerlProfilerConfigurable();
  }

  /**
   * consulo: upstream anonymous {@code SearchableConfigurable.Delegate} over {@code EditProfilerConfigurationsComponent}.
   * Registered as application configurable under the profiler settings group by {@code PerlProfilerConfigurableRegistration}
   * (upstream {@code <applicationConfigurable parentId="profiler" id="profiler.perl5">} in perl5.plugin.profiler.main.xml)
   */
  public static class PerlProfilerConfigurable extends EditProfilerConfigurationComponent implements SearchableConfigurable {
    public PerlProfilerConfigurable() {
      super(PerlProfilerConfigurationType.PERL_PROFILER_LANGUAGE_SETTINGS_GROUP, null);
    }

    @Override
    public @NotNull String getId() {
      return PERL_PROFILER_CONFIGURABLE_ID;
    }

    // consulo: upstream display name comes from <applicationConfigurable key="profiler.configurable.name">
    @Override
    public @NotNull LocalizeValue getDisplayName() {
      return LocalizeValue.localizeTODO(PerlProfilerBundle.message("profiler.configurable.name"));
    }
  }
}
