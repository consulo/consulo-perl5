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

package com.perl5.lang.perl.psi.stubs.globs;

import com.perl5.lang.perl.PerlLanguage;
import com.perl5.lang.perl.parser.elementTypes.PsiElementProvider;
import com.perl5.lang.perl.psi.PerlGlobVariableElement;
import com.perl5.lang.perl.psi.PsiPerlGlobVariable;
import com.perl5.lang.perl.psi.impl.PsiPerlGlobVariableImpl;
import com.perl5.lang.perl.psi.stubs.PerlStubSerializationUtil;
import com.perl5.lang.perl.psi.stubs.subsdefinitions.PerlCallableNamesIndex;
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

import static com.perl5.lang.perl.psi.stubs.globs.PerlGlobNamespaceStubIndex.KEY_GLOB_NAMESPACE;
import static com.perl5.lang.perl.psi.stubs.globs.PerlGlobStubIndex.KEY_GLOB;

// consulo: IStubElementType instead of IElementType, the element type creates, (de)serializes and indexes its stubs
//  (upstream: PerlGlobStubSerializingFactory registered in PerlStubRegistryExtension)
public class PerlGlobStubElementType extends IStubElementType<PerlGlobStub, PsiPerlGlobVariable> implements PsiElementProvider {
  public PerlGlobStubElementType(@NotNull String name) {
    super(name, PerlLanguage.INSTANCE);
  }

  @Override
  public @NotNull PsiElement getPsiElement(@NotNull ASTNode node) {
    return new PsiPerlGlobVariableImpl(node);
  }

  @Override
  public @NotNull String getExternalId() {
    return "perl." + this;
  }

  @Override
  public PsiPerlGlobVariable createPsi(@NotNull PerlGlobStub stub) {
    return new PsiPerlGlobVariableImpl(stub, this);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull PerlGlobStub createStub(@NotNull PsiPerlGlobVariable psi, @Nullable StubElement parentStub) {
    return new PerlGlobStub(parentStub, Objects.requireNonNull(psi.getNamespaceName()), psi.getName(), psi.isLeftSideOfAssignment());
  }

  @Override
  public void serialize(@NotNull PerlGlobStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getNamespaceName());
    dataStream.writeName(stub.getGlobName());
    dataStream.writeBoolean(stub.isLeftSideOfAssignment());
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull PerlGlobStub deserialize(@NotNull StubInputStream dataStream, @Nullable StubElement parentStub) throws IOException {
    return new PerlGlobStub(
      parentStub, Objects.requireNonNull(PerlStubSerializationUtil.readString(dataStream)),
      PerlStubSerializationUtil.readString(dataStream), dataStream.readBoolean()
    );
  }

  @Override
  public void indexStub(@NotNull PerlGlobStub stub, @NotNull IndexSink sink) {
    String canonicalName = stub.getCanonicalName();
    if (StringUtil.isNotEmpty(canonicalName)) {
      sink.occurrence(KEY_GLOB, Objects.requireNonNull(canonicalName));
    }
    String namespaceName = stub.getNamespaceName();
    if (StringUtil.isNotEmpty(namespaceName)) {
      sink.occurrence(KEY_GLOB_NAMESPACE, namespaceName);
    }
    String callableName = stub.getCallableName();
    if (StringUtil.isNotEmpty(callableName)) {
      sink.occurrence(PerlCallableNamesIndex.KEY, callableName);
    }
  }

  @Override
  public boolean shouldCreateStub(@NotNull ASTNode node) {
    return node.getPsi() instanceof PerlGlobVariableElement it && it.isValid() && StringUtil.isNotEmpty(it.getCanonicalName());
  }
}
