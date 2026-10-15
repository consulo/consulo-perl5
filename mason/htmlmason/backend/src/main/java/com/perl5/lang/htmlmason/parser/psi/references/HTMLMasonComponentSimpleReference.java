/*
 * Copyright 2015-2023 Alexandr Evstigneev
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

package com.perl5.lang.htmlmason.parser.psi.references;

import consulo.document.util.TextRange;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementResolveResult;
import consulo.language.psi.PsiFile;
import consulo.language.psi.ResolveResult;
import consulo.language.util.IncorrectOperationException;
import com.perl5.lang.htmlmason.parser.psi.impl.HTMLMasonFileImpl;
import com.perl5.lang.perl.psi.PerlString;
import org.jetbrains.annotations.NotNull;


public class HTMLMasonComponentSimpleReference extends HTMLMasonStringReference {
  public HTMLMasonComponentSimpleReference(@NotNull PerlString element, TextRange textRange) {
    super(element, textRange);
  }

  @Override
  public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
    return myElement;
  }

  @Override
  public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
    return myElement;
  }

  @Override
  protected @NotNull ResolveResult[] resolveInner(boolean incompleteCode) {
    PsiFile psiFile = getElement().getContainingFile();

    if (psiFile instanceof HTMLMasonFileImpl) {
      return new ResolveResult[]{new PsiElementResolveResult(psiFile)};
    }

    return ResolveResult.EMPTY_ARRAY;
  }
}