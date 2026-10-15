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

package com.perl5.lang.perl.idea.presentations;

import consulo.annotation.component.ServiceImpl;
import jakarta.inject.Singleton;
import consulo.module.content.ProjectRootManager;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import com.perl5.lang.perl.fileTypes.PerlFileTypePackage;
import com.perl5.lang.perl.util.PerlPackageUtil;
import com.perl5.lang.perl.util.PerlPackageUtilCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@ServiceImpl
@Singleton
public final class PerlBackendItemPresentationService implements PerlItemPresentationService {
  @Override
  public @Nullable String getLocation(@NotNull PsiElement psiElement) {
    PsiFile containingFile = psiElement.getContainingFile();
    if (containingFile == null) {
      return null;
    }

    var locationString = containingFile.getName();
    var virtualFile = containingFile.getVirtualFile();
    if (virtualFile == null) {
      return locationString;
    }

    if (virtualFile.getFileType() == PerlFileTypePackage.INSTANCE) {
      var innerMostClassRoot = PerlPackageUtil.getClosestIncRoot(containingFile.getProject(), virtualFile);

      if (innerMostClassRoot != null) {
        var relativePath = VirtualFileUtil.getRelativePath(virtualFile, innerMostClassRoot);
        return PerlPackageUtilCore.getPackageNameByPath(relativePath);
      }
    }

    // trying to get project's root directory
    var innerMostSourceRoot =
      ProjectRootManager.getInstance(containingFile.getProject()).getFileIndex().getContentRootForFile(virtualFile);
    if (innerMostSourceRoot != null) {
      var relativePath = VirtualFileUtil.getRelativePath(virtualFile, innerMostSourceRoot);
      if (relativePath == null) {
        throw new NullPointerException("Unable to find relative path for " + virtualFile + " in " + innerMostSourceRoot);
      }
      return relativePath;
    }
    else {
      return virtualFile.getPath();
    }
  }
}
