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

package com.perl5.lang.perl.idea.refactoring.move;

import consulo.application.ApplicationManager;
import consulo.project.Project;
import consulo.util.dataholder.Key;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.language.psi.PsiDirectory;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiReference;
import consulo.language.psi.search.ReferencesSearch;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.language.editor.refactoring.RefactoringFactory;
import consulo.language.editor.refactoring.RenameRefactoring;
import consulo.language.editor.refactoring.move.MoveFileHandler;
import consulo.usage.UsageInfo;
import consulo.language.util.IncorrectOperationException;
import com.perl5.lang.perl.fileTypes.PerlFileTypePackage;
import com.perl5.lang.perl.psi.PerlNamespaceDefinitionElement;
import com.perl5.lang.perl.psi.impl.PerlFileImpl;
import com.perl5.lang.perl.psi.utils.PerlPsiUtil;
import com.perl5.lang.perl.util.PerlPackageUtil;
import com.perl5.lang.perl.util.PerlPackageUtilCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;

public class PerlMoveFileHandler extends MoveFileHandler {
  private static final Key<String> ORIGINAL_PACKAGE_NAME = Key.create("PERL_ORIGINAL_PACKAGE_NAME");

  @Override
  public boolean canProcessElement(PsiFile element) {
    return element instanceof PerlFileImpl && element.getVirtualFile().getFileType() == PerlFileTypePackage.INSTANCE;
  }

  @Override
  public void prepareMovedFile(PsiFile file, PsiDirectory moveDestination, Map<PsiElement, PsiElement> oldToNewMap) {
    file.putUserData(ORIGINAL_PACKAGE_NAME, ((PerlFileImpl)file).getFilePackageName());

    String newFilePath = moveDestination.getVirtualFile().getPath() + '/' + file.getName();
    VirtualFile newClassRoot = PerlPackageUtil.getClosestIncRoot(moveDestination.getProject(), newFilePath);

    if (newClassRoot != null) {
      String newRelativePath = newFilePath.substring(newClassRoot.getPath().length());
      String newPackageName = PerlPackageUtilCore.getPackageNameByPath(newRelativePath);

      if (newPackageName != null) {
        for (PsiReference reference : ReferencesSearch.search(file, file.getUseScope()).findAll()) {
          PerlPsiUtil.renameElement(reference.getElement(), newPackageName);
        }
      }
    }
  }

  @Override
  public void updateMovedFile(PsiFile file) throws IncorrectOperationException {
    String originalPackageName = file.getUserData(ORIGINAL_PACKAGE_NAME);
    Project project = file.getProject();
    VirtualFile virtualFile = file.getVirtualFile();
    VirtualFile newInnermostRoot = PerlPackageUtil.getClosestIncRoot(project, virtualFile);

    if (newInnermostRoot != null && originalPackageName != null) {
      String newRelativePath = VirtualFileUtil.getRelativePath(virtualFile, newInnermostRoot);
      String newPackageName = PerlPackageUtilCore.getPackageNameByPath(newRelativePath);

      final RenameRefactoring[] refactoring = {null};

      for (PerlNamespaceDefinitionElement namespaceDefinition : PsiTreeUtil
        .findChildrenOfType(file, PerlNamespaceDefinitionElement.class)) {
        if (originalPackageName.equals(namespaceDefinition.getNamespaceName())) {
          if (refactoring[0] == null) {
            refactoring[0] = RefactoringFactory.getInstance(file.getProject()).createRename(namespaceDefinition, newPackageName);
          }
          else {
            refactoring[0].addElement(namespaceDefinition, newPackageName);
          }
        }
      }

      if (refactoring[0] != null) {
        ApplicationManager.getApplication().invokeLater(refactoring[0]::run);
      }
    }
  }

  @Override
  public @Nullable List<UsageInfo> findUsages(@NotNull PsiFile psiFile,
                                              @NotNull PsiDirectory newParent,
                                              boolean searchInComments,
                                              boolean searchInNonJavaFiles) {
    return null;
  }

  @Override
  public void retargetUsages(@Unmodifiable @NotNull List<UsageInfo> usageInfos,
                             @NotNull Map<PsiElement, PsiElement> oldToNewMap) {

  }
}
