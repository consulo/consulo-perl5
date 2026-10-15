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

package com.perl5.lang.perl.psi.stubs.subsdeclarations;

import com.perl5.lang.perl.PerlLanguage;
import com.perl5.lang.perl.parser.elementTypes.PsiElementProvider;
import com.perl5.lang.perl.psi.PerlSubDeclarationElement;
import com.perl5.lang.perl.psi.impl.PsiPerlSubDeclarationImpl;
import com.perl5.lang.perl.psi.stubs.PerlStubSerializationUtil;
import com.perl5.lang.perl.psi.stubs.subsdefinitions.PerlCallableNamesIndex;
import com.perl5.lang.perl.psi.stubs.subsdefinitions.PerlSubDefinitionElementType;
import com.perl5.lang.perl.psi.utils.PerlSubAnnotations;
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

// consulo: IStubElementType instead of IElementType, the element type creates, (de)serializes and indexes its stubs
//  (upstream: PerlSubDeclarationStubSerializingFactory registered in PerlStubRegistryExtension)
public class PerlSubDeclarationElementType extends IStubElementType<PerlSubDeclarationStub, PerlSubDeclarationElement>
  implements PsiElementProvider {
  public PerlSubDeclarationElementType(String name) {
    super(name, PerlLanguage.INSTANCE);
  }

  @Override
  public @NotNull PsiElement getPsiElement(@NotNull ASTNode node) {
    return new PsiPerlSubDeclarationImpl(node);
  }

  @Override
  public @NotNull String getExternalId() {
    return "perl." + this;
  }

  @Override
  public PerlSubDeclarationElement createPsi(@NotNull PerlSubDeclarationStub stub) {
    return new PsiPerlSubDeclarationImpl(stub, this);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull PerlSubDeclarationStub createStub(@NotNull PerlSubDeclarationElement psi, @Nullable StubElement parentStub) {
    return new PerlSubDeclarationStub(
      parentStub,
      psi.getNamespaceName(),
      psi.getSubName(),
      psi.getAnnotations(),
      this
    );
  }

  @Override
  public void serialize(@NotNull PerlSubDeclarationStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getNamespaceName());
    dataStream.writeName(stub.getSubName());
    PerlSubAnnotations subAnnotations = stub.getAnnotations();
    if (subAnnotations == null) {
      dataStream.writeBoolean(false);
    }
    else {
      dataStream.writeBoolean(true);
      PerlSubDefinitionElementType.writeSubAnnotations(dataStream, subAnnotations);
    }
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull PerlSubDeclarationStub deserialize(@NotNull StubInputStream dataStream, @Nullable StubElement parentStub)
    throws IOException {
    String packageName = PerlStubSerializationUtil.readString(dataStream);
    String subName = PerlStubSerializationUtil.readString(dataStream);
    PerlSubAnnotations annotations = dataStream.readBoolean() ? PerlSubDefinitionElementType.readSubAnnotations(dataStream) : null;
    return new PerlSubDeclarationStub(parentStub, packageName, subName, annotations, this);
  }

  @Override
  public boolean shouldCreateStub(@NotNull ASTNode node) {
    PsiElement psi = node.getPsi();
    return psi instanceof PerlSubDeclarationElement subDeclarationElement &&
           StringUtil.isNotEmpty(subDeclarationElement.getNamespaceName()) &&
           StringUtil.isNotEmpty(subDeclarationElement.getSubName());
  }

  @Override
  public void indexStub(@NotNull PerlSubDeclarationStub stub, @NotNull IndexSink sink) {
    String canonicalName = stub.getCanonicalName();
    if (canonicalName != null) {
      sink.occurrence(PerlSubDeclarationIndex.KEY, canonicalName);
    }

    String packageName = stub.getNamespaceName();
    if (packageName != null) {
      sink.occurrence(PerlSubDeclarationReverseIndex.KEY, packageName);
    }

    String callableName = stub.getCallableName();
    if (StringUtil.isNotEmpty(callableName)) {
      sink.occurrence(PerlCallableNamesIndex.KEY, callableName);
    }
  }
}
