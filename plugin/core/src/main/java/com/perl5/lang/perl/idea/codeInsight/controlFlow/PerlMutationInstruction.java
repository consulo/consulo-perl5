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

package com.perl5.lang.perl.idea.codeInsight.controlFlow;

import com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlValue;
import consulo.language.controlFlow.ControlFlowBuilder;
import consulo.language.controlFlow.base.InstructionImpl;
import consulo.language.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PerlMutationInstruction extends InstructionImpl {
  public PerlMutationInstruction(@NotNull ControlFlowBuilder builder,
                                 @Nullable PsiElement element) {
    super(builder, element);
  }

  public abstract @Nullable PsiElement getLeftSide();

  /**
   * @return a new value of the operation target.
   */
  public abstract @NotNull PerlValue createValue();
}
