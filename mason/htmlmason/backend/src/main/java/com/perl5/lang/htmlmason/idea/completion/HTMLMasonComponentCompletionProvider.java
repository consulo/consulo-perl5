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

package com.perl5.lang.htmlmason.idea.completion;

import consulo.language.editor.completion.CompletionInitializationContext;
import consulo.language.editor.completion.CompletionParameters;
import consulo.language.editor.completion.CompletionProvider;
import consulo.language.editor.completion.CompletionResultSet;
import consulo.project.Project;
import consulo.document.util.TextRange;
import consulo.util.lang.StringUtil;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiReference;
import consulo.language.util.ProcessingContext;
import com.perl5.lang.htmlmason.parser.psi.impl.HTMLMasonFileImpl;
import com.perl5.lang.htmlmason.parser.psi.references.HTMLMasonMethodReference;
import com.perl5.lang.perl.psi.PerlString;
import org.jetbrains.annotations.NotNull;


public class HTMLMasonComponentCompletionProvider implements CompletionProvider {
  @Override
  public void addCompletions(@NotNull CompletionParameters parameters,
                                @NotNull ProcessingContext context,
                                @NotNull CompletionResultSet result) {
    PsiElement position = parameters.getPosition();
    PsiElement parent = position.getParent();
    PsiFile file = position.getContainingFile();

    if (!(parent instanceof PerlString) || !(file instanceof HTMLMasonFileImpl htmlMasonFile)) {
      return;
    }
    PsiReference[] references = parent.getReferences();
    for (PsiReference reference : references) {
      TextRange refRange = reference.getRangeInElement();
      if (refRange.contains(position.getStartOffsetInParent())) {
        Project project = position.getProject();

        String fullPrefix = refRange.substring(parent.getText())
          .replace(CompletionInitializationContext.DUMMY_IDENTIFIER, "")
          .replace(CompletionInitializationContext.DUMMY_IDENTIFIER_TRIMMED, "");

        final CompletionResultSet finalResultSet = result.withPrefixMatcher(fullPrefix);

        if (reference instanceof HTMLMasonMethodReference) {
          processMethodReference(references, finalResultSet);
        }
        else {
          processSubcomponentReference(project, htmlMasonFile, fullPrefix, finalResultSet);
        }

        result.stopHere();
        break;
      }
    }
  }

  private static void processSubcomponentReference(@NotNull Project project,
                                                   @NotNull HTMLMasonFileImpl file,
                                                   @NotNull String fullPrefix,
                                                   @NotNull CompletionResultSet resultSet) {
    HTMLMasonCompletionUtil.fillWithComponentSlugs(resultSet);
    HTMLMasonCompletionUtil.fillWithSubcomponents(resultSet, file);
    if (!StringUtil.startsWith(fullPrefix, "/")) {
      HTMLMasonCompletionUtil.fillWithRelativeSubcomponents(resultSet, project, file);
    }
    else {
      HTMLMasonCompletionUtil.fillWithAbsoluteSubcomponents(resultSet, project);
    }
  }

  private static void processMethodReference(@NotNull PsiReference[] references, @NotNull CompletionResultSet resultSet) {
    PsiElement firstReferenceTarget = references[0].resolve();
    if (firstReferenceTarget instanceof HTMLMasonFileImpl masonFile) {
      HTMLMasonCompletionUtil.fillWithMethods(resultSet, masonFile);
    }
  }
}
