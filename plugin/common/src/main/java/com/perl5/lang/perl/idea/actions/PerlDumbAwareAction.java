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

package com.perl5.lang.perl.idea.actions;

import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.AnActionWithSyncUpdate;
import consulo.ui.ex.action.DumbAwareAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// consulo: update(AnActionEvent) is declared by AnActionWithSyncUpdate
public abstract class PerlDumbAwareAction extends DumbAwareAction implements PerlAction, AnActionWithSyncUpdate {
  public PerlDumbAwareAction() {
  }

  public PerlDumbAwareAction(@Nullable String text) {
    super(text);
  }

  // consulo: AnActionWithSyncUpdate requires update(); IntelliJ AnAction.update() default is a no-op
  @Override
  public void update(@NotNull AnActionEvent e) {
  }
}
