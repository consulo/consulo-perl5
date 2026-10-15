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

import com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlValue;
import com.perl5.lang.perl.psi.PerlSubDefinitionElement;
import com.perl5.lang.perl.psi.light.PerlLightSubDefinitionElement;
import com.perl5.lang.perl.psi.utils.PerlSubAnnotations;
import com.perl5.lang.perl.psi.utils.PerlSubArgument;
import consulo.language.psi.PsiElement;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubIndexKey;
import consulo.language.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

// consulo: stub creation and indexing are in the element type (upstream: PerlLightSubDefinitionStubSerializingFactory)
public class PerlLightSubDefinitionElementType extends PerlSubDefinitionElementType {
  public PerlLightSubDefinitionElementType(@NotNull String name) {
    super(name);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public @NotNull PerlSubDefinitionStub createStub(@NotNull PerlSubDefinitionElement psi, @Nullable StubElement parentStub) {
    if (psi instanceof PerlLightSubDefinitionElement<?> lightSubDefinitionElement && lightSubDefinitionElement.isImplicit()) {
      return createStubElement(
        parentStub,
        lightSubDefinitionElement.getNamespaceName(),
        lightSubDefinitionElement.getSubName(),
        Collections.emptyList(),
        lightSubDefinitionElement.getReturnValueFromCode(),
        null
      );
    }
    return super.createStub(psi, parentStub);
  }

  @Override
  protected @NotNull PerlSubDefinitionStub createStubElement(@Nullable StubElement<? extends PsiElement> parentStub,
                                                             @Nullable String packageName,
                                                             @NotNull String functionName,
                                                             @NotNull List<PerlSubArgument> arguments,
                                                             @NotNull PerlValue returnValueFromCode,
                                                             @Nullable PerlSubAnnotations annotations) {
    return new PerlLightSubDefinitionStub(parentStub, packageName, functionName, arguments, annotations, returnValueFromCode, this);
  }

  @Override
  protected @NotNull StubIndexKey<String, ? extends PsiElement> getDirectKey() {
    return PerlLightSubDefinitionsIndex.KEY;
  }

  @Override
  protected @NotNull StubIndexKey<String, ? extends PsiElement> getReverseKey() {
    return PerlLightSubDefinitionsReverseIndex.KEY;
  }

  @Override
  protected @NotNull StubIndexKey<String, ? extends PsiElement> getCallableNameKey() {
    return PerlLightCallableNamesIndex.KEY;
  }

  @Override
  public PerlSubDefinitionElement createPsi(@NotNull PerlSubDefinitionStub stub) {
    throw new IncorrectOperationException("Light elements should be created by wrappers, not element types");
  }
}
