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

package com.perl5.lang.htmlmason;

import consulo.virtualFileSystem.util.VirtualFileUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.NullVirtualFile;
import consulo.virtualFileSystem.StubVirtualFile;
import consulo.language.psi.PsiFile;
import consulo.language.file.light.LightVirtualFile;
import com.perl5.lang.perl.idea.modules.PerlSourceRootType;
import com.perl5.lang.perl.idea.project.PerlProjectManager;
import consulo.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public final class MasonCoreUtil {
  private MasonCoreUtil() {
  }

  /**
   * consulo: upstream takes the project settings, which provide the project and the components root type
   */
  public static @Nullable VirtualFile getComponentRoot(@NotNull Project project,
                                                       @NotNull PerlSourceRootType rootType,
                                                       @Nullable VirtualFile file) {
    if (file != null) {
      // consulo: FakeVirtualFile (a file which is not created yet) is internal; it's the only platform StubVirtualFile with a parent
      if (file instanceof StubVirtualFile && !(file instanceof NullVirtualFile)) {
        file = file.getParent();
      }

      for (VirtualFile componentRoot : PerlProjectManager.getInstance(project).getModulesRootsOfType(rootType)) {
        if (VirtualFileUtil.isAncestor(componentRoot, file, false)) {
          return componentRoot;
        }
      }
    }
    return null;
  }

  /**
   * Returns a real containing virtual file, not the Light one
   *
   * @return virtual file or null
   */
  public static @Nullable VirtualFile getContainingVirtualFile(@NotNull PsiFile psiFile) {
    VirtualFile originalFile = psiFile.getViewProvider().getVirtualFile();

    if (originalFile instanceof LightVirtualFile lightVirtualFile) {
      if (psiFile.getVirtualFile() != null) {
        originalFile = psiFile.getVirtualFile();
      }
      else if (lightVirtualFile.getOriginalFile() != null) {
        originalFile = lightVirtualFile.getOriginalFile();
      }
    }
    return originalFile instanceof LightVirtualFile || originalFile == null || !originalFile.exists() ? null : originalFile;
  }
}
