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

package com.perl5.lang.pod.parser.psi.references.providers;

import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiReference;
import consulo.language.psi.PsiReferenceProvider;
import consulo.language.util.ProcessingContext;
import com.perl5.lang.pod.parser.psi.PodLinkDescriptor;
import com.perl5.lang.pod.parser.psi.mixin.PodFormatterL;
import com.perl5.lang.pod.parser.psi.references.PodLinkToFileReference;
import com.perl5.lang.pod.parser.psi.references.PodLinkToSectionReference;
import com.perl5.lang.pod.psi.PsiLinkName;
import com.perl5.lang.pod.psi.PsiLinkSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class PodFormatterLReferenceProvider extends PsiReferenceProvider {
  @Override
  public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
    if (element instanceof PodFormatterL podFormatterL) {
      List<PsiReference> references = new ArrayList<>();
      PodLinkDescriptor descriptor = podFormatterL.getLinkDescriptor();

      if (descriptor != null && !descriptor.isUrl()) {
        int linkStartOffset = podFormatterL.getTextRange().getStartOffset();
        PsiLinkName linkNameElement = podFormatterL.getLinkNameElement();
        if (linkNameElement != null) {
          references.add(new PodLinkToFileReference(podFormatterL, linkNameElement.getTextRange().shiftLeft(linkStartOffset)));
        }

        PsiLinkSection linkSectionElement = podFormatterL.getLinkSectionElement();
        if (linkSectionElement != null) {
          references.add(new PodLinkToSectionReference(podFormatterL, linkSectionElement.getTextRange().shiftLeft(linkStartOffset)));
        }
      }
      return references.toArray(PsiReference.EMPTY_ARRAY);
    }
    return PsiReference.EMPTY_ARRAY;
  }
}
