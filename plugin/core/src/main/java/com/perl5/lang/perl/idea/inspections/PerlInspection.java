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

package com.perl5.lang.perl.idea.inspections;

import consulo.document.util.TextRange;
import consulo.language.editor.inspection.LocalInspectionTool;
import consulo.language.editor.inspection.LocalQuickFix;
import consulo.language.editor.inspection.ProblemHighlightType;
import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.psi.ElementManipulators;
import consulo.language.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static consulo.language.editor.inspection.ProblemHighlightType.*;


public abstract class PerlInspection extends LocalInspectionTool {

  protected void registerProblem(@NotNull ProblemsHolder holder,
                                 @Nullable PsiElement element,
                                 @NotNull  String message,
                                 LocalQuickFix... quickFixes) {
    doRegisterProblem(holder, element, message, GENERIC_ERROR_OR_WARNING, quickFixes);
  }

  protected void registerError(@NotNull ProblemsHolder holder,
                               @Nullable PsiElement element,
                               @NotNull  String message,
                               LocalQuickFix... quickFixes) {
    doRegisterProblem(holder, element, message, GENERIC_ERROR, quickFixes);
  }

  protected void markDeprecated(@NotNull ProblemsHolder holder,
                                @Nullable PsiElement element,
                                @NotNull  String message,
                                LocalQuickFix... quickFixes) {
    doRegisterProblem(holder, element, message, LIKE_DEPRECATED, quickFixes);
  }

  private void doRegisterProblem(@NotNull ProblemsHolder holder,
                                 @Nullable PsiElement element,
                                 @NotNull  String message,
                                 @NotNull ProblemHighlightType highlightType,
                                 @NotNull LocalQuickFix... quickFixes) {
    if (element == null) {
      return;
    }
    TextRange range = ElementManipulators.getValueTextRange(element);
    if (!range.isEmpty()) {
      holder.registerProblem(element, message, highlightType, range, quickFixes);
    }
  }

  @Override
  public boolean isEnabledByDefault() {
    return true;
  }
}
