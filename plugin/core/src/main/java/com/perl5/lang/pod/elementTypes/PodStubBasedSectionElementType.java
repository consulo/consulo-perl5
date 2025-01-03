/*
 * Copyright 2015-2020 Alexandr Evstigneev
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

package com.perl5.lang.pod.elementTypes;

import com.perl5.lang.perl.parser.elementTypes.PsiElementProvider;
import com.perl5.lang.perl.psi.stubs.PerlStubSerializationUtil;
import com.perl5.lang.pod.PodLanguage;
import com.perl5.lang.pod.parser.psi.mixin.PodStubBasedSection;
import com.perl5.lang.pod.parser.psi.stubs.PodSectionStub;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.stub.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public abstract class PodStubBasedSectionElementType<Psi extends PodStubBasedSection> extends IStubElementType<PodSectionStub, Psi>
  implements PsiElementProvider {
  public PodStubBasedSectionElementType(@NotNull @NonNls String debugName) {
    super(debugName, PodLanguage.INSTANCE);
  }

  @Override
  public @NotNull String getExternalId() {
    return "PodSection " + super.toString();
  }

  @Override
  public void serialize(@NotNull PodSectionStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    dataStream.writeName(stub.getContent());
  }

  @Override
  public @NotNull PodSectionStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) throws IOException {
    return new PodSectionStub(parentStub, this, Objects.requireNonNull(PerlStubSerializationUtil.readString(dataStream)));
  }

  @Override
  public void indexStub(@NotNull PodSectionStub stub, @NotNull IndexSink sink) {
  }

  @Override
  public final boolean shouldCreateStub(ASTNode node) {
    PsiElement psi = node.getPsi();
    //noinspection unchecked
    return psi != null && shouldCreateStub((Psi)psi);
  }

  protected boolean shouldCreateStub(@NotNull Psi psi) {
    return psi.isIndexed();
  }

  @Override
  public String toString() {
    return "Perl5 POD: " + super.toString();
  }
}
