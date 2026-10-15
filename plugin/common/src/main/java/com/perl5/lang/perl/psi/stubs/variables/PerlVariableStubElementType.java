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

package com.perl5.lang.perl.psi.stubs.variables;

import com.perl5.lang.perl.PerlLanguage;
import com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlValueSerializer;
import com.perl5.lang.perl.parser.elementTypes.PsiElementProvider;
import com.perl5.lang.perl.psi.PerlVariableDeclarationElement;
import com.perl5.lang.perl.psi.impl.PsiPerlVariableDeclarationElementImpl;
import com.perl5.lang.perl.psi.stubs.PerlStubSerializationUtil;
import com.perl5.lang.perl.psi.utils.PerlVariableAnnotations;
import com.perl5.lang.perl.psi.utils.PerlVariableType;
import com.perl5.lang.perl.util.PerlPackageUtilCore;
import com.perl5.lang.perl.util.PerlValuesUtil;
import com.perl5.lang.perl.util.PerlVariableUtil;
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

// consulo: IStubElementType instead of IElementType, the element type creates, (de)serializes and indexes its stubs
//  (upstream: PerlVariableStubSerializingFactory registered in PerlStubRegistryExtension)
public class PerlVariableStubElementType extends IStubElementType<PerlVariableDeclarationStub, PerlVariableDeclarationElement>
  implements PsiElementProvider {
  public PerlVariableStubElementType(@NotNull String debugName) {
    super(debugName, PerlLanguage.INSTANCE);
  }

  @Override
  public @NotNull PsiElement getPsiElement(@NotNull ASTNode node) {
    return new PsiPerlVariableDeclarationElementImpl(node);
  }

  @Override
  public @NotNull String getExternalId() {
    return "perl." + this;
  }

  @Override
  public PerlVariableDeclarationElement createPsi(@NotNull PerlVariableDeclarationStub stub) {
    return new PsiPerlVariableDeclarationElementImpl(stub, this);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull PerlVariableDeclarationStub createStub(@NotNull PerlVariableDeclarationElement psi, @Nullable StubElement parentStub) {
    return new PerlVariableDeclarationStub(
      parentStub,
      this,
      Objects.requireNonNull(psi.getNamespaceName()),
      Objects.requireNonNull(psi.getName()),
      psi.getDeclaredValue(),
      psi.getActualType(),
      psi.getLocalVariableAnnotations()
    );
  }

  @Override
  public void serialize(@NotNull PerlVariableDeclarationStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getNamespaceName());
    dataStream.writeName(stub.getVariableName());
    PerlValueSerializer.serialize(stub.getDeclaredValue(), dataStream);
    dataStream.writeByte(stub.getActualType().ordinal());
    PerlVariableAnnotations annotations = stub.getVariableAnnotations();
    if (annotations.isEmpty()) {
      dataStream.writeBoolean(false);
    }
    else {
      dataStream.writeBoolean(true);
      dataStream.writeByte(annotations.getFlags());
      PerlValueSerializer.serialize(annotations.getAnnotatedValue(), dataStream);
    }
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull PerlVariableDeclarationStub deserialize(@NotNull StubInputStream dataStream, @Nullable StubElement parentStub)
    throws IOException {
    return new PerlVariableDeclarationStub(
      parentStub,
      this,
      PerlStubSerializationUtil.readNotNullString(dataStream),
      PerlStubSerializationUtil.readNotNullString(dataStream),
      PerlValuesUtil.readValue(dataStream),
      PerlVariableType.values()[dataStream.readByte()],
      readAnnotations(dataStream)
    );
  }

  private static @NotNull PerlVariableAnnotations readAnnotations(@NotNull StubInputStream dataStream) throws IOException {
    return dataStream.readBoolean()
           ? new PerlVariableAnnotations(dataStream.readByte(), PerlValuesUtil.readValue(dataStream))
           : PerlVariableAnnotations.empty();
  }

  @Override
  public void indexStub(@NotNull PerlVariableDeclarationStub stub, @NotNull IndexSink sink) {
    String variableName = PerlPackageUtilCore.join(stub.getNamespaceName(), stub.getVariableName());
    var indexKeys = PerlVariableUtil.getIndexKey(stub);
    sink.occurrence(indexKeys.getFirst(), variableName);
    sink.occurrence(indexKeys.getSecond(), stub.getNamespaceName());
  }

  @Override
  public boolean shouldCreateStub(@NotNull ASTNode node) {
    PsiElement psi = node.getPsi();
    return psi instanceof PerlVariableDeclarationElement variableDeclarationElement &&
           variableDeclarationElement.isValid() &&
           variableDeclarationElement.isGlobalDeclaration() &&
           StringUtil.isNotEmpty(variableDeclarationElement.getName()) &&
           StringUtil.isNotEmpty(variableDeclarationElement.getNamespaceName());
  }
}
