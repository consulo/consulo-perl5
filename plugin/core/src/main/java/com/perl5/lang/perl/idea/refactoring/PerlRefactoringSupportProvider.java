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

package com.perl5.lang.perl.idea.refactoring;

import com.perl5.lang.perl.PerlLanguage;
import com.perl5.lang.perl.extensions.PerlRenameUsagesHelper;
import com.perl5.lang.perl.idea.refactoring.introduce.PerlIntroduceVariableHandler;
import com.perl5.lang.perl.parser.Exception.Class.ide.refactoring.PerlRenamingVetoCondition;
import com.perl5.lang.perl.psi.PerlNamespaceDefinition;
import com.perl5.lang.perl.psi.PerlNamespaceElement;
import com.perl5.lang.perl.psi.PerlStringContentElement;
import consulo.content.scope.SearchScope;
import consulo.language.editor.refactoring.RefactoringSupportProvider;
import consulo.language.editor.refactoring.action.RefactoringActionHandler;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiNameIdentifierOwner;
import consulo.language.psi.scope.LocalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class is responsible for controlling refactoring process
 */
public class PerlRefactoringSupportProvider extends RefactoringSupportProvider {
  // todo RenameInputValidator
  @Override
  public boolean isInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context) {
    SearchScope useScope = element.getUseScope();
    return useScope instanceof LocalSearchScope
           && element instanceof PsiNameIdentifierOwner
           && !(element instanceof PerlRenameUsagesHelper)
           && isInplaceAllowed(element, context)
           && !(((PsiNameIdentifierOwner)element).getNameIdentifier() instanceof PerlStringContentElement)
           && element.getContainingFile().getLanguage() == PerlLanguage.INSTANCE
           && !(((LocalSearchScope)useScope).getScope()[0] instanceof PsiFile)
      ;
  }

  public boolean isPerlInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context) {
    if (!isInplaceAllowed(element, context)) {
      return false;
    }
    else if (element instanceof PerlRenameUsagesHelper renameUsagesHelper) {
      return renameUsagesHelper.isInplaceRefactoringAllowed();
    }
    return true;
  }

  /**
   * Common logic for any inplace, platform or ours
   */
  private static boolean isInplaceAllowed(@NotNull PsiElement element, PsiElement context) {
    if (PerlRenamingVetoCondition.isVetoedImpl(element)) {
      return false;
    }
    else {
      return !(element instanceof PerlNamespaceDefinition) ||
             !(context instanceof PerlNamespaceElement perlNamespaceElement) ||
             !perlNamespaceElement.isTag();
    }
  }

  @Override
  public boolean isMemberInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context) {
    return false;
  }

  @Override
  public @Nullable RefactoringActionHandler getIntroduceVariableHandler() {
    return new PerlIntroduceVariableHandler();
  }
}
