/*
 * Copyright 2015-2022 Alexandr Evstigneev
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

package com.perl5.lang.perl.profiler.parser;

import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.execution.profiler.ProfilerDumpFileParser;
import consulo.execution.profiler.ProfilerDumpParserProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// consulo: upstream <profiler.api.ProfilerDumpParserProvider> registration in perl5.plugin.profiler.main.xml
@ExtensionImpl
public class PerlProfilerCollapsedDumpFileParserProvider implements ProfilerDumpParserProvider {
  @Override
  public @NotNull String getId() {
    return "perl5.dump.parser.collapsed";
  }

  @Override
  public @NotNull LocalizeValue getName() {
    return LocalizeValue.of("NYTProf Dump Backup");
  }

  @Override
  public @Nullable String getRequiredFileExtension() {
    return "gz";
  }

  @Override
  public @NotNull ProfilerDumpFileParser createParser(@NotNull Project project) {
    return new PerlProfilerCollapsedDumpFileParser();
  }
}
