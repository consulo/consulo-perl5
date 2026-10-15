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

package com.perl5.lang.perl.psi.stubs.subsdefinitions;

import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.StubElement;
import com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlValue;
import com.perl5.lang.perl.psi.PerlSubDefinition;
import com.perl5.lang.perl.psi.PerlSubDefinitionElement;
import com.perl5.lang.perl.psi.stubs.PerlSubStub;
import com.perl5.lang.perl.psi.utils.PerlSubAnnotations;
import com.perl5.lang.perl.psi.utils.PerlSubArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PerlSubDefinitionStub extends PerlSubStub<PerlSubDefinitionElement> implements PerlSubDefinition {
  private final @NotNull List<PerlSubArgument> myArguments;
  private final @NotNull PerlValue myReturnValueFromCode;

  public PerlSubDefinitionStub(@Nullable StubElement<?> parent,
                               @Nullable String packageName,
                               @NotNull String subName,
                               @NotNull List<PerlSubArgument> myArguments,
                               @Nullable PerlSubAnnotations annotations,
                               @NotNull PerlValue myReturnValueFromCode,
                               @NotNull IStubElementType<?, ?> elementType) {
    super(parent, packageName, subName, annotations, elementType);
    this.myArguments = myArguments;
    this.myReturnValueFromCode = myReturnValueFromCode;
  }

  @Override
  public @NotNull PerlValue getReturnValueFromCode() {
    return myReturnValueFromCode;
  }

  @Override
  public @NotNull List<PerlSubArgument> getSubArgumentsList() {
    return myArguments;
  }

  @Override
  public String toString() {
    return super.toString() + "\n" +
           "\tArguments: " + myArguments + "\n" +
           "\tReturn value: " + myReturnValueFromCode;
  }
}
