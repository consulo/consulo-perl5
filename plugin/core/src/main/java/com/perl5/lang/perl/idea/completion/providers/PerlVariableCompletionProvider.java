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

package com.perl5.lang.perl.idea.completion.providers;

import com.perl5.lang.perl.idea.completion.providers.processors.PerlVariableCompletionProcessor;
import com.perl5.lang.perl.idea.completion.providers.processors.PerlVariableCompletionProcessorImpl;
import com.perl5.lang.perl.idea.completion.util.PerlVariableCompletionUtil;
import com.perl5.lang.perl.psi.PsiPerlMethod;
import com.perl5.lang.perl.util.PerlPackageUtil;
import com.perl5.lang.perl.util.PerlTimeLogger;
import consulo.application.util.registry.Registry;
import consulo.language.editor.completion.CompletionParameters;
import consulo.language.editor.completion.CompletionResultSet;
import consulo.language.psi.PsiElement;
import consulo.language.util.ProcessingContext;
import consulo.util.lang.StringUtil;
import org.jetbrains.annotations.NotNull;

public class PerlVariableCompletionProvider extends PerlCompletionProvider {
  @Override
  public void addCompletions(@NotNull CompletionParameters parameters,
                                @NotNull ProcessingContext context,
                                @NotNull CompletionResultSet resultSet) {
    if (!Registry.is("perl5.completion.var.without.sigil", true)) {
      return;
    }

    PsiElement subName = parameters.getPosition();
    PsiElement method = subName.getParent();

    String namespaceName = null;
    if (!(method instanceof PsiPerlMethod psiPerlMethod) || psiPerlMethod.isObjectMethod()) {
      return;
    }
    namespaceName = psiPerlMethod.getExplicitNamespaceName();
    if (StringUtil.isNotEmpty(namespaceName)) {
      resultSet = resultSet.withPrefixMatcher(PerlPackageUtil.join(namespaceName, resultSet.getPrefixMatcher().getPrefix()));
    }

    PerlVariableCompletionProcessor variableCompletionProcessor = new PerlVariableCompletionProcessorImpl(
      parameters, resultSet, subName, namespaceName, false, false, false);

    PerlTimeLogger logger = PerlTimeLogger.create(LOG);
    PerlVariableCompletionUtil.processVariables(variableCompletionProcessor, logger);
    variableCompletionProcessor.logStatus(getClass());
  }
}
