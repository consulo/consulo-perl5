/*
 * Copyright 2015-2024 Alexandr Evstigneev
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

import com.perl5.lang.perl.fileTypes.PerlFileTypePackage;
import com.perl5.lang.perl.util.PerlPackageUtil;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.module.content.internal.ProjectRootManagerEx;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import org.jetbrains.annotations.Nullable;

public class PerlItemPresentationSimpleDynamicLocation extends PerlItemPresentationSimple {

  public PerlItemPresentationSimpleDynamicLocation(PsiElement element, String presentableText) {
    super(element, presentableText);
  }

  @Override
  public @Nullable String getLocationString() {
    PsiFile containingFile = getElement().getContainingFile();

    if (containingFile == null) {
      return null;
    }

    String locationString = containingFile.getName();
    VirtualFile virtualFile = containingFile.getVirtualFile();

    if (virtualFile == null) {
      return locationString;
    }

    if (virtualFile.getFileType() == PerlFileTypePackage.INSTANCE) {
      VirtualFile innerMostClassRoot = PerlPackageUtil.getClosestIncRoot(containingFile.getProject(), virtualFile);

      if (innerMostClassRoot != null) {
        String relativePath = VirtualFileUtil.getRelativePath(virtualFile, innerMostClassRoot);
        return PerlPackageUtil.getPackageNameByPath(relativePath);
      }
    }

    // trying to get project's root directory
    VirtualFile innerMostSourceRoot = ProjectRootManagerEx.getInstanceEx(containingFile.getProject()).getFileIndex().getContentRootForFile(virtualFile);
    if (innerMostSourceRoot != null) {
      return VirtualFileUtil.getRelativePath(virtualFile, innerMostSourceRoot);
    } else {
      return virtualFile.getPath();
    }
  }
}
