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
import com.perl5.lang.htmlmason.parser.psi.HTMLMasonArgsBlock;
import com.perl5.lang.htmlmason.parser.psi.impl.HTMLMasonArgsBlockImpl;
import com.perl5.lang.htmlmason.parser.stubs.HTMLMasonArgsBlockStub;
import com.perl5.lang.htmlmason.parser.stubs.impl.HTMLMasonArgsBlockStubImpl;
import com.perl5.lang.perl.parser.elementTypes.PsiElementProvider;
import com.perl5.lang.perl.psi.stubs.subsdefinitions.PerlSubDefinitionElementType;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.IndexSink;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

// consulo: IStubElementType instead of IElementType, the element type creates and (de)serializes its stubs
//  (upstream: HTMLMasonArgsBlockStubSerializingFactory registered in HTMLMasonStubRegistryExtension)
public final class HTMLMasonArgsBlockElementType extends IStubElementType<HTMLMasonArgsBlockStub, HTMLMasonArgsBlock>
  implements PsiElementProvider {
  public HTMLMasonArgsBlockElementType(@NonNls @NotNull String debugName) {
    super(debugName, HTMLMasonLanguage.INSTANCE);
  }

  @Override
  public @NotNull PsiElement getPsiElement(@NotNull ASTNode node) {
    return new HTMLMasonArgsBlockImpl(node);
  }

  @Override
  public @NotNull String getExternalId() {
    return "HTML::Mason::" + this;
  }

  @Override
  public HTMLMasonArgsBlock createPsi(@NotNull HTMLMasonArgsBlockStub stub) {
    return new HTMLMasonArgsBlockImpl(stub, this);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull HTMLMasonArgsBlockStub createStub(@NotNull HTMLMasonArgsBlock psi, @Nullable StubElement parentStub) {
    return new HTMLMasonArgsBlockStubImpl(parentStub, this, psi.getArgumentsList());
  }

  @Override
  public void serialize(@NotNull HTMLMasonArgsBlockStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    PerlSubDefinitionElementType.serializeArguments(dataStream, stub.getArgumentsList());
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull HTMLMasonArgsBlockStub deserialize(@NotNull StubInputStream dataStream, @Nullable StubElement parentStub)
    throws IOException {
    return new HTMLMasonArgsBlockStubImpl(parentStub, this, PerlSubDefinitionElementType.deserializeArguments(dataStream));
  }

  @Override
  public void indexStub(@NotNull HTMLMasonArgsBlockStub stub, @NotNull IndexSink sink) {
  }
}
