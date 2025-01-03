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

package com.perl5.lang.perl.idea.inspections;

import com.perl5.PerlBundle;
import com.perl5.lang.perl.psi.PerlVisitor;
import com.perl5.lang.perl.psi.PsiPerlConditionExpr;
import com.perl5.lang.perl.psi.PsiPerlForStatementModifier;
import com.perl5.lang.perl.psi.PsiPerlForeachCompound;
import consulo.language.ast.TokenSet;
import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.psi.PsiUtilCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.perl5.lang.perl.parser.PerlElementTypesGenerated.HASH_CAST_EXPR;
import static com.perl5.lang.perl.parser.PerlElementTypesGenerated.HASH_VARIABLE;

public class PerlHashLoopInspection extends PerlInspection {
  private static final TokenSet HASH_LIKE_TOKENS = TokenSet.create(
    HASH_VARIABLE,
    HASH_CAST_EXPR
  );

  @Override
  public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new PerlVisitor() {
      @Override
      public void visitForStatementModifier(@NotNull PsiPerlForStatementModifier o) {
        checkExpression(o.getExpr());
      }

      @Override
      public void visitForeachCompound(@NotNull PsiPerlForeachCompound o) {
        PsiPerlConditionExpr conditionExpr = o.getConditionExpr();
        if (conditionExpr == null) {
          return;
        }
        checkExpression(conditionExpr.getExpr());
      }

      private void checkExpression(@Nullable PsiElement expr) {
        if (HASH_LIKE_TOKENS.contains(PsiUtilCore.getElementType(expr))) {
          registerProblem(holder, expr, PerlBundle.message("perl.inspection.loop.on.hash"));
        }
      }
    };
  }
}
