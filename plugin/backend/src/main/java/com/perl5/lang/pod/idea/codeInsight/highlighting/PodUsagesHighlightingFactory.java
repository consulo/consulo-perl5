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

package com.perl5.lang.pod.idea.codeInsight.highlighting;

import consulo.language.editor.TargetElementUtil;
import consulo.language.editor.highlight.usage.HighlightUsagesHandlerBase;
import consulo.language.editor.highlight.usage.HighlightUsagesHandlerFactoryBase;
import consulo.codeEditor.Editor;
import consulo.document.util.TextRange;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiReference;
import consulo.language.psi.scope.LocalSearchScope;
import consulo.language.psi.search.ReferencesSearch;
import consulo.language.psi.PsiUtilCore;
import com.perl5.lang.pod.PodLanguage;
import com.perl5.lang.pod.parser.psi.PodTitledSection;
import com.perl5.lang.pod.parser.psi.references.PodLinkToSectionReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

import static consulo.language.editor.TargetElementUtilExtender.ELEMENT_NAME_ACCEPTED;
import static consulo.language.editor.TargetElementUtilExtender.REFERENCED_ELEMENT_ACCEPTED;

public class PodUsagesHighlightingFactory extends HighlightUsagesHandlerFactoryBase {
  @Override
  public @Nullable HighlightUsagesHandlerBase<?> createHighlightUsagesHandler(@NotNull Editor editor,
                                                                              @NotNull PsiFile file,
                                                                              @NotNull PsiElement target) {
    if (!target.isValid() || !target.getLanguage().isKindOf(PodLanguage.INSTANCE)) {
      return null;
    }

    PsiElement targetElement = TargetElementUtil.findTargetElement(editor, Set.of(REFERENCED_ELEMENT_ACCEPTED, ELEMENT_NAME_ACCEPTED));

    if (!(targetElement instanceof PodTitledSection section)) {
      return null;
    }
    return new HighlightUsagesHandlerBase<>(editor, file) {
      @Override
      public @NotNull List<PsiElement> getTargets() {
        return Collections.singletonList(targetElement);
      }

      @Override
      protected void selectTargets(@NotNull List<PsiElement> targets,
                                   @NotNull Consumer<List<PsiElement>> selectionConsumer) {
        selectionConsumer.accept(targets);
      }

      @Override
      public void computeUsages(@NotNull List<PsiElement> targets) {
        List<PodTitledSection> allTargetSections = PodLinkToSectionReference.getAllSynonymousSections(section);
        if (allTargetSections.isEmpty()) {
          return;
        }
        if (Objects.equals(PsiUtilCore.getVirtualFile(targetElement), PsiUtilCore.getVirtualFile(file))) {
          List<TextRange> writeUsages = getWriteUsages();
          for (PodTitledSection titledSection : allTargetSections) {
            PsiElement titleElement = titledSection.getTitleElement();
            if (titleElement != null) {
              writeUsages.add(titleElement.getTextRange());
            }
          }
        }

        List<TextRange> readUsages = getReadUsages();
        ReferencesSearch.search(allTargetSections.getFirst(), new LocalSearchScope(file)).forEach(
          (PsiReference it) -> readUsages.add(it.getRangeInElement().shiftRight(it.getElement().getTextRange().getStartOffset())));
      }
    };
  }
}
