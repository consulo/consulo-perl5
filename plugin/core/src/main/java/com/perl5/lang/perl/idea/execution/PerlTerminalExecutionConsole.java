/*
 * Copyright 2015-2023 Alexandr Evstigneev
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

package com.perl5.lang.perl.idea.execution;

import com.perl5.lang.perl.idea.sdk.host.PerlConsoleView;
import com.perl5.lang.perl.idea.sdk.host.PerlHostData;
import consulo.application.ReadAction;
import consulo.application.util.concurrent.AppExecutorUtil;
import consulo.execution.ui.console.ConsoleView;
import consulo.execution.ui.console.ConsoleViewUtil;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.project.Project;
import consulo.ui.ModalityState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PerlTerminalExecutionConsole extends TerminalExecutionConsole implements PerlConsoleView {
  private @Nullable PerlHostData<?, ?> myHostData;

  public PerlTerminalExecutionConsole(@NotNull Project project) {
    super(project, null);
    updatePredefinedFiltersLater(project, this);
  }

  @Override
  public @Nullable PerlHostData<?, ?> getHostData() {
    return myHostData;
  }

  @Override
  public @NotNull PerlTerminalExecutionConsole withHostData(@Nullable PerlHostData<?, ?> hostData) {
    myHostData = hostData;
    return this;
  }

  /**
   * Copy-paste of {@link ConsoleViewImpl#updatePredefinedFiltersLater()}
   */
  public static void updatePredefinedFiltersLater(@NotNull Project project, @NotNull ConsoleView consoleView) {
    ReadAction
      .nonBlocking(() -> ConsoleViewUtil.computeConsoleFilters(project, consoleView, GlobalSearchScope.allScope(project)))
      .expireWith(consoleView)
      .finishOnUiThread(
        ModalityState.stateForComponent(consoleView.getComponent()), filters -> filters.forEach(consoleView::addMessageFilter)
      ).submit(AppExecutorUtil.getAppExecutorService());
  }
}
