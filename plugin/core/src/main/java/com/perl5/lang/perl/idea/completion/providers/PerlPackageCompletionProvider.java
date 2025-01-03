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

import com.perl5.lang.perl.idea.completion.PerlInsertHandlers;
import com.perl5.lang.perl.idea.completion.providers.processors.PerlSimpleCompletionProcessor;
import com.perl5.lang.perl.idea.completion.providers.processors.PerlVariableCompletionProcessor;
import com.perl5.lang.perl.idea.completion.util.PerlPackageCompletionUtil;
import com.perl5.lang.perl.psi.PerlAnnotationType;
import com.perl5.lang.perl.util.PerlTimeLogger;
import consulo.application.util.registry.Registry;
import consulo.language.editor.completion.CompletionParameters;
import consulo.language.editor.completion.CompletionResultSet;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import consulo.language.psi.PsiElement;
import consulo.language.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import static com.perl5.lang.perl.idea.PerlElementPatterns.*;


public class PerlPackageCompletionProvider extends PerlCompletionProvider {
  @Override
  public void addCompletions(@NotNull CompletionParameters parameters,
                                @NotNull ProcessingContext context,
                                @NotNull CompletionResultSet result) {
    PsiElement element = parameters.getPosition();

    PerlSimpleCompletionProcessor completionProcessor = new PerlSimpleCompletionProcessor(parameters, result, element);

    PerlTimeLogger logger = PerlTimeLogger.create(LOG);

    if (NAMESPACE_IN_DEFINITION_PATTERN.accepts(element)) { // package Foo
      PerlPackageCompletionUtil.fillWithNamespaceNameSuggestions(completionProcessor);
      logger.debug("Filled with NS suggestions");
    }
    else if (NAMESPACE_IN_VARIABLE_DECLARATION_PATTERN.accepts(element)) { // my Foo::Bar
      PerlPackageCompletionUtil.processAllNamespacesNames(completionProcessor);
      logger.debug("Processed all namespace names");
    }
    else if (NAMESPACE_IN_ANNOTATION_PATTERN.accepts(element)) { // #@returns / #@type
      completionProcessor.processSingle(LookupElementBuilder.create("ArrayRef")
                                                            .withInsertHandler(PerlInsertHandlers.ARRAY_ELEMENT_INSERT_HANDLER)
                                                            .withTailText("[]"));
      completionProcessor.processSingle(LookupElementBuilder.create("HashRef")
                                          .withInsertHandler(PerlInsertHandlers.ARRAY_ELEMENT_INSERT_HANDLER)
                                          .withTailText("[]"));
      if (Registry.is("perl5.completion.var.without.sigil", true) && element.getParent() instanceof PerlAnnotationType typeAnnotation &&
          typeAnnotation.getAnnotationVariable() == null) {
        // fill with sigiliess variables
        PerlVariableCompletionProcessor variableCompletionProcessor =
          PerlAnnotationVariablesCompletionProvider.createProcessor(parameters, result, element);

        PerlAnnotationVariablesCompletionProvider.processTargets(typeAnnotation, variableCompletionProcessor, true);
        variableCompletionProcessor.logStatus(getClass());
      }

      PerlPackageCompletionUtil.processAllNamespacesNames(completionProcessor);
      logger.debug("Processed all namespace names");
    }
    else if (NAMESPACE_IN_USE_PATTERN.accepts(element)) { // use/no/require
      PerlPackageCompletionUtil.fillWithVersionNumbers(completionProcessor);
      logger.debug("Processed all version numbers");
      PerlPackageCompletionUtil.fillWithAllPackageFiles(completionProcessor);
      logger.debug("Processed all package files");
    }
    else { // fallback
      PerlPackageCompletionUtil.processAllNamespacesNames(completionProcessor);
      logger.debug("Processed all namespace names");
    }
    completionProcessor.logStatus(getClass());
  }
}
