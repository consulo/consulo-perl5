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
import consulo.project.Project;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.psi.scope.GlobalSearchScope;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.idea.configuration.settings.PerlSharedSettings;
import com.perl5.lang.perl.psi.PerlSubDefinitionElement;
import com.perl5.lang.perl.psi.PerlVisitor;
import com.perl5.lang.perl.util.PerlPackageUtilCore;
import com.perl5.lang.perl.util.PerlSubUtil;
import org.jetbrains.annotations.NotNull;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.localize.LocalizeValue;
import com.perl5.lang.perl.PerlLanguage;
import org.jetbrains.annotations.Nullable;


@ExtensionImpl
public class PerlMultipleSubDefinitionsInspection extends PerlInspection {
  @Override
  public @NotNull PsiElementVisitor buildVisitor(final @NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new PerlVisitor() {
      @Override
      public void visitPerlSubDefinitionElement(@NotNull PerlSubDefinitionElement o) {
        Project project = o.getProject();
        String name = "Sub";

        String canonicalName = o.getCanonicalName();
        GlobalSearchScope searchScope;
        if (PerlPackageUtilCore.isMain(o.getNamespaceName()) && PerlSharedSettings.getInstance(project).SIMPLE_MAIN_RESOLUTION) {
          searchScope = GlobalSearchScope.fileScope(o.getContainingFile());
        }
        else {
          searchScope = GlobalSearchScope.projectScope(project);
        }
        if (PerlSubUtil.getSubDefinitions(project, canonicalName, searchScope).size() > 1) {
          registerProblem(holder, o.getNameIdentifier(),
                          PerlBundle.message("perl.inspection.multiple.sub.declarations", name.toLowerCase()));
        }
        super.visitPerlSubDefinitionElement(o);
      }

      @Override
      protected boolean shouldVisitLightElements() {
        return true;
      }
    };
  }

  // consulo: metadata from upstream <localInspection> registration in perl5.plugin.backend.main.xml
  @Override
  public @NotNull String getShortName() {
    return "SubMultipleDefinitions";
  }

  @Override
  public @Nullable Language getLanguage() {
    return PerlLanguage.INSTANCE;
  }

  @Override
  public @NotNull LocalizeValue getDisplayName() {
    return LocalizeValue.localizeTODO(PerlBundle.message("inspection.sub.multiple.definitions.display.name"));
  }

  @Override
  public @NotNull LocalizeValue getGroupDisplayName() {
    return LocalizeValue.localizeTODO(PerlBundle.message("inspection.group.perl"));
  }

  @Override
  public @NotNull HighlightDisplayLevel getDefaultLevel() {
    return HighlightDisplayLevel.WARNING;
  }
}
