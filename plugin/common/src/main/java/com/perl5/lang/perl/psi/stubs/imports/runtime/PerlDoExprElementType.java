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

package com.perl5.lang.perl.psi.stubs.imports.runtime;

import com.perl5.lang.perl.PerlLanguage;
import com.perl5.lang.perl.parser.elementTypes.PsiElementProvider;
import com.perl5.lang.perl.psi.PerlDoExpr;
import com.perl5.lang.perl.psi.impl.PsiPerlDoExprImpl;
import com.perl5.lang.perl.psi.stubs.PerlStubSerializationUtil;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.IndexSink;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import consulo.util.lang.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

// consulo: IStubElementType instead of IElementType, the element type creates and (de)serializes its stubs
//  (upstream: PerlDoExprStubSerializingFactory registered in PerlStubRegistryExtension)
public class PerlDoExprElementType extends IStubElementType<PerlRuntimeImportStub, PerlDoExpr> implements PsiElementProvider {
  public PerlDoExprElementType(@NotNull String debugName) {
    super(debugName, PerlLanguage.INSTANCE);
  }

  @Override
  public @NotNull PsiElement getPsiElement(@NotNull ASTNode node) {
    return new PsiPerlDoExprImpl(node);
  }

  @Override
  public @NotNull String getExternalId() {
    return "perl." + this;
  }

  @Override
  public PerlDoExpr createPsi(@NotNull PerlRuntimeImportStub stub) {
    return new PsiPerlDoExprImpl(stub, this);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull PerlRuntimeImportStub createStub(@NotNull PerlDoExpr psi, @Nullable StubElement parentStub) {
    return new PerlRuntimeImportStubImpl(parentStub, this, psi.getImportPath());
  }

  @Override
  public void serialize(@NotNull PerlRuntimeImportStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(Objects.requireNonNullElse(stub.getImportPath(), ""));
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull PerlRuntimeImportStub deserialize(@NotNull StubInputStream dataStream, @Nullable StubElement parentStub)
    throws IOException {
    String importPath = StringUtil.nullize(PerlStubSerializationUtil.readString(dataStream));
    return new PerlRuntimeImportStubImpl(parentStub, this, importPath);
  }

  @Override
  public void indexStub(@NotNull PerlRuntimeImportStub stub, @NotNull IndexSink sink) {
  }
}
