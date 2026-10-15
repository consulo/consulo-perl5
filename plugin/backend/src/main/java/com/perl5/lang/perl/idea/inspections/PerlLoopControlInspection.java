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

package com.perl5.lang.perl.idea.inspections;

import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.ast.IElementType;
import consulo.language.ast.TokenSet;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.language.psi.PsiUtilCore;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.lexer.PerlSyntax;
import com.perl5.lang.perl.psi.*;
import com.perl5.lang.perl.psi.impl.PsiPerlStatementImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import static com.perl5.PerlBundleKt.PATH_TO_BUNDLE;
import static com.perl5.lang.perl.parser.PerlElementTypesGenerated.*;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.localize.LocalizeValue;
import com.perl5.lang.perl.PerlLanguage;
import org.jetbrains.annotations.Nullable;

@ExtensionImpl
public class PerlLoopControlInspection extends PerlInspection {
  static final TokenSet MAP_GREP = TokenSet.create(
    MAP_EXPR, GREP_EXPR
  );

  @Override
  public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new PerlVisitor() {
      private void problem(@NotNull PsiElement anchor,
                           @NotNull @PropertyKey(resourceBundle = PATH_TO_BUNDLE) String key,
                           @NotNull String... args) {
        registerProblem(holder, anchor, PerlBundle.message(key, (Object[])args));
      }

      /**
       * Traversing blocks up, trying to figure out if last/next/redo are in right place.
       *
       * @param expr last/next/redo expression
       * @implNote duplicates logic in {@link PerlFlowControlExpr#getTargetScope()}
       */
      @Override
      public void visitPerlFlowControlExpr(PerlFlowControlExpr expr) {
        PsiElement keyword = expr.getFirstChild();
        if (keyword == null) {
          return;
        }
        String keywordText = keyword.getText();

        // checks modifier
        PsiPerlStatementImpl containingStatement = PsiTreeUtil.getParentOfType(expr, PsiPerlStatementImpl.class);
        PsiPerlStatementModifier modifier = containingStatement == null ? null : containingStatement.getModifier();
        if (modifier instanceof PsiPerlForStatementModifier) {
          return;
        }

        // traversing up
        PsiElement position = expr;
        boolean isInsideGiven = false;
        boolean isInsideWhenOrDefault = false;
        while (true) {
          PsiElement closestBlockContainer = PerlBlock.getClosestBlockCompoundContainer(position);

          if (closestBlockContainer == null) {
            break;
          }

          IElementType blockContainerType = PsiUtilCore.getElementType(closestBlockContainer);

          if (PerlBlock.LOOPS_CONTAINERS.contains(blockContainerType)) {
            return;
          }
          else if (blockContainerType == NAMED_BLOCK) {
            problem(expr, "perl.inspection.loop.control.in.named.block", keywordText);
            return;
          }
          else if (MAP_GREP.contains(blockContainerType)) {
            problem(expr, "perl.inspection.loop.control.in.map", keywordText);
            return;
          }
          else if (PerlBlock.BLOCKS_WITH_RETURN_VALUE.contains(blockContainerType)) {
            problem(expr, "perl.inspection.loop.control.in.do", keywordText);
            return;
          }
          else if (blockContainerType == GIVEN_COMPOUND) {
            isInsideGiven = true;
          }
          else if (blockContainerType == WHEN_COMPOUND || blockContainerType == DEFAULT_COMPOUND) {
            isInsideWhenOrDefault = true;
          }
          position = closestBlockContainer;
        }

        if (expr instanceof PsiPerlNextExpr && isInsideWhenOrDefault) {
          holder.registerProblem(
            expr,
            PerlBundle.message("perl.inspection.loop.control.next.instead.of.continue"),
            new ReplaceWithExpressionQuickFix("continue"));
        }
        else if (expr instanceof PsiPerlLastExpr && isInsideGiven) {
          holder.registerProblem(
            expr,
            PerlBundle.message("perl.inspection.loop.control.last.instead.of.break"),
            new ReplaceWithExpressionQuickFix(PerlSyntax.BREAK_KEYWORD));
        }
        else {
          problem(expr, "perl.inspection.loop.control.outside", keywordText);
        }
      }
    };
  }

  // consulo: metadata from upstream <localInspection> registration in perl5.plugin.backend.main.xml
  @Override
  public @NotNull String getShortName() {
    return "LoopControlInspection";
  }

  @Override
  public @Nullable Language getLanguage() {
    return PerlLanguage.INSTANCE;
  }

  @Override
  public @NotNull LocalizeValue getDisplayName() {
    return LocalizeValue.localizeTODO(PerlBundle.message("inspection.loop.control.inspection.display.name"));
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
