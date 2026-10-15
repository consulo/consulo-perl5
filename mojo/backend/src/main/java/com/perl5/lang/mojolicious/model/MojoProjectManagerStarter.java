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

package com.perl5.lang.mojolicious.model;

import consulo.annotation.component.ExtensionImpl;
import consulo.application.ReadAction;
import consulo.logging.Logger;
import consulo.project.Project;
import consulo.project.startup.BackgroundStartupActivity;
import consulo.ui.UIAccess;
import org.jetbrains.annotations.NotNull;

@ExtensionImpl
public final class MojoProjectManagerStarter implements BackgroundStartupActivity {
  private static final Logger log = Logger.getInstance(MojoProjectManagerStarter.class);

  // TODO consulo: upstream implements Kotlin `suspend fun execute(project: Project)` of ProjectActivity (coroutine API), mapped to BackgroundStartupActivity
  @Override
  public void runActivity(@NotNull Project project, @NotNull UIAccess uiAccess) {
    if (project.isDefault()) {
      return;
    }
    // TODO consulo: upstream uses suspending `readAction { }` (coroutine API), blocking ReadAction.run used instead
    ReadAction.run(() -> {
      if (!project.isDisposed()) {
        log.debug("Project is initialized");
        MojoProjectManager.getInstance(project).scheduleUpdate();
      }
    });
  }
}
