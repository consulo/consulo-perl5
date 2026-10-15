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

package com.perl5.lang.htmlmason.elementType;

import com.perl5.lang.htmlmason.HTMLMasonLanguage;
import com.perl5.lang.htmlmason.parser.psi.HTMLMasonMethodDefinition;
import com.perl5.lang.htmlmason.parser.psi.impl.HTMLMasonMethodDefinitionImpl;
import com.perl5.lang.htmlmason.parser.stubs.HTMLMasonMethodDefinitionStub;
import com.perl5.lang.htmlmason.parser.stubs.impl.HTMLMasonMethodDefinitionStubImpl;
import com.perl5.lang.perl.parser.elementTypes.PsiElementProvider;
import com.perl5.lang.perl.psi.stubs.PerlStubSerializationUtil;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.IndexSink;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import consulo.util.lang.StringUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

// consulo: IStubElementType instead of IElementType, the element type creates and (de)serializes its stubs
//  (upstream: HTMLMasonMethodStubSerializingFactory registered in HTMLMasonStubRegistryExtension)
public final class HTMLMasonMethodElementType extends IStubElementType<HTMLMasonMethodDefinitionStub, HTMLMasonMethodDefinition>
  implements PsiElementProvider {
  public HTMLMasonMethodElementType(@NonNls @NotNull String debugName) {
    super(debugName, HTMLMasonLanguage.INSTANCE);
  }

  @Override
  public @NotNull PsiElement getPsiElement(@NotNull ASTNode node) {
    return new HTMLMasonMethodDefinitionImpl(node);
  }

  @Override
  public @NotNull String getExternalId() {
    return "HTML::Mason::" + this;
  }

  @Override
  public HTMLMasonMethodDefinition createPsi(@NotNull HTMLMasonMethodDefinitionStub stub) {
    return new HTMLMasonMethodDefinitionImpl(stub, this);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull HTMLMasonMethodDefinitionStub createStub(@NotNull HTMLMasonMethodDefinition psi, @Nullable StubElement parentStub) {
    return new HTMLMasonMethodDefinitionStubImpl(parentStub, this, psi.getName());
  }

  @Override
  public void serialize(@NotNull HTMLMasonMethodDefinitionStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull HTMLMasonMethodDefinitionStub deserialize(@NotNull StubInputStream dataStream, @Nullable StubElement parentStub)
    throws IOException {
    return new HTMLMasonMethodDefinitionStubImpl(parentStub, this, PerlStubSerializationUtil.readString(dataStream));
  }

  @Override
  public void indexStub(@NotNull HTMLMasonMethodDefinitionStub stub, @NotNull IndexSink sink) {
  }

  @Override
  public boolean shouldCreateStub(@NotNull ASTNode node) {
    PsiElement psi = node.getPsi();
    return psi instanceof HTMLMasonMethodDefinition methodDefinition && StringUtil.isNotEmpty(methodDefinition.getName());
  }
}
