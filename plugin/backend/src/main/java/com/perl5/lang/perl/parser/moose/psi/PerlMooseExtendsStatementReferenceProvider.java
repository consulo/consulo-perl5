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

package com.perl5.lang.perl.parser.moose.psi;

import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiReference;
import consulo.language.psi.PsiReferenceProvider;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.language.util.ProcessingContext;
import com.perl5.lang.perl.psi.PerlString;
import com.perl5.lang.perl.psi.PsiPerlStatement;
import com.perl5.lang.perl.psi.references.PerlNamespaceReference;
import org.jetbrains.annotations.NotNull;


public final class PerlMooseExtendsStatementReferenceProvider extends PsiReferenceProvider {
  @Override
  public @NotNull PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                                  @NotNull ProcessingContext context) {
    PerlMooseExtendsStatement provider = PsiTreeUtil.getParentOfType(
      element, PerlMooseExtendsStatement.class, true, PsiPerlStatement.class
    );
    if (provider == null) {
      return PsiReference.EMPTY_ARRAY;
    }

    PsiElement string = element.getParent();
    if (string == null) {
      return PsiReference.EMPTY_ARRAY;
    }
    PsiElement meOrCommaSequence = string.getParent();
    if (meOrCommaSequence == provider || meOrCommaSequence != null && meOrCommaSequence.getParent() == provider) {
      if (!PerlString.looksLikePackage(element.getText())) {
        return new PsiReference[]{new PerlNamespaceReference(element)};
      }
    }
    return PsiReference.EMPTY_ARRAY;
  }
}
