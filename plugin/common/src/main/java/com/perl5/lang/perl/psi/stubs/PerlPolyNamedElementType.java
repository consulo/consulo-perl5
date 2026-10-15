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

package com.perl5.lang.perl.psi.stubs;

import com.perl5.lang.perl.PerlLanguage;
import com.perl5.lang.perl.parser.elementTypes.PsiElementProvider;
import com.perl5.lang.perl.psi.impl.PerlPolyNamedElement;
import com.perl5.lang.perl.psi.light.PerlDelegatingLightNamedElement;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.psi.PsiElement;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.IndexSink;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import consulo.logging.Logger;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.perl5.lang.perl.parser.Class.Accessor.ClassAccessorElementTypes.CLASS_ACCESSOR_METHOD;
import static com.perl5.lang.perl.psi.stubs.PerlStubElementTypes.LIGHT_ATTRIBUTE_DEFINITION;
import static com.perl5.lang.perl.psi.stubs.PerlStubElementTypes.LIGHT_METHOD_DEFINITION;
import static com.perl5.lang.perl.psi.stubs.PerlStubElementTypes.LIGHT_NAMESPACE_DEFINITION;
import static com.perl5.lang.perl.psi.stubs.PerlStubElementTypes.LIGHT_SUB_DEFINITION;

// consulo: IStubElementType instead of IElementType, the element type creates, (de)serializes and indexes its stubs and stubs of
//  its light elements (upstream: PerlPolynamedStubSerializingFactory)
public abstract class PerlPolyNamedElementType<Stub extends PerlPolyNamedElementStub<Psi>, Psi extends PerlPolyNamedElement<Stub>>
  extends IStubElementType<Stub, Psi> implements PsiElementProvider {
  private static final Logger LOG = Logger.getInstance(PerlPolyNamedElementType.class);

  /**
   * Serialization ids of light elements. Lazy holder, so light element types are accessed on the first (de)serialization only,
   * not during initialization of {@link PerlStubElementTypes}
   */
  private static final class SerializationIds {
    private static final Object2IntOpenHashMap<IElementType> DIRECT_MAP = new Object2IntOpenHashMap<>();
    // consulo: fleet.fastutil is not available, plain fastutil is used
    private static final Int2ObjectOpenHashMap<IElementType> REVERSE_MAP = new Int2ObjectOpenHashMap<>();

    static {
      // 0 is reserved for n/a
      DIRECT_MAP.put(LIGHT_SUB_DEFINITION, 1);
      DIRECT_MAP.put(LIGHT_NAMESPACE_DEFINITION, 2);
      DIRECT_MAP.put(LIGHT_METHOD_DEFINITION, 3);
      DIRECT_MAP.put(CLASS_ACCESSOR_METHOD, 4);
      DIRECT_MAP.put(LIGHT_ATTRIBUTE_DEFINITION, 5);
      LOG.assertTrue(DIRECT_MAP.size() == 5);

      for (Object2IntMap.Entry<IElementType> entry : DIRECT_MAP.object2IntEntrySet()) {
        REVERSE_MAP.put(entry.getIntValue(), entry.getKey());
      }
    }
  }

  public PerlPolyNamedElementType(@NotNull String debugName) {
    super(debugName, PerlLanguage.INSTANCE);
  }

  @Override
  public @NotNull String getExternalId() {
    return "perl." + this;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public final @NotNull Stub createStub(@NotNull Psi psi, @Nullable StubElement parentStub) {
    List<StubElement<?>> lightNamedElements = new ArrayList<>();
    Stub result = createStub(psi, parentStub, lightNamedElements);

    for (PerlDelegatingLightNamedElement<?> lightPsi : psi.getLightElements()) {
      StubElement<?> lightStubElement = getStubSerializer(lightPsi.getElementType()).createStub(lightPsi, result);
      if (lightStubElement instanceof PerlLightElementStub lightElementStub && lightPsi.isImplicit()) {
        lightElementStub.setImplicit(true);
      }
      lightNamedElements.add(lightStubElement);
    }

    return result;
  }

  /**
   * Creates the main psi element stub. the {@code lightElementsStubs} goingn to be populated with light elements stubs later, so it should
   * be stored as is
   */
  protected abstract @NotNull Stub createStub(@NotNull Psi psi,
                                              @Nullable StubElement<?> parentStub,
                                              @NotNull List<StubElement<?>> lightElementsStubs);

  @SuppressWarnings("rawtypes")
  private static @NotNull IStubElementType getStubSerializer(@NotNull StubElement<?> childStub) {
    return getStubSerializer(childStub.getStubType());
  }

  /**
   * consulo: upstream asks {@code StubElementRegistryService} for non-{@code IStubElementType} element types, all perl light element
   * types are {@link IStubElementType}s in Consulo
   */
  @SuppressWarnings("rawtypes")
  private static @NotNull IStubElementType getStubSerializer(@Nullable IElementType elementType) {
    if (elementType instanceof IStubElementType<?, ?> stubElementType) {
      return stubElementType;
    }
    throw new IllegalArgumentException(
      "Don't know how to serialize:" + elementType + "; " + (elementType == null ? null : elementType.getClass())
    );
  }

  @SuppressWarnings("unchecked")
  @Override
  public final void serialize(@NotNull Stub stub, @NotNull StubOutputStream dataStream) throws IOException {
    List<StubElement<?>> childrenStubs = new ArrayList<>();
    for (StubElement<?> it : stub.getLightNamedElementsStubs()) {
      if (!(it instanceof PerlLightElementStub lightElementStub) || !lightElementStub.isImplicit()) {
        childrenStubs.add(it);
      }
    }
    dataStream.writeVarInt(childrenStubs.size());
    serializeStub(stub, dataStream);
    for (StubElement<?> childStub : childrenStubs) {
      dataStream.writeVarInt(getSerializationId(childStub)); // serialization id
      getStubSerializer(childStub).serialize(childStub, dataStream);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public final void indexStub(@NotNull Stub stub, @NotNull IndexSink sink) {
    for (StubElement<?> childStub : stub.getLightNamedElementsStubs()) {
      getStubSerializer(childStub).indexStub(childStub, sink);
    }
    doIndexStub(stub, sink);
  }

  @Override
  public boolean shouldCreateStub(@NotNull ASTNode node) {
    PsiElement psi = node.getPsi();
    assert psi instanceof PerlPolyNamedElement<?>;
    return !((PerlPolyNamedElement<?>)psi).getLightElements().isEmpty();
  }

  @SuppressWarnings("rawtypes")
  @Override
  public final @NotNull Stub deserialize(@NotNull StubInputStream dataStream, @Nullable StubElement parentStub) throws IOException {
    int size = dataStream.readVarInt();
    List<StubElement<?>> childStubs = new ArrayList<>(size);
    Stub result = deserialize(dataStream, parentStub, childStubs);

    for (int i = 0; i < size; i++) {
      childStubs.add((StubElement<?>)getStubSerializer(getElementTypeById(dataStream.readVarInt())).deserialize(dataStream, result));
    }

    return result;
  }

  private static int getSerializationId(@NotNull StubElement<?> stubElement) {
    int id = SerializationIds.DIRECT_MAP.getInt(stubElement.getStubType());
    if (id > 0) {
      return id;
    }
    throw new IllegalArgumentException("Unregistered stub element class:" + stubElement.getStubType());
  }

  private static @NotNull IElementType getElementTypeById(int id) {
    IElementType elementType = SerializationIds.REVERSE_MAP.get(id);
    if (elementType == null) {
      throw new IllegalArgumentException("Unregistered stub element id:" + id);
    }
    return elementType;
  }

  protected void doIndexStub(@NotNull Stub stub, @NotNull IndexSink sink) {
  }

  // to save additional data in subclasses
  protected void serializeStub(@NotNull Stub stub, @NotNull StubOutputStream dataStream) throws IOException {
  }

  protected abstract @NotNull Stub deserialize(@NotNull StubInputStream dataStream,
                                               @Nullable StubElement<?> parentStub,
                                               @NotNull List<StubElement<?>> lightElementsStubs) throws IOException;
}
