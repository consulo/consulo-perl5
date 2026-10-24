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

package com.perl5.lang.perl.idea.run.run;

import consulo.execution.configuration.ConfigurationFactory;
import consulo.execution.configuration.LegacyRunConfigurationConverter;
import consulo.execution.configuration.RunConfigurationOptions;
import consulo.execution.configuration.fragment.RunConfigurationFragment;
import consulo.project.Project;
import com.perl5.lang.perl.idea.run.GenericPerlRunConfiguration;
import com.perl5.lang.perl.idea.run.GenericPerlRunConfigurationOptions;
import com.perl5.lang.perl.idea.run.PerlScriptFragment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.ArrayList;
import java.util.List;

@VisibleForTesting
public class PerlRunConfiguration extends GenericPerlRunConfiguration {
  public PerlRunConfiguration(Project project,
                              @NotNull ConfigurationFactory factory,
                              String name) {
    super(project, factory, name);
  }

  // consulo: settings are edited with run configuration fragments
  @Override
  protected Class<? extends RunConfigurationOptions> getOptionsClass() {
    return GenericPerlRunConfigurationOptions.class;
  }

  @Override
  protected void convertLegacyState(LegacyRunConfigurationConverter converter) {
    super.convertLegacyState(converter);

    GenericPerlRunConfigurationOptions options = new GenericPerlRunConfigurationOptions();
    convertLegacyCommonState(converter, options);
    converter.setOptions(options);
  }

  @Override
  public List<RunConfigurationFragment<?>> createFragments() {
    List<RunConfigurationFragment<?>> fragments = new ArrayList<>();
    fragments.add(new PerlScriptFragment<PerlRunConfiguration>(PerlRunConfigurationProducer.getInstance()));
    fragments.addAll(createCommonFragments());
    return fragments;
  }
}
