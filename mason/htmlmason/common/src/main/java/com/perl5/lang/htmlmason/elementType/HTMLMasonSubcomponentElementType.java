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
import com.perl5.lang.htmlmason.parser.psi.HTMLMasonSubcomponentDefitnition;
import com.perl5.lang.htmlmason.parser.psi.impl.HTMLMasonSubcomponentDefitnitionImpl;
import com.perl5.lang.htmlmason.parser.stubs.HTMLMasonSubcomponentDefinitionStub;
import com.perl5.lang.htmlmason.parser.stubs.impl.HTMLMasonSubcomponentDefinitionStubImpl;
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
//  (upstream: HTMLMasonSubcomponentStubSerializingFactory registered in HTMLMasonStubRegistryExtension)
public final class HTMLMasonSubcomponentElementType
  extends IStubElementType<HTMLMasonSubcomponentDefinitionStub, HTMLMasonSubcomponentDefitnition> implements PsiElementProvider {
  public HTMLMasonSubcomponentElementType(@NonNls @NotNull String debugName) {
    super(debugName, HTMLMasonLanguage.INSTANCE);
  }

  @Override
  public @NotNull PsiElement getPsiElement(@NotNull ASTNode node) {
    return new HTMLMasonSubcomponentDefitnitionImpl(node);
  }

  @Override
  public @NotNull String getExternalId() {
    return "HTML::Mason::" + this;
  }

  @Override
  public HTMLMasonSubcomponentDefitnition createPsi(@NotNull HTMLMasonSubcomponentDefinitionStub stub) {
    return new HTMLMasonSubcomponentDefitnitionImpl(stub, this);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull HTMLMasonSubcomponentDefinitionStub createStub(@NotNull HTMLMasonSubcomponentDefitnition psi,
                                                                 @Nullable StubElement parentStub) {
    return new HTMLMasonSubcomponentDefinitionStubImpl(parentStub, this, psi.getName());
  }

  @Override
  public void serialize(@NotNull HTMLMasonSubcomponentDefinitionStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getName());
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull HTMLMasonSubcomponentDefinitionStub deserialize(@NotNull StubInputStream dataStream, @Nullable StubElement parentStub)
    throws IOException {
    return new HTMLMasonSubcomponentDefinitionStubImpl(parentStub, this, PerlStubSerializationUtil.readString(dataStream));
  }

  @Override
  public void indexStub(@NotNull HTMLMasonSubcomponentDefinitionStub stub, @NotNull IndexSink sink) {
  }

  @Override
  public boolean shouldCreateStub(@NotNull ASTNode node) {
    PsiElement psi = node.getPsi();
    return psi instanceof HTMLMasonSubcomponentDefitnition subcomponentDefinition &&
           StringUtil.isNotEmpty(subcomponentDefinition.getName());
  }
}
