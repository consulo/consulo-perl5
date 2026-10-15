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

package com.perl5.lang.mason2.elementType;

import com.perl5.lang.perl.parser.moose.stubs.augment.PerlMooseAugmentStatementStub;
import com.perl5.lang.perl.parser.moose.psi.PerlMooseAugmentStatement;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import com.perl5.lang.mason2.psi.impl.MasonAugmentMethodModifierImpl;
import com.perl5.lang.perl.parser.moose.stubs.augment.PerlMooseAugmentStatementElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public final class MasonAugmentMethodModifierElementType extends PerlMooseAugmentStatementElementType {
  public MasonAugmentMethodModifierElementType(@NonNls @NotNull String debugName) {
    super(debugName);
  }

  @Override
  public @NotNull PsiElement getPsiElement(@NotNull ASTNode node) {
    return new MasonAugmentMethodModifierImpl(node);
  }

  @Override
  public PerlMooseAugmentStatement createPsi(@NotNull PerlMooseAugmentStatementStub stub) {
    return new MasonAugmentMethodModifierImpl(stub, this);
  }
}
