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

package com.perl5.lang.pod.parser.psi.references;

import consulo.project.util.query.QueryExecutorBase;
import consulo.util.lang.StringUtil;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiReference;
import consulo.language.psi.search.ReferencesSearch;
import consulo.language.psi.search.ReferencesSearchQueryExecutor;
import com.perl5.lang.pod.parser.psi.PodTitledSection;
import com.perl5.lang.pod.parser.psi.util.PodRenderUtil;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class PodReferencesSearch extends QueryExecutorBase<PsiReference, ReferencesSearch.SearchParameters>
  implements ReferencesSearchQueryExecutor {
  public PodReferencesSearch() {
    super(true);
  }

  @Override
  public void processQuery(@NotNull ReferencesSearch.SearchParameters queryParameters, @NotNull Predicate<? super PsiReference> consumer) {
    final PsiElement element = queryParameters.getElementToSearch();
    if (!(element instanceof PodTitledSection podTitledSection)) {
      return;
    }
    final String textTitle = podTitledSection.getTitleText();
    if (!StringUtil.isNotEmpty(textTitle)) {
      return;
    }
    String longestWord = "";
    for (String chunk : textTitle.split("[^\\w_]+")) {
      if (chunk.length() > longestWord.length()) {
        longestWord = chunk;
      }
    }
    queryParameters.getOptimizer().searchWord(longestWord, queryParameters.getEffectiveSearchScope(), true, element);
    String escapedTitle = PodRenderUtil.escapeTitle(textTitle);
    if (!StringUtil.equals(escapedTitle, textTitle)) {
      queryParameters.getOptimizer().searchWord(escapedTitle, queryParameters.getEffectiveSearchScope(), true, element);
    }
  }
}
