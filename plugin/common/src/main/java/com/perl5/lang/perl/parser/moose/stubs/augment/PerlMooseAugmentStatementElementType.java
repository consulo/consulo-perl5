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

package com.perl5.lang.perl.parser.moose.stubs.augment;

import com.perl5.lang.perl.PerlLanguage;
import com.perl5.lang.perl.parser.elementTypes.PsiElementProvider;
import com.perl5.lang.perl.parser.moose.psi.PerlMooseAugmentStatement;
import com.perl5.lang.perl.parser.moose.psi.impl.PerlMooseAugmentStatementImpl;
import com.perl5.lang.perl.psi.stubs.PerlStubSerializationUtil;
import consulo.language.Language;
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
//  (upstream: PerlMooseAugmentStatementSerializingFactory registered in PerlStubRegistryExtension)
public class PerlMooseAugmentStatementElementType extends IStubElementType<PerlMooseAugmentStatementStub, PerlMooseAugmentStatement>
  implements PsiElementProvider {
  public PerlMooseAugmentStatementElementType(@NonNls @NotNull String debugName, @Nullable Language language) {
    super(debugName, language);
  }

  public PerlMooseAugmentStatementElementType(@NonNls @NotNull String debugName) {
    this(debugName, PerlLanguage.INSTANCE);
  }

  @Override
  public @NotNull PsiElement getPsiElement(@NotNull ASTNode node) {
    return new PerlMooseAugmentStatementImpl(node);
  }

  @Override
  public @NotNull String getExternalId() {
    return "perl." + this;
  }

  @Override
  public PerlMooseAugmentStatement createPsi(@NotNull PerlMooseAugmentStatementStub stub) {
    return new PerlMooseAugmentStatementImpl(stub, this);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull PerlMooseAugmentStatementStub createStub(@NotNull PerlMooseAugmentStatement psi, @Nullable StubElement parentStub) {
    return new PerlMooseAugmentStatementStubImpl(parentStub, this, psi.getSubName());
  }

  @Override
  public void serialize(@NotNull PerlMooseAugmentStatementStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getSubName());
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull PerlMooseAugmentStatementStub deserialize(@NotNull StubInputStream dataStream, @Nullable StubElement parentStub)
    throws IOException {
    return new PerlMooseAugmentStatementStubImpl(parentStub, this, PerlStubSerializationUtil.readString(dataStream));
  }

  @Override
  public void indexStub(@NotNull PerlMooseAugmentStatementStub stub, @NotNull IndexSink sink) {
  }

  @Override
  public boolean shouldCreateStub(@NotNull ASTNode node) {
    PsiElement element = node.getPsi();
    return element instanceof PerlMooseAugmentStatement augmentStatement &&
           augmentStatement.isValid() &&
           StringUtil.isNotEmpty(augmentStatement.getSubName());
  }
}
