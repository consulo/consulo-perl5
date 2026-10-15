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

package com.perl5.lang.perl.psi.references.providers;

import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiReference;
import consulo.language.psi.PsiReferenceProvider;
import consulo.language.util.ProcessingContext;
import com.perl5.lang.perl.psi.PerlNamespaceDefinitionWithIdentifier;
import com.perl5.lang.perl.psi.PsiPerlRequireExpr;
import com.perl5.lang.perl.psi.impl.PerlNamespaceElementImpl;
import com.perl5.lang.perl.psi.impl.PerlUseStatementElementBase;
import com.perl5.lang.perl.psi.references.PerlNamespaceFileReference;
import com.perl5.lang.perl.psi.references.PerlNamespaceReference;
import org.jetbrains.annotations.NotNull;


public final class PerlNamespaceElementReferenceProvider extends PsiReferenceProvider {
  @Override
  public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
    if (element instanceof PerlNamespaceElementImpl namespaceElement) {
      PsiElement nameSpaceContainer = namespaceElement.getParent();
      if (nameSpaceContainer instanceof PerlUseStatementElementBase || nameSpaceContainer instanceof PsiPerlRequireExpr) {
        return new PsiReference[]{new PerlNamespaceFileReference(namespaceElement)};
      }
      else if (nameSpaceContainer instanceof PerlNamespaceDefinitionWithIdentifier) {
        return PsiReference.EMPTY_ARRAY;
      }
      return new PsiReference[]{new PerlNamespaceReference(namespaceElement)};
    }
    return PsiReference.EMPTY_ARRAY;
  }
}
