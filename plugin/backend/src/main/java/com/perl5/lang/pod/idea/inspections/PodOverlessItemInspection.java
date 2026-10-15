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

package com.perl5.lang.pod.idea.inspections;

import consulo.language.editor.inspection.LocalInspectionTool;
import consulo.language.editor.inspection.ProblemHighlightType;
import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.psi.util.PsiTreeUtil;
import com.perl5.PerlBundle;
import com.perl5.lang.pod.parser.psi.PodVisitor;
import com.perl5.lang.pod.parser.psi.mixin.PodSectionOver;
import com.perl5.lang.pod.psi.PsiItemSection;
import org.jetbrains.annotations.NotNull;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.localize.LocalizeValue;
import com.perl5.lang.pod.PodLanguage;
import org.jetbrains.annotations.Nullable;


@ExtensionImpl
public class PodOverlessItemInspection extends LocalInspectionTool {
  @Override
  public @NotNull PsiElementVisitor buildVisitor(final @NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new PodVisitor() {
      @Override
      public void visitItemSection(@NotNull PsiItemSection o) {
        if (PsiTreeUtil.getParentOfType(o, PodSectionOver.class) == null) {
          PsiElement openTag = o.getFirstChild();
          if (openTag != null) {
            holder.registerProblem(openTag, PerlBundle.message("inspection.message.list.item.outside.over.block"),
                                   ProblemHighlightType.GENERIC_ERROR);
          }
        }
        super.visitItemSection(o);
      }
    };
  }

  // consulo: metadata from upstream <localInspection> registration in perl5.plugin.backend.main.xml
  @Override
  public @Nullable Language getLanguage() {
    return PodLanguage.INSTANCE;
  }

  @Override
  public @NotNull LocalizeValue getDisplayName() {
    return LocalizeValue.localizeTODO(PerlBundle.message("inspection.pod.overless.item.display.name"));
  }

  @Override
  public @NotNull LocalizeValue getGroupDisplayName() {
    return LocalizeValue.localizeTODO(PerlBundle.message("perl5.pod.group"));
  }

  @Override
  public @NotNull HighlightDisplayLevel getDefaultLevel() {
    return HighlightDisplayLevel.ERROR;
  }
}
