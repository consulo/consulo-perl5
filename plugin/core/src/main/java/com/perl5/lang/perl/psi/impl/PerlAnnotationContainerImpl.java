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

package com.perl5.lang.perl.psi.impl;

import com.perl5.lang.perl.psi.PerlAnnotation;
import com.perl5.lang.perl.psi.PerlAnnotationContainer;
import consulo.language.ast.ASTNode;
import consulo.language.impl.psi.ASTWrapperPsiElement;
import consulo.language.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PerlAnnotationContainerImpl extends ASTWrapperPsiElement implements PerlAnnotationContainer {
  public PerlAnnotationContainerImpl(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public @Nullable PerlAnnotation getAnnotation() {
    PsiElement annotation = getFirstChild();
    return annotation instanceof PerlAnnotation ? (PerlAnnotation)annotation : null;
  }
}
