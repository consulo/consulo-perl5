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

package com.perl5.lang.perl.fileTypes;

import consulo.annotation.component.ExtensionImpl;
import consulo.module.Module;
import consulo.module.event.ModuleListener;
import consulo.project.Project;
import consulo.module.content.layer.event.ModuleRootEvent;
import consulo.module.content.layer.event.ModuleRootListener;
import consulo.project.startup.BackgroundStartupActivity;
import consulo.ui.UIAccess;
import com.perl5.lang.perl.util.PerlPluginUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@ExtensionImpl
public final class PerlFileTypeServiceWatcher implements BackgroundStartupActivity, ModuleListener, ModuleRootListener {
  // TODO consulo: upstream implements Kotlin `suspend fun execute(project: Project)` of ProjectActivity (coroutine API), mapped to BackgroundStartupActivity
  @Override
  public void runActivity(@NotNull Project project, @NotNull UIAccess uiAccess) {
    var connection = project.getMessageBus().connect(PerlPluginUtil.getUnloadAwareDisposable(project));
    connection.subscribe(ModuleRootListener.class, this);
    connection.subscribe(ModuleListener.class, this);
    reset();
  }

  // consulo: Consulo ModuleListener reports added modules one by one (no modulesAdded(Project, List))
  @Override
  public void moduleAdded(@NotNull Project project, @NotNull Module module) {
    reset();
  }

  @Override
  public void modulesRenamed(@NotNull Project project, @NotNull Map<Module, String> modulesWithOldName) {
    reset();
  }

  @Override
  public void rootsChanged(@NotNull ModuleRootEvent event) {
    reset();
  }

  private void reset() {
    PerlFileTypeService.getInstance().reset();
  }
}
