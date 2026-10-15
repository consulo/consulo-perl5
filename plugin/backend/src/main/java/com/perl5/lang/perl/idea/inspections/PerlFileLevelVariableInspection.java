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
import consulo.language.psi.util.PsiTreeUtil;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.psi.PerlSubDefinitionElement;
import com.perl5.lang.perl.psi.PerlVariable;
import com.perl5.lang.perl.psi.PerlVariableDeclarationElement;
import com.perl5.lang.perl.psi.PsiPerlSubExpr;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.localize.LocalizeValue;
import com.perl5.lang.perl.PerlLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@ExtensionImpl
public class PerlFileLevelVariableInspection extends PerlVariableInspectionBase {

  @Override
  public void checkDeclaration(ProblemsHolder holder, PerlVariableDeclarationElement variableDeclarationWrapper) {
    if (variableDeclarationWrapper.isGlobalDeclaration() &&
        PerlUnusedGlobalVariableInspection.EXCLUSIONS.contains(variableDeclarationWrapper.getText())) {
      return;
    }

    if (PsiTreeUtil.getParentOfType(variableDeclarationWrapper, PerlSubDefinitionElement.class, PsiPerlSubExpr.class) == null) {
      PerlVariable variable = variableDeclarationWrapper.getVariable();
      holder.registerProblem(
        variable,
        PerlBundle.message("inspection.message.file.level.variable", variable.getText()),
        ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
    }
  }

  // consulo: metadata from upstream <localInspection> registration in perl5.plugin.backend.main.xml
  @Override
  public @NotNull String getShortName() {
    return "FileLevelVariable";
  }

  @Override
  public @Nullable Language getLanguage() {
    return PerlLanguage.INSTANCE;
  }

  @Override
  public @NotNull LocalizeValue getDisplayName() {
    return LocalizeValue.localizeTODO(PerlBundle.message("inspection.file.level.variable.display.name"));
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
