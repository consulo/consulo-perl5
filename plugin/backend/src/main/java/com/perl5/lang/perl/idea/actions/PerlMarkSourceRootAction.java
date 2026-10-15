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

package com.perl5.lang.perl.idea.actions;

import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.Presentation;
import consulo.application.Application;
import consulo.application.ReadAction;
import consulo.module.Module;
import consulo.language.util.ModuleUtilCore;
import consulo.project.Project;
import consulo.project.ProjectBundle;
import com.intellij.openapi.projectRoots.impl.PerlModuleExtension;
import com.perl5.lang.perl.idea.configuration.settings.sdk.PerlSourceRootEditHandler;
import consulo.virtualFileSystem.VirtualFile;
import consulo.application.util.concurrent.AppExecutorUtil;
import com.perl5.lang.perl.idea.modules.PerlSourceRootType;
import com.perl5.lang.perl.idea.project.PerlProjectManager;
import com.perl5.lang.perl.util.PerlPluginUtil;
import consulo.perl5.core.module.PerlFrameworkContentFolderSupportPatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public abstract class PerlMarkSourceRootAction extends PerlSourceRootAction {
  private final @NotNull PerlSourceRootType myType;

  public PerlMarkSourceRootAction(@NotNull PerlSourceRootType type) {
    myType = type;
    PerlSourceRootEditHandler editHandler = type.getEditHandler();
    Presentation presentation = getTemplatePresentation();
    presentation.setText(editHandler.getMarkRootButtonText());
    presentation.setDescription(ProjectBundle.message("module.toggle.sources.action.description",
                                                      editHandler.getFullRootTypeName().toLowerCase(Locale.getDefault())));
    presentation.setIcon(editHandler.getRootIcon());
  }


  @Override
  protected boolean isEnabled(@NotNull List<? extends VirtualFile> files, @NotNull Module module) {
    if (files.isEmpty() || !PerlProjectManager.isPerlEnabled(module)) {
      return false;
    }
    // consulo: framework roots (templates, components) may be marked only with the framework module extension enabled for the module
    if (!PerlFrameworkContentFolderSupportPatcher.isRootTypeEnabled(module, myType)) {
      return false;
    }
    return !myType.equals(PerlModuleExtension.getInstance(module).getRootType(files.getFirst()));
  }

  @Override
  protected final void modifyRoots(@NotNull AnActionEvent e, @NotNull Module module, VirtualFile @NotNull [] files) {
    markRoot(module, files);
  }

  public final void markRoot(@NotNull Project project, @Nullable VirtualFile virtualFile) {
    if (virtualFile == null) {
      return;
    }
    ReadAction.nonBlocking(() -> {
        if (!virtualFile.isValid()) {
          return null;
        }
        return ModuleUtilCore.findModuleForFile(virtualFile, project);
      }).finishOnUiThread(Application::getCurrentModalityState, (module) -> {
        // consulo: upstream checks isUnloaded without negation, so nothing is ever marked
        if (module != null && !PerlPluginUtil.isUnloaded(module.getProject())) {
          markRoot(module, virtualFile);
        }
      }).expireWith(PerlPluginUtil.getUnloadAwareDisposable(project))
      .coalesceBy(project, virtualFile)
      .submit(AppExecutorUtil.getAppExecutorService());
  }

  public final void markRoot(@NotNull Module module, @NotNull VirtualFile... files) {
    markRoot(module, Arrays.asList(files));
  }
  public final void markRoot(@NotNull Module module, @NotNull List<? extends VirtualFile> files) {
    PerlModuleExtension.modify(module, it -> {
      for (VirtualFile virtualFile : files) {
        it.setRoot(virtualFile, myType);
      }
    });
  }
}
