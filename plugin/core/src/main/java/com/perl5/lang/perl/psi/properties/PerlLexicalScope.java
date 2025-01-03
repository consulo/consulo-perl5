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

package com.perl5.lang.perl.psi.properties;

import com.perl5.lang.perl.psi.impl.PerlImplicitVariableDeclaration;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This interface marks that PsiElement has it's own scope
 */
public interface PerlLexicalScope extends PerlLexicalScopeMember, PsiElement, PerlStatementsContainer {
  @Override
  void accept(@NotNull PsiElementVisitor visitor);

  @Contract("null->null")
  static @Nullable PerlLexicalScope from(@Nullable PsiElement element) {
    if (element == null) {
      return null;
    }
    if (element instanceof PerlImplicitVariableDeclaration) {
      return from(element.getParent());
    }
    return PsiTreeUtil.getParentOfType(element, PerlLexicalScope.class);
  }
}
