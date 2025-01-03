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

package com.perl5.lang.perl.psi.stubs.namespaces;

import com.perl5.lang.perl.psi.PerlNamespaceDefinitionElement;
import consulo.language.psi.PsiElement;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubIndexKey;
import consulo.language.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.perl5.lang.perl.psi.stubs.namespaces.PerlLightNamespaceDescendantsIndex.LIGHT_NAMESPACE_DESCENDANTS_KEY;
import static com.perl5.lang.perl.psi.stubs.namespaces.PerlLightNamespaceIndex.LIGHT_NAMESPACE_KEY;

public class PerlLightNamespaceDefinitionElementType extends PerlNamespaceDefinitionElementType {
  public PerlLightNamespaceDefinitionElementType(String name) {
    super(name);
  }

  @Override
  public final PerlNamespaceDefinitionElement createPsi(@NotNull PerlNamespaceDefinitionStub stub) {
    throw new IncorrectOperationException("Light elements should be created by wrappers, not element types");
  }

  @Override
  protected StubIndexKey<String, ? extends PsiElement> getNamespacesIndexKey() {
    return LIGHT_NAMESPACE_KEY;
  }

  @Override
  protected StubIndexKey<String, ? extends PsiElement> getDescendantsIndexKey() {
    return LIGHT_NAMESPACE_DESCENDANTS_KEY;
  }

  @Override
  protected PerlNamespaceDefinitionStub createStubElement(@Nullable StubElement<?> parentStub, @NotNull PerlNamespaceDefinitionData data) {
    return new PerlLightNamespaceDefinitionStub(parentStub, this, data);
  }
}
