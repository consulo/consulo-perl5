/*
 * Copyright 2015-2020 Alexandr Evstigneev
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

package com.perl5.lang.pod.idea.completion;

import com.perl5.lang.perl.idea.completion.providers.processors.PerlSimpleCompletionProcessor;
import com.perl5.lang.perl.psi.PerlSubElement;
import com.perl5.lang.perl.psi.utils.PerlPsiUtil;
import com.perl5.lang.pod.parser.PodElementPatterns;
import com.perl5.lang.pod.parser.psi.PodRecursiveVisitor;
import com.perl5.lang.pod.parser.psi.PodSectionTitle;
import com.perl5.lang.pod.parser.psi.PodTitledSection;
import com.perl5.lang.pod.parser.psi.references.PodSubReference;
import com.perl5.lang.pod.parser.psi.util.PodFileUtil;
import consulo.language.ast.IElementType;
import consulo.language.editor.completion.CompletionParameters;
import consulo.language.editor.completion.CompletionProvider;
import consulo.language.editor.completion.CompletionResultSet;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiUtilCore;
import consulo.language.util.ProcessingContext;
import consulo.util.lang.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PodTitleCompletionProvider implements CompletionProvider, PodElementPatterns {
  public static final String COPYRIGHT_AND_LICENSE = "COPYRIGHT AND LICENSE";
  public static final List<String> DEFAULT_POD_SECTIONS = Collections.unmodifiableList(Arrays.asList(
    "VERSION",
    "SYNOPSIS",
    "API",
    "DESCRIPTION",
    "INSTALLATION",
    "NAME",
    "AUTHORS",
    "CONTRIBUTORS",
    COPYRIGHT_AND_LICENSE,
    "METHODS",
    "ATTRIBUTES",
    "FUNCTIONS"
  ));

  @Override
  public void addCompletions(@NotNull CompletionParameters parameters,
                                @NotNull ProcessingContext context,
                                @NotNull CompletionResultSet result) {
    PsiElement element = parameters.getPosition();

    PsiElement elementParent = element.getParent();
    if (PsiUtilCore.getElementType(element) != POD_IDENTIFIER ||
        element.getPrevSibling() != null ||
        !(elementParent instanceof PodSectionTitle)) {
      return;
    }

    IElementType grandparentElementType = PsiUtilCore.getElementType(elementParent.getParent());
    PerlSimpleCompletionProcessor completionProcessor = new PerlSimpleCompletionProcessor(parameters, result, element);

    if (grandparentElementType == HEAD_1_SECTION) {
      for (String sectionTitle : DEFAULT_POD_SECTIONS) {
        if (completionProcessor.matches(sectionTitle) && !completionProcessor.process(LookupElementBuilder.create(sectionTitle))) {
          break;
        }
      }
    }

    final PsiFile elementFile = element.getContainingFile().getOriginalFile();
    final PsiFile perlFile = PodFileUtil.getTargetPerlFile(elementFile);
    if (perlFile == null) {
      return;
    }

    Set<PerlSubElement> possibleTargets = new HashSet<>();
    PerlPsiUtil.processSubElements(perlFile, possibleTargets::add);
    elementFile.accept(new PodRecursiveVisitor() {
      @Override
      public void visitTargetableSection(PodTitledSection o) {
        processSection(o);
        super.visitTargetableSection(o);
      }

      private void processSection(@NotNull PodTitledSection o) {
        PsiElement titleBlock = o.getTitleElement();
        if (titleBlock == null) {
          return;
        }
        PsiElement firstChild = titleBlock.getFirstChild();
        if (firstChild == null) {
          return;
        }
        //noinspection SuspiciousMethodCalls
        Arrays.stream(firstChild.getReferences())
          .filter(it -> it instanceof PodSubReference)
          .flatMap(it -> Arrays.stream(((PodSubReference)it).multiResolve(false)))
          .map(ResolveResult::getElement)
          .forEach(possibleTargets::remove);
      }
    });

    for (PerlSubElement subElement : possibleTargets) {
      String lookupString = StringUtil.notNullize(subElement.getPresentableName());
      if (completionProcessor.matches(lookupString) &&
          !completionProcessor.process(LookupElementBuilder.create(subElement, lookupString).withIcon(subElement.getIcon(0)))) {
        break;
      }
    }
    completionProcessor.logStatus(getClass());
  }
}
