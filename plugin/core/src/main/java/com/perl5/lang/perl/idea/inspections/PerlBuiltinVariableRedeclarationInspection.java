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

package com.perl5.lang.perl.idea.inspections;

import com.perl5.PerlBundle;
import com.perl5.lang.perl.psi.PerlVariable;
import com.perl5.lang.perl.psi.PerlVariableDeclarationElement;
import com.perl5.lang.perl.psi.PsiPerlVariableDeclarationLocal;
import com.perl5.lang.perl.psi.references.PerlBuiltInVariablesService;
import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.psi.PsiElement;


public class PerlBuiltinVariableRedeclarationInspection extends PerlVariableInspectionBase {
  @Override
  public void checkDeclaration(ProblemsHolder holder, PerlVariableDeclarationElement variableDeclarationWrapper) {
    PerlVariable variable = variableDeclarationWrapper.getVariable();
    PsiElement declarationContainer = variableDeclarationWrapper.getParent();
    if (PerlBuiltInVariablesService.getInstance(variableDeclarationWrapper.getProject())
          .getVariableDeclaration(variable.getActualType(), variable.getName()) != null &&
        !(declarationContainer instanceof PsiPerlVariableDeclarationLocal)) {
      registerProblem(holder, variable, PerlBundle.message("perl.inspection.builtin.shadowing"));
    }
  }
}
