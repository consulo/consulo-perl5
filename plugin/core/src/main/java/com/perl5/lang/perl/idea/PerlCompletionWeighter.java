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

package com.perl5.lang.perl.idea;

import com.perl5.lang.perl.PerlLanguage;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.completion.CompletionLocation;
import consulo.language.editor.completion.CompletionWeigher;
import consulo.language.editor.completion.lookup.LookupElement;
import consulo.language.psi.PsiUtilCore;
import consulo.util.dataholder.Key;
import org.jetbrains.annotations.NotNull;

@ExtensionImpl
public class PerlCompletionWeighter extends CompletionWeigher {
  public static final Key<Integer> WEIGHT = new Key<>("WEIGHT");

  @Override
  public Comparable<?> weigh(@NotNull LookupElement element, @NotNull CompletionLocation location) {
    if (PsiUtilCore.findLanguageFromElement(location.getCompletionParameters().getPosition()).isKindOf(PerlLanguage.INSTANCE)) {
      Integer weight = element.getUserData(WEIGHT);
      return weight == null ? 0 : weight;
    }

    return 0;
  }
}
