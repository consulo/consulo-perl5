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
import com.perl5.lang.perl.psi.PsiPerlCommaSequenceExpr;
import com.perl5.lang.perl.psi.PsiPerlParenthesisedExpr;
import com.perl5.lang.perl.psi.PsiPerlStatement;
import com.perl5.lang.perl.psi.references.PerlSubReferenceSuper;
import org.jetbrains.annotations.NotNull;

public final class PerlMooseStringSubReferenceProvider extends PsiReferenceProvider {
  @Override
  public @NotNull PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element,
                                                                  @NotNull ProcessingContext context) {
    PerlMooseStatementWithSubReference provider = PsiTreeUtil.getParentOfType(
      element, PerlMooseStatementWithSubReference.class, true, PsiPerlStatement.class
    );
    if (provider == null) {
      return PsiReference.EMPTY_ARRAY;
    }

    if (!provider.hasStringSubReference()) {
      return PsiReference.EMPTY_ARRAY;
    }

    PsiElement expr = provider.getExpr();
    if (expr == null) {
      return PsiReference.EMPTY_ARRAY;
    }

    if (expr instanceof PsiPerlParenthesisedExpr) {
      expr = expr.getFirstChild();
      if (expr != null) {
        expr = expr.getNextSibling();
      }
    }

    if (expr instanceof PsiPerlCommaSequenceExpr) {
      PsiElement lastElement = expr.getLastChild();

      if (PsiTreeUtil.isAncestor(expr, element, true) && !PsiTreeUtil.isAncestor(lastElement, element, true)) {
        return new PsiReference[]{new PerlSubReferenceSuper(element)};
      }
    }

    return PsiReference.EMPTY_ARRAY;
  }
}
