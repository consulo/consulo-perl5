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

package com.perl5.lang.perl.idea.execution.filters;

import consulo.execution.ui.console.ConsoleDependentFilterProvider;
import consulo.execution.ui.console.Filter;
import consulo.execution.ui.console.ConsoleView;
import consulo.project.Project;
import consulo.content.scope.SearchScope;
import com.perl5.lang.perl.idea.sdk.host.PerlHostDataProvider;
import org.jetbrains.annotations.NotNull;

public class PerlConsoleFilterProvider extends ConsoleDependentFilterProvider {

  @Override
  public Filter @NotNull [] getDefaultFilters(@NotNull ConsoleView consoleView,
                                              @NotNull Project project,
                                              @NotNull SearchScope scope) {
    return consoleView instanceof PerlHostDataProvider hostDataProvider ? createFilters(project, hostDataProvider) : Filter.EMPTY_ARRAY;
  }

  private Filter @NotNull [] createFilters(@NotNull Project project, PerlHostDataProvider hostDataProvider) {
    return new Filter[]{
      new PerlConsoleFileLinkFilter(project, hostDataProvider),
      new PerlAbsolutePathConsoleFilter(project, hostDataProvider)
    };
  }
}
