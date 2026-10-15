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

package com.perl5.lang.perl.psi.stubs.calls;

import com.perl5.lang.perl.psi.PerlSubCallHandler;
import com.perl5.lang.perl.psi.impl.PerlSubCallElement;
import com.perl5.lang.perl.psi.impl.PsiPerlSubCallImpl;
import com.perl5.lang.perl.psi.stubs.PerlPolyNamedElementType;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

// consulo: stubs are handled by the element type (upstream: PerlSubCallStubSerializingFactory)
public class PerlSubCallElementType extends PerlPolyNamedElementType<PerlSubCallElementStub, PerlSubCallElement> {
  public PerlSubCallElementType(@NotNull String debugName) {
    super(debugName);
  }

  @Override
  public @NotNull PsiElement getPsiElement(@NotNull ASTNode node) {
    return new PsiPerlSubCallImpl(node);
  }

  @Override
  public PerlSubCallElement createPsi(@NotNull PerlSubCallElementStub stub) {
    return new PsiPerlSubCallImpl(stub, this);
  }

  @Override
  protected @NotNull PerlSubCallElementStub createStub(@NotNull PerlSubCallElement psi,
                                                       @Nullable StubElement<?> parentStub,
                                                       @NotNull List<StubElement<?>> lightElementsStubs) {
    return new PerlSubCallElementStub(parentStub, this, lightElementsStubs, Objects.requireNonNull(psi.getSubName()), psi.getCallData());
  }

  @Override
  protected @NotNull PerlSubCallElementStub deserialize(@NotNull StubInputStream dataStream,
                                                        @Nullable StubElement<?> parentStub,
                                                        @NotNull List<StubElement<?>> lightElementsStubs) throws IOException {
    String subName = Objects.requireNonNull(dataStream.readNameString());
    PerlSubCallHandler<?> provider = getProvider(subName);
    return new PerlSubCallElementStub(parentStub, this, lightElementsStubs, subName, provider.deserialize(dataStream));
  }

  @Override
  protected void serializeStub(@NotNull PerlSubCallElementStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getSubName());
    getProvider(stub.getSubName()).serialize(stub.getCallData(), dataStream);
  }

  private static @NotNull PerlSubCallHandler<?> getProvider(@NotNull String subName) {
    PerlSubCallHandler<?> handler = PerlSubCallHandler.getHandler(subName);
    if (handler == null) {
      throw new IllegalArgumentException("Unable to find call handler for " + subName);
    }
    return handler;
  }
}
