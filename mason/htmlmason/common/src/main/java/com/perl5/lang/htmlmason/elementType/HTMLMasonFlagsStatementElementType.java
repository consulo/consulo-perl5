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
import com.perl5.lang.htmlmason.parser.psi.HTMLMasonFlagsStatement;
import com.perl5.lang.htmlmason.parser.psi.impl.HTMLMasonFlagsStatementImpl;
import com.perl5.lang.htmlmason.parser.stubs.HTMLMasonFlagsStatementStub;
import com.perl5.lang.htmlmason.parser.stubs.HTMLMasonFlagsStubIndex;
import com.perl5.lang.htmlmason.parser.stubs.impl.HTMLMasonFlagsStatementStubImpl;
import com.perl5.lang.perl.parser.elementTypes.PsiElementProvider;
import consulo.index.io.StringRef;
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

import static com.perl5.lang.htmlmason.parser.psi.HTMLMasonFlagsStatement.UNDEF_RESULT;

// consulo: IStubElementType instead of IElementType, the element type creates, (de)serializes and indexes its stubs
//  (upstream: HTMLMasonFlagsStatementStubSerializingFactory registered in HTMLMasonStubRegistryExtension)
public final class HTMLMasonFlagsStatementElementType extends IStubElementType<HTMLMasonFlagsStatementStub, HTMLMasonFlagsStatement>
  implements PsiElementProvider {
  public HTMLMasonFlagsStatementElementType(@NonNls @NotNull String debugName) {
    super(debugName, HTMLMasonLanguage.INSTANCE);
  }

  @Override
  public @NotNull PsiElement getPsiElement(@NotNull ASTNode node) {
    return new HTMLMasonFlagsStatementImpl(node);
  }

  @Override
  public @NotNull String getExternalId() {
    return "HTML::Mason::" + this;
  }

  @Override
  public HTMLMasonFlagsStatement createPsi(@NotNull HTMLMasonFlagsStatementStub stub) {
    return new HTMLMasonFlagsStatementImpl(stub, this);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull HTMLMasonFlagsStatementStub createStub(@NotNull HTMLMasonFlagsStatement psi, @Nullable StubElement parentStub) {
    return new HTMLMasonFlagsStatementStubImpl(parentStub, this, psi.getParentComponentPath());
  }

  @Override
  public void serialize(@NotNull HTMLMasonFlagsStatementStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    String parentComponentPath = stub.getParentComponentPath();
    if (UNDEF_RESULT.equals(parentComponentPath)) {
      dataStream.writeBoolean(false);
      return;
    }
    dataStream.writeBoolean(true);
    dataStream.writeName(parentComponentPath);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull HTMLMasonFlagsStatementStub deserialize(@NotNull StubInputStream dataStream, @Nullable StubElement parentStub)
    throws IOException {
    if (!dataStream.readBoolean()) {
      return new HTMLMasonFlagsStatementStubImpl(parentStub, this, UNDEF_RESULT);
    }
    @Nullable StringRef nameRef = dataStream.readName();
    return new HTMLMasonFlagsStatementStubImpl(parentStub, this, nameRef == null ? null : nameRef.toString());
  }

  @Override
  public void indexStub(@NotNull HTMLMasonFlagsStatementStub stub, @NotNull IndexSink sink) {
    String parentComponentPath = stub.getParentComponentPath();
    if (parentComponentPath != null && !UNDEF_RESULT.equals(parentComponentPath)) {
      sink.occurrence(HTMLMasonFlagsStubIndex.KEY, parentComponentPath);
    }
  }
}
