/*
 * Copyright 2015-2022 Alexandr Evstigneev
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

package com.perl5.lang.perl.idea.completion.providers;

import com.perl5.PerlIcons;
import com.perl5.lang.perl.idea.completion.inserthandlers.PerlAnnotationInsertHandler;
import com.perl5.lang.perl.idea.completion.providers.processors.PerlSimpleCompletionProcessor;
import consulo.language.editor.completion.CompletionParameters;
import consulo.language.editor.completion.CompletionResultSet;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import consulo.language.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import static com.perl5.lang.perl.psi.utils.PerlAnnotations.TOKENS_MAP;


public class PerlAnnotationCompletionProvider extends PerlCompletionProvider {
  @Override
  public void addCompletions(@NotNull CompletionParameters parameters,
                                @NotNull ProcessingContext context,
                                @NotNull CompletionResultSet resultSet) {
    PerlSimpleCompletionProcessor completionProcessor = new PerlSimpleCompletionProcessor(parameters, resultSet, parameters.getPosition());
    for (String annotation : TOKENS_MAP.keySet()) {
      if (completionProcessor.matches(annotation)) {
        if (!completionProcessor.process(
          LookupElementBuilder
            .create(TOKENS_MAP.get(annotation), annotation)
            .withInsertHandler(PerlAnnotationInsertHandler.INSTANCE)
            .withIcon(PerlIcons.ANNOTATION_GUTTER_ICON))) {
          break;
        }
      }
    }
    completionProcessor.logStatus(getClass());
  }
}
