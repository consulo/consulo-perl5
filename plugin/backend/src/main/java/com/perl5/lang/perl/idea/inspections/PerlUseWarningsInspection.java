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

import consulo.language.editor.inspection.ProblemHighlightType;
import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.inject.InjectedLanguageManager;
import consulo.virtualFileSystem.fileType.FileType;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.psi.PsiFile;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.language.file.light.LightVirtualFile;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.fileTypes.PerlFileType;
import com.perl5.lang.perl.idea.quickfixes.PerlUsePackageQuickFix;
import com.perl5.lang.perl.psi.PerlVisitor;
import com.perl5.lang.perl.psi.impl.PerlUseStatementElement;
import org.jetbrains.annotations.NotNull;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.localize.LocalizeValue;
import com.perl5.lang.perl.PerlLanguage;
import org.jetbrains.annotations.Nullable;


@ExtensionImpl
public class PerlUseWarningsInspection extends PerlInspection {
  @Override
  public @NotNull PsiElementVisitor buildVisitor(final @NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new PerlVisitor() {
      @Override
      public void visitFile(@NotNull PsiFile file) {
        if (!file.isWritable() ||
            !file.isPhysical() ||
            file.getVirtualFile() instanceof LightVirtualFile ||
            InjectedLanguageManager.getInstance(file.getProject()).isInjectedFragment(file)) {
          return;
        }

        FileType fileType = file.getFileType();
        if (!(fileType instanceof PerlFileType perlFileType) || !perlFileType.checkWarningsPragma()) {
          return;
        }

        for (PerlUseStatementElement useStatement : PsiTreeUtil.findChildrenOfType(file, PerlUseStatementElement.class)) {
          if (useStatement.getPackageProcessor().isWarningsEnabled(useStatement)) {
            return;
          }
        }
        holder.registerProblem(
          file,
          PerlBundle.message("perl.inspection.use.warnings"),
          ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
          new PerlUsePackageQuickFix("warnings FATAL => 'all'"),
          new PerlUsePackageQuickFix("warnings")
        );
      }
    };
  }

  // consulo: metadata from upstream <localInspection> registration in perl5.plugin.backend.main.xml
  @Override
  public @NotNull String getShortName() {
    return "UseWarnings";
  }

  @Override
  public @Nullable Language getLanguage() {
    return PerlLanguage.INSTANCE;
  }

  @Override
  public @NotNull LocalizeValue getDisplayName() {
    return LocalizeValue.localizeTODO(PerlBundle.message("inspection.use.warnings.display.name"));
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
