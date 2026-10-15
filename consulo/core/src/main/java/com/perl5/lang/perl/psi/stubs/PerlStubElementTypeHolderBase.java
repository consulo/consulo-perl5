package com.perl5.lang.perl.psi.stubs;

import consulo.language.ast.IElementType;
import consulo.language.psi.stub.ObjectStubSerializer;
import consulo.language.psi.stub.ObjectStubSerializerProvider;
import consulo.language.psi.stub.StubElementTypeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Base of the {@link StubElementTypeHolder}s of the perl plugins (upstream: {@code stubElementTypeHolder} in plugin xml).
 * <p>
 * Stub element types and stub file element types are their own serializers, so the platform registers them by their external ids when it
 * initializes the serializers. Holders are not lazy: external ids have own prefixes ({@code perl.}, {@code PodSection },
 * {@code HTML::Mason::}, {@code perl5.file.}) not matching field names, and holder classes contain other element types too.
 * {@link #loadSerializers()} just makes sure all stub element types of the plugin are instantiated before that moment.
 */
public abstract class PerlStubElementTypeHolderBase<T> extends StubElementTypeHolder<T> {
  @Override
  public final @Nullable String getExternalIdPrefix() {
    return null;
  }

  @Override
  public final List<ObjectStubSerializerProvider> loadSerializers() {
    List<ObjectStubSerializerProvider> result = new ArrayList<>();
    for (IElementType elementType : getStubElementTypes()) {
      if (elementType instanceof ObjectStubSerializer<?, ?> serializer) {
        result.add(new Provider(serializer));
      }
      else {
        throw new IllegalStateException("Not a stub element type: " + elementType + "; " +
                                        (elementType == null ? null : elementType.getClass()) + "; holder: " + getClass());
      }
    }
    return result;
  }

  /**
   * @return all stub element types and stub file element types of the plugin, with non-default external ids
   */
  protected abstract @NotNull List<IElementType> getStubElementTypes();

  private static final class Provider implements ObjectStubSerializerProvider {
    private final @NotNull ObjectStubSerializer<?, ?> mySerializer;

    private Provider(@NotNull ObjectStubSerializer<?, ?> serializer) {
      mySerializer = serializer;
    }

    @Override
    public String getExternalId() {
      return mySerializer.getExternalId();
    }

    @Override
    public boolean isLazy() {
      return false;
    }

    @Override
    public ObjectStubSerializer getObjectStubSerializer() {
      return mySerializer;
    }
  }
}
