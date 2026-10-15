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

import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.ast.TokenSet;
import consulo.language.psi.PsiUtilCore;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.psi.PerlVisitor;
import com.perl5.lang.perl.psi.PsiPerlConditionExpr;
import com.perl5.lang.perl.psi.PsiPerlForStatementModifier;
import com.perl5.lang.perl.psi.PsiPerlForeachCompound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.perl5.lang.perl.parser.PerlElementTypesGenerated.HASH_CAST_EXPR;
import static com.perl5.lang.perl.parser.PerlElementTypesGenerated.HASH_VARIABLE;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.localize.LocalizeValue;
import com.perl5.lang.perl.PerlLanguage;

@ExtensionImpl
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

  // consulo: metadata from upstream <localInspection> registration in perl5.plugin.backend.main.xml
  @Override
  public @NotNull String getShortName() {
    return "HashLoopInspection";
  }

  @Override
  public @Nullable Language getLanguage() {
    return PerlLanguage.INSTANCE;
  }

  @Override
  public @NotNull LocalizeValue getDisplayName() {
    return LocalizeValue.localizeTODO(PerlBundle.message("inspection.hash.loop.inspection.display.name"));
  }

  @Override
  public @NotNull LocalizeValue getGroupDisplayName() {
    return LocalizeValue.localizeTODO(PerlBundle.message("inspection.group.perl"));
  }

  @Override
  public @NotNull HighlightDisplayLevel getDefaultLevel() {
    return HighlightDisplayLevel.ERROR;
  }
}
