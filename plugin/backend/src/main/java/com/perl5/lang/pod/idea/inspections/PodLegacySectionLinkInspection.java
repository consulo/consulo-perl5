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
import consulo.language.ast.IElementType;
import consulo.language.psi.PsiUtilCore;
import com.perl5.PerlBundle;
import com.perl5.lang.pod.parser.psi.PodVisitor;
import com.perl5.lang.pod.parser.psi.mixin.PodFormatterL;
import com.perl5.lang.pod.parser.psi.util.PodRenderUtil;
import com.perl5.lang.pod.psi.PsiLinkSection;
import com.perl5.lang.pod.psi.PsiPodFormatLink;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.perl5.lang.pod.parser.PodElementTypesGenerated.POD_DIV;
import static com.perl5.lang.pod.parser.PodElementTypesGenerated.POD_QUOTE_DOUBLE;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.localize.LocalizeValue;
import com.perl5.lang.pod.PodLanguage;

@ExtensionImpl
public class PodLegacySectionLinkInspection extends LocalInspectionTool {
  @Override
  public @NotNull PsiElementVisitor buildVisitor(final @NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new PodVisitor() {

      @Override
      public void visitPodFormatLink(@NotNull PsiPodFormatLink o) {
        assert o instanceof PodFormatterL;
        PodFormatterL link = (PodFormatterL)o;
        PsiLinkSection sectionelement = link.getLinkSectionElement();
        if (link.getLinkNameElement() == null && isSectionLegacy(sectionelement)) {
          holder.registerProblem(sectionelement,
                                 PerlBundle.message("inspection.message.section.should.have.slash.before.it",
                                                    PodRenderUtil.renderPsiElementAsText(sectionelement)),
                                 ProblemHighlightType.LIKE_DEPRECATED);
        }
        super.visitPodFormatLink(o);
      }

      @Contract("null -> false")
      private boolean isSectionLegacy(@Nullable PsiLinkSection linkSection) {
        if (linkSection == null) {
          return false;
        }

        PsiElement run = linkSection.getPrevSibling();
        IElementType elementType = PsiUtilCore.getElementType(run);
        return elementType != POD_DIV &&
               !(elementType == POD_QUOTE_DOUBLE && PsiUtilCore.getElementType(run.getPrevSibling()) == POD_DIV);
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
    return LocalizeValue.localizeTODO(PerlBundle.message("inspection.pod.legacy.section.link.display.name"));
  }

  @Override
  public @NotNull LocalizeValue getGroupDisplayName() {
    return LocalizeValue.localizeTODO(PerlBundle.message("perl5.pod.group"));
  }

  @Override
  public @NotNull HighlightDisplayLevel getDefaultLevel() {
    return HighlightDisplayLevel.WARNING;
  }
}
