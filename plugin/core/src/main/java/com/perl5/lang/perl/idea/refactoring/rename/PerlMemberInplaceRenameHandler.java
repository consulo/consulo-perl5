/*
 * Copyright 2015-2020 Alexandr Evstigneev
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

package com.perl5.lang.perl.idea.refactoring.rename;

import com.perl5.lang.perl.idea.refactoring.PerlRefactoringSupportProvider;
import consulo.codeEditor.Editor;
import consulo.language.editor.completion.lookup.LookupManager;
import consulo.language.editor.refactoring.RefactoringSupportProvider;
import consulo.language.editor.refactoring.rename.inplace.MemberInplaceRenameHandler;
import consulo.language.editor.refactoring.rename.inplace.MemberInplaceRenamer;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiNameIdentifierOwner;
import consulo.language.psi.PsiNamedElement;
import consulo.language.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;


public class PerlMemberInplaceRenameHandler extends MemberInplaceRenameHandler {
  @Override
  protected @NotNull MemberInplaceRenamer createMemberRenamer(@NotNull PsiElement element,
                                                              @NotNull PsiNameIdentifierOwner elementToRename,
                                                              @NotNull Editor editor) {
    return new PerlMemberInplaceRenamer(elementToRename, element, editor);
  }

  @Override
  protected boolean isAvailable(PsiElement element, @NotNull Editor editor, @NotNull PsiFile file) {
    PsiElement nameSuggestionContext = file.findElementAt(editor.getCaretModel().getOffset());
    if (nameSuggestionContext == null && editor.getCaretModel().getOffset() > 0) {
      nameSuggestionContext = file.findElementAt(editor.getCaretModel().getOffset() - 1);
    }

    if (element == null && LookupManager.getActiveLookup(editor) != null) {
      element = PsiTreeUtil.getParentOfType(nameSuggestionContext, PsiNamedElement.class);
    }
    final RefactoringSupportProvider
      supportProvider = element == null ? null : LanguageRefactoringSupport.INSTANCE.forLanguage(element.getLanguage());
    return editor.getSettings().isVariableInplaceRenameEnabled()
           && supportProvider instanceof PerlRefactoringSupportProvider &&
           ((PerlRefactoringSupportProvider)supportProvider).isPerlInplaceRenameAvailable(element, nameSuggestionContext);
  }
}
