/*
 * Copyright 2015-2019 Alexandr Evstigneev
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

import com.perl5.lang.pod.parser.PodElementPatterns;
import com.perl5.lang.pod.parser.psi.PodSyntaxElements;
import consulo.application.progress.ProgressManager;
import consulo.language.ast.IElementType;
import consulo.language.editor.completion.CompletionContributor;
import consulo.language.editor.completion.CompletionParameters;
import consulo.language.editor.completion.CompletionResultSet;
import consulo.language.editor.completion.CompletionType;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiUtilCore;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.util.Collections;


public class PodCompletionContributor extends CompletionContributor implements PodElementPatterns {
  public PodCompletionContributor() {
    extend(
      CompletionType.BASIC,
      LINK_IDENTIFIER,
      new PodLinkCompletionProvider()
    );

    extend(
      CompletionType.BASIC,
      TITLE_IDENTIFIER,
      new PodTitleCompletionProvider()
    );
  }

  @Override
  public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
    PsiElement position = parameters.getPosition();
    IElementType positionType = PsiUtilCore.getElementType(position);
    if (positionType == POD_FORMAT_NAME) {
      IElementType prevSiblingType = PsiUtilCore.getElementType(position.getPrevSibling());
      PodSyntaxElements.KNOWN_FORMATTERS.forEach(it -> {
        result.addElement(LookupElementBuilder.create(it));
        if (prevSiblingType != POD_COLON) {
          result.addElement(LookupElementBuilder.create(":" + it));
        }
      });
      return;
    }
    else if (positionType == POD_ENCODING_NAME) {
      Charset.availableCharsets().keySet().forEach(it -> {
        ProgressManager.checkCanceled();
        result.addElement(LookupElementBuilder.create(it).withLookupStrings(Collections.singletonList(it.toLowerCase())));
      });
      return;
    }
    super.fillCompletionVariants(parameters, result);
  }
}
