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
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.stub.PsiFileStubImpl;
import consulo.language.psi.stub.StubElement;
import consulo.language.util.IncorrectOperationException;
import com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlValue;
import com.perl5.lang.perl.psi.stubs.PerlLightElementStub;
import com.perl5.lang.perl.psi.utils.PerlSubAnnotations;
import com.perl5.lang.perl.psi.utils.PerlSubArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PerlLightSubDefinitionStub extends PerlSubDefinitionStub implements PerlLightElementStub {
  private final @Nullable StubElement<?> myParent;
  private boolean myIsImplicit = false;

  public PerlLightSubDefinitionStub(@Nullable StubElement<?> myParent,
                                    @Nullable String packageName,
                                    @NotNull String subName,
                                    @NotNull List<PerlSubArgument> arguments,
                                    @Nullable PerlSubAnnotations annotations,
                                    @NotNull PerlValue returnValueFromCode,
                                    @NotNull IStubElementType<?, ?> elementType) {
    super(new PsiFileStubImpl<PsiFile>(null), packageName, subName, arguments, annotations, returnValueFromCode, elementType);
    this.myParent = myParent;
  }

  @Override
  public @Nullable StubElement<?> getParentStub() {
    return myParent;
  }

  @Override
  public <E extends PsiElement> E getParentStubOfType(@NotNull Class<E> parentClass) {
    throw new IncorrectOperationException("NYI");
  }

  @Override
  public String toString() {
    return getStubType().toString() + ":" + super.toString();
  }

  @Override
  public boolean isImplicit() {
    return myIsImplicit;
  }

  @Override
  public void setImplicit(boolean isImplicit) {
    myIsImplicit = isImplicit;
  }
}
