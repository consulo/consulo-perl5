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

import consulo.language.editor.inspection.ProblemHighlightType;
import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.search.ReferencesSearch;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.psi.PerlSubDeclarationElement;
import com.perl5.lang.perl.psi.PerlSubDefinitionElement;
import com.perl5.lang.perl.psi.PerlVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static com.perl5.lang.perl.util.PerlSubUtilCore.SUB_DESTROY;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.localize.LocalizeValue;
import com.perl5.lang.perl.PerlLanguage;
import org.jetbrains.annotations.Nullable;


@ExtensionImpl
public class PerlUnusedSubInspection extends PerlInspection {
  private static final Set<String> EXCLUSIONS = Set.of(
    "BEGIN",
    "UNITCHECK",
    "CHECK",
    "INIT",
    "END",
    SUB_DESTROY
  );

  @Override
  public @NotNull PsiElementVisitor buildVisitor(final @NotNull ProblemsHolder holder, boolean isOnTheFly) {

    return new PerlVisitor() {
      @Override
      public void visitPerlSubDefinitionElement(@NotNull PerlSubDefinitionElement o) {
        PsiElement subNameElement = o.getNameIdentifier();
        if (subNameElement != null &&
            !EXCLUSIONS.contains(o.getName()) &&
            ReferencesSearch.search(o, GlobalSearchScope.projectScope(o.getProject())).findFirst() == null) {
          holder.registerProblem(subNameElement, PerlBundle.message("inspection.message.unused.sub.definition"),
                                 ProblemHighlightType.LIKE_UNUSED_SYMBOL);
        }
      }

      @Override
      protected boolean shouldVisitLightElements() {
        return true;
      }

      @Override
      public void visitSubDeclarationElement(@NotNull PerlSubDeclarationElement o) {
        PsiElement subNameElement = o.getNameIdentifier();
        if (subNameElement != null &&
            !EXCLUSIONS.contains(o.getName()) &&
            ReferencesSearch.search(o, GlobalSearchScope.projectScope(o.getProject())).findFirst() == null) {
          holder.registerProblem(subNameElement, PerlBundle.message("inspection.message.unused.sub.declaration"),
                                 ProblemHighlightType.LIKE_UNUSED_SYMBOL);
        }
      }
    };
  }

  // consulo: metadata from upstream <localInspection> registration in perl5.plugin.backend.main.xml
  @Override
  public @NotNull String getShortName() {
    return "SubUnused";
  }

  @Override
  public @Nullable Language getLanguage() {
    return PerlLanguage.INSTANCE;
  }

  @Override
  public @NotNull LocalizeValue getDisplayName() {
    return LocalizeValue.localizeTODO(PerlBundle.message("inspection.sub.unused.display.name"));
  }

  @Override
  public @NotNull LocalizeValue getGroupDisplayName() {
    return LocalizeValue.localizeTODO(PerlBundle.message("inspection.group.perl"));
  }

  @Override
  public @NotNull HighlightDisplayLevel getDefaultLevel() {
    return HighlightDisplayLevel.WARNING;
  }

  @Override
  public boolean isEnabledByDefault() {
    return false;
  }
}
