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

package com.perl5.lang.tt2.idea.settings;

import com.perl5.lang.perl.idea.project.PerlProjectManager;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.application.util.AtomicNotNullLazyValue;
import consulo.disposer.Disposable;
import consulo.language.psi.PsiDirectory;
import consulo.language.psi.PsiFileSystemItem;
import consulo.language.psi.PsiManager;
import consulo.module.content.layer.event.ModuleRootEvent;
import consulo.module.content.layer.event.ModuleRootListener;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;

/**
 * Template Toolkit templates roots of the project.
 * <p>
 * consulo: upstream also caches file name matchers of the substituted extensions of the project settings and is reset by
 * {@code TT2_SETTINGS_TOPIC}. In Consulo the settings are per module ({@link TemplateToolkitSettings} has the matchers) and templates
 * roots are module content folders, so the cache is reset on roots changes (which include module extensions changes).
 */
@ServiceAPI(ComponentScope.PROJECT)
@ServiceImpl
@Singleton
public final class TemplateToolkitSettingsBackendHelper implements Disposable {
  private volatile AtomicNotNullLazyValue<Collection<PsiFileSystemItem>> myLazyPsiDirsRoots;
  private final @NotNull Project myProject;

  @Inject
  public TemplateToolkitSettingsBackendHelper(@NotNull Project project) {
    myProject = project;
    createLazyObjects();
    myProject.getMessageBus().connect(this).subscribe(ModuleRootListener.class, new ModuleRootListener() {
      @Override
      public void rootsChanged(ModuleRootEvent event) {
        createLazyObjects();
      }
    });
  }

  @Override
  public void dispose() {
  }

  private void createLazyObjects() {
    myLazyPsiDirsRoots = AtomicNotNullLazyValue.createValue(() -> {
      Collection<PsiFileSystemItem> result = new ArrayDeque<>();

      PsiManager psiManager = PsiManager.getInstance(myProject);
      for (VirtualFile virtualFile : getTemplateRoots()) {
        PsiDirectory directory = psiManager.findDirectory(virtualFile);
        if (directory != null) {
          result.add(directory);
        }
      }
      return result;
    });
  }

  public @NotNull Collection<PsiFileSystemItem> getTemplatePsiRoots() {
    return myLazyPsiDirsRoots.getValue();
  }

  /**
   * @return templates roots of the modules with the Template Toolkit module extension enabled
   */
  public @NotNull List<VirtualFile> getTemplateRoots() {
    return PerlProjectManager.getInstance(myProject).getModulesRootsOfType(TemplateToolkitSourceRootType.INSTANCE);
  }

  /**
   * Checks if virtualFile is under configured root
   *
   * @return true or false
   */
  public boolean isVirtualFileUnderRoot(@NotNull VirtualFile file) {
    for (VirtualFile root : getTemplateRoots()) {
      if (VirtualFileUtil.isAncestor(root, file, true)) {
        return true;
      }
    }
    return false;
  }

  public static @NotNull TemplateToolkitSettingsBackendHelper getInstance(@NotNull Project project) {
    return project.getService(TemplateToolkitSettingsBackendHelper.class);
  }
}
