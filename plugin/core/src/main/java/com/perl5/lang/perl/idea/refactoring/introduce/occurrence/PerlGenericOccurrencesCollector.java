/*
 * Copyright 2015-2018 Alexandr Evstigneev
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

package com.perl5.lang.perl.idea.refactoring.introduce.occurrence;

import com.perl5.lang.perl.idea.refactoring.introduce.PerlIntroduceTarget;
import com.perl5.lang.perl.psi.utils.PerlPsiUtil;
import consulo.language.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

class PerlGenericOccurrencesCollector extends PerlIntroduceTargetOccurrencesCollector {
  public PerlGenericOccurrencesCollector(@NotNull PerlIntroduceTarget target) {
    super(target);
  }

  @Override
  protected boolean collectOccurrences(@NotNull PsiElement element) {
    PsiElement targetElement = getTargetElement();
    if (PerlPsiUtil.areElementsSame(targetElement, element)) {
      addOccurrence(PerlIntroduceTarget.create(element));
      return true;
    }
    return false;
  }
}
