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

package com.perl5.lang.perl.psi.stubs.namespaces;

import com.perl5.lang.perl.PerlLanguage;
import com.perl5.lang.perl.parser.elementTypes.PsiElementProvider;
import com.perl5.lang.perl.psi.PerlNamespaceDefinitionElement;
import com.perl5.lang.perl.psi.impl.PsiPerlNamespaceDefinitionImpl;
import com.perl5.lang.perl.psi.mro.PerlMroType;
import com.perl5.lang.perl.psi.stubs.PerlStubSerializationUtil;
import com.perl5.lang.perl.psi.utils.PerlNamespaceAnnotations;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.IndexSink;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubIndexKey;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import consulo.util.lang.StringUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.perl5.lang.perl.psi.stubs.namespaces.PerlNamespaceDescendantsIndex.NAMESPACE_DESCENDANTS_KEY;
import static com.perl5.lang.perl.psi.stubs.namespaces.PerlNamespaceIndex.NAMESPACE_KEY;

// consulo: IStubElementType instead of IElementType, the element type creates, (de)serializes and indexes its stubs
//  (upstream: PerlNamespaceDefinitionStubSerializingFactory registered in PerlStubRegistryExtension)
public class PerlNamespaceDefinitionElementType extends IStubElementType<PerlNamespaceDefinitionStub, PerlNamespaceDefinitionElement>
  implements PsiElementProvider {
  public PerlNamespaceDefinitionElementType(@NotNull String name) {
    super(name, PerlLanguage.INSTANCE);
  }

  public PerlNamespaceDefinitionElementType(@NotNull @NonNls String debugName, @Nullable Language language) {
    super(debugName, language);
  }

  @Override
  public @NotNull PsiElement getPsiElement(@NotNull ASTNode node) {
    return new PsiPerlNamespaceDefinitionImpl(node);
  }

  @Override
  public @NotNull String getExternalId() {
    return "perl." + this;
  }

  @Override
  public PerlNamespaceDefinitionElement createPsi(@NotNull PerlNamespaceDefinitionStub stub) {
    return new PsiPerlNamespaceDefinitionImpl(stub, this);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull PerlNamespaceDefinitionStub createStub(@NotNull PerlNamespaceDefinitionElement psi, @Nullable StubElement parentStub) {
    return createStubElement(parentStub, new PerlNamespaceDefinitionData(psi));
  }

  @Override
  public void serialize(@NotNull PerlNamespaceDefinitionStub stub, @NotNull StubOutputStream dataStream) throws IOException {
    serializeNamespaceData(dataStream, stub.getData());
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull PerlNamespaceDefinitionStub deserialize(@NotNull StubInputStream dataStream, @Nullable StubElement parentStub)
    throws IOException {
    return createStubElement(parentStub, deserializeNamespaceData(dataStream));
  }

  @Override
  public void indexStub(@NotNull PerlNamespaceDefinitionStub stub, @NotNull IndexSink sink) {
    String name = stub.getNamespaceName();
    sink.occurrence(getNamespacesIndexKey(), name);

    for (String parent : stub.getParentNamespacesNames()) {
      if (parent != null && !parent.isEmpty()) {
        sink.occurrence(getDescendantsIndexKey(), parent);
      }
    }
  }

  @Override
  public boolean shouldCreateStub(@NotNull ASTNode node) {
    PsiElement psi = node.getPsi();
    return psi instanceof PerlNamespaceDefinitionElement namespaceDefinitionElement &&
           namespaceDefinitionElement.isValid() &&
           StringUtil.isNotEmpty(namespaceDefinitionElement.getNamespaceName());
  }

  protected @NotNull StubIndexKey<String, ? extends PsiElement> getNamespacesIndexKey() {
    return NAMESPACE_KEY;
  }

  protected @NotNull StubIndexKey<String, ? extends PsiElement> getDescendantsIndexKey() {
    return NAMESPACE_DESCENDANTS_KEY;
  }

  @SuppressWarnings("rawtypes")
  protected @NotNull PerlNamespaceDefinitionStub createStubElement(@Nullable StubElement parentStub,
                                                                   @NotNull PerlNamespaceDefinitionData data) {
    return new PerlNamespaceDefinitionStub(parentStub, this, data);
  }

  public static void serializeNamespaceData(@NotNull StubOutputStream dataStream, @NotNull PerlNamespaceDefinitionData namespaceData)
    throws IOException {
    dataStream.writeName(namespaceData.getNamespaceName());
    dataStream.writeName(namespaceData.getMroType().toString());
    PerlStubSerializationUtil.writeStringsList(dataStream, namespaceData.getParentNamespacesNames());
    PerlStubSerializationUtil.writeStringsList(dataStream, namespaceData.getEXPORT());
    PerlStubSerializationUtil.writeStringsList(dataStream, namespaceData.getEXPORT_OK());
    PerlStubSerializationUtil.writeStringListMap(dataStream, namespaceData.getEXPORT_TAGS());
    PerlNamespaceAnnotations namespaceAnnotations = namespaceData.getAnnotations();
    if (namespaceAnnotations == null) {
      dataStream.writeBoolean(false);
    }
    else {
      dataStream.writeBoolean(true);
      dataStream.writeByte(namespaceAnnotations.getFlags());
    }
  }

  public static @NotNull PerlNamespaceDefinitionData deserializeNamespaceData(@NotNull StubInputStream dataStream) throws IOException {
    String packageName = Objects.requireNonNull(PerlStubSerializationUtil.readString(dataStream));
    PerlMroType mroType = PerlMroType.valueOf(Objects.requireNonNull(PerlStubSerializationUtil.readString(dataStream)));
    List<String> parentNamespaces = Objects.requireNonNull(PerlStubSerializationUtil.readStringsList(dataStream));
    List<String> export = Objects.requireNonNull(PerlStubSerializationUtil.readStringsList(dataStream));
    List<String> exportOk = Objects.requireNonNull(PerlStubSerializationUtil.readStringsList(dataStream));
    Map<String, List<String>> exportTags = Objects.requireNonNull(PerlStubSerializationUtil.readStringListMap(dataStream));
    return new PerlNamespaceDefinitionData(
      packageName,
      mroType,
      parentNamespaces,
      export,
      exportOk,
      exportTags,
      dataStream.readBoolean() ? new PerlNamespaceAnnotations(dataStream.readByte()) : null
    );
  }
}
