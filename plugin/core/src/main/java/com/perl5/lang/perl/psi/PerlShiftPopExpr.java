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

package com.perl5.lang.perl.psi;

import com.intellij.psi.PsiElement;
import com.perl5.lang.perl.psi.mixins.PerlCallArguments;
import com.perl5.lang.perl.psi.references.PerlBuiltInVariablesService;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PerlShiftPopExpr extends PsiPerlExpr {
  /**
   * @return element representing target of this operation, first argument or implicit array
   */
  default @Nullable PsiElement getTarget() {
    PsiPerlCallArguments callArguments = getCallArguments();
    if (callArguments == null) {
      return PerlBuiltInVariablesService.getImplicitArray(getProject());
    }
    List<PsiElement> argumentsList = ((PerlCallArguments)callArguments).getArgumentsList();
    return argumentsList.isEmpty() ? PerlBuiltInVariablesService.getImplicitArray(getProject()) : argumentsList.getFirst();
  }

  @Nullable
  PsiPerlCallArguments getCallArguments();
}
