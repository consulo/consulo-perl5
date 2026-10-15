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

package com.perl5.lang.perl.idea.codeInsight.typeInference.value.serialization;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.application.Application;
import consulo.component.extension.ByClassGrouper;
import consulo.component.extension.ExtensionPoint;
import consulo.component.extension.ExtensionPointCacheKey;
import com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlValue;
import com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlValueDeserializer;
import com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlValueSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;


public interface PerlValueBackendHelper<Val extends PerlValue> {
  /**
   * consulo: replacement of IntelliJ {@code ClassExtensionPoint} for the {@code com.perl5.valueBackendHelper} extension point,
   * binds a value class ({@code forClass}) to the helper
   */
  @ExtensionAPI(ComponentScope.APPLICATION)
  interface ClassExtensionPoint {
    @NotNull
    Class<?> getForClass();

    @NotNull
    PerlValueBackendHelper<?> getInstance();
  }

  /**
   * consulo: replacement of IntelliJ {@code ClassExtension} over {@link ClassExtensionPoint} extensions
   */
  final class ClassExtension {
    private static final ExtensionPointCacheKey<ClassExtensionPoint, Function<Class, ClassExtensionPoint>> CACHE_KEY =
      ExtensionPointCacheKey.create("PerlValueBackendHelper.EP", ByClassGrouper.build(ClassExtensionPoint::getForClass));

    public @Nullable PerlValueBackendHelper<?> findSingle(@NotNull Class<?> cls) {
      ClassExtensionPoint extension = getPoint().getOrBuildCache(CACHE_KEY).apply(cls);
      return extension == null ? null : extension.getInstance();
    }

    public @NotNull ExtensionPoint<ClassExtensionPoint> getPoint() {
      return Application.get().getExtensionPoint(ClassExtensionPoint.class);
    }
  }


  /**
   * @return a serialization id unique for this value.
   */
  int getSerializationId();

  default void serializeData(@NotNull Val value, @NotNull PerlValueSerializer serializer) throws IOException {
  }

  default @NotNull PerlValue deserialize(@NotNull PerlValueDeserializer deserializer) throws IOException {
    throw new UnsupportedOperationException("This method is not implemented for " + this);
  }

  @SuppressWarnings("unchecked")
  static <Val extends PerlValue> @NotNull PerlValueBackendHelper<Val> get(@NotNull Val value) {
    PerlValueBackendHelper<?> helper = PerlValueBackendHelperKt.EP.findSingle(value.getClass());
    if (helper == null) {
      throw new RuntimeException("No serialization helper for " + value);
    }
    return (PerlValueBackendHelper<Val>)helper;
  }

  static @NotNull PerlValueBackendHelper<?> instance(@NotNull Class<?> clazz) {
    return PerlValueBackendHelperKt.instanceCache.computeIfAbsent(clazz, it ->
      Objects.requireNonNull(PerlValueBackendHelperKt.EP.getPoint()).getExtensionList().stream()
        .map(extension -> extension.getInstance())
        .filter(helper -> helper.getSerializationId() == PerlValueBackendHelperKt.id)
        .findFirst()
        .orElseThrow(() -> new RuntimeException("No serialization helper for id " + PerlValueBackendHelperKt.id))
    );
  }

  // TODO consulo: `inline fun <reified T : PerlValueBackendHelper<*>> instance(): T = instance(T::class.java) as T` has no Java
  //  equivalent (reified type parameter; a generic `instance(Class<T>)` would clash with `instance(Class<?>)`), use `(T)instance(T.class)`

  static @NotNull PerlValueBackendHelper<?> get(int id) {
    return PerlValueBackendHelperKt.idMap.computeIfAbsent(id, it ->
      Objects.requireNonNull(PerlValueBackendHelperKt.EP.getPoint()).getExtensionList().stream()
        .map(extension -> extension.getInstance())
        .filter(helper -> helper.getSerializationId() == id)
        .findFirst()
        .orElseThrow(() -> new RuntimeException("No serialization helper for id " + id))
    );
  }

  int DUPLICATE_ID = PerlValueBackendHelperKt.id++;

  // special values
  int UNKNOWN_ID = PerlValueBackendHelperKt.id++;

  int UNDEF_ID = PerlValueBackendHelperKt.id++;

  int ARGUMENTS_ID = PerlValueBackendHelperKt.id++;

  // primitives
  int SCALAR_ID = PerlValueBackendHelperKt.id++;

  int SCALAR_DEREFERENCE_ID = PerlValueBackendHelperKt.id++;

  int SCALAR_CONTEXT_ID = PerlValueBackendHelperKt.id++;

  int ARRAY_ID = PerlValueBackendHelperKt.id++;

  int ARRAY_ELEMENT_ID = PerlValueBackendHelperKt.id++;

  int ARRAY_SLICE_ID = PerlValueBackendHelperKt.id++;

  int ARRAY_DEREFERENCE_ID = PerlValueBackendHelperKt.id++;

  int UNSHIFT_ID = PerlValueBackendHelperKt.id++;

  int PUSH_ID = PerlValueBackendHelperKt.id++;

  int SUBLIST_ID = PerlValueBackendHelperKt.id++;

  int HASH_ID = PerlValueBackendHelperKt.id++;

  int HASH_ELEMENT_VALUE = PerlValueBackendHelperKt.id++;

  int DEFERRED_HASH_ID = PerlValueBackendHelperKt.id++;

  int HASH_SLICE_ID = PerlValueBackendHelperKt.id++;

  int HASH_DEREFERENCE_ID = PerlValueBackendHelperKt.id++;

  int ARITHMETIC_NEGATION = PerlValueBackendHelperKt.id++;

  int REFERENCE_ID = PerlValueBackendHelperKt.id++;

  int BLESSED_ID = PerlValueBackendHelperKt.id++;

  // synthetic values
  int CALL_STATIC_ID = PerlValueBackendHelperKt.id++;

  int CALL_OBJECT_ID = PerlValueBackendHelperKt.id++;

  int ONE_OF_ID = PerlValueBackendHelperKt.id++;

  int DEFAULT_ARGUMENT_ID = PerlValueBackendHelperKt.id++;

  int SMART_GETTER_ID = PerlValueBackendHelperKt.id++;

  int DUCK_TYPE_ID = PerlValueBackendHelperKt.id++;

  int VALUE_WITH_FALLBACK = PerlValueBackendHelperKt.id++;

  static int getVersion() {
    return PerlValueBackendHelperKt.id + (PerlValue.isDuckTypingEnabled() ? 100 : 0);
  }
}

/**
 * File-level private state of the {@link PerlValueBackendHelper}
 */
final class PerlValueBackendHelperKt {
  // TODO consulo: IntelliJ ClassExtension (forClass declared in plugin.xml) is missing in Consulo; helpers are bound to value classes
  //  by @ExtensionImpl of PerlValueBackendHelper.ClassExtensionPoint instead
  static final PerlValueBackendHelper.ClassExtension EP = new PerlValueBackendHelper.ClassExtension();
  static int id = 0;
  static final Map<Integer, PerlValueBackendHelper<?>> idMap = new ConcurrentHashMap<>();
  static final Map<Class<?>, PerlValueBackendHelper<?>> instanceCache = new ConcurrentHashMap<>();

  private PerlValueBackendHelperKt() {
  }
}
