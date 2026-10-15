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

package com.perl5.lang.perl.psi;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.application.Application;
import consulo.component.extension.ExtensionPoint;
import consulo.component.extension.ExtensionPointCacheKey;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import com.perl5.lang.perl.psi.impl.PerlSubCallElement;
import com.perl5.lang.perl.psi.stubs.calls.PerlSubCallElementData;
import com.perl5.lang.perl.psi.stubs.calls.PerlSubCallElementStub;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class PerlSubCallHandler<CallData extends PerlSubCallElementData>
  implements PerlLightElementProvider<PerlSubCallElement, PerlSubCallElementStub>,
             PerlSelfHinter {
  // TODO consulo: IntelliJ KeyedExtensionCollector (keys declared in plugin.xml) is missing in Consulo; handlers are bound to sub names
  //  by @ExtensionImpl of PerlSubCallHandler.KeyedLazyInstance instead, upstream xml registrations are not ported yet
  private static final KeyedExtensionCollector EP = new KeyedExtensionCollector();

  public final @NotNull CallData getCallData(@NotNull PerlSubCallElement subCallElement) {
    PerlSubCallElementStub stub = subCallElement.getGreenStub();
    if (stub != null) {
      //noinspection unchecked
      return (CallData)stub.getCallData();
    }
    return computeCallData(subCallElement);
  }

  public abstract @NotNull CallData computeCallData(@NotNull PerlSubCallElement subCallElement);

  public void serialize(@NotNull PerlSubCallElementData callData, @NotNull StubOutputStream dataStream) throws IOException {
  }

  public abstract CallData deserialize(@NotNull StubInputStream dataStream) throws IOException;

  @Contract("null -> null")
  public static @Nullable PerlSubCallHandler<?> getHandler(@Nullable String subName) {
    if (subName == null) {
      return null;
    }
    return EP.findSingle(subName);
  }

  static @NotNull ExtensionPoint<@NotNull KeyedLazyInstance> getExtensionPoint() {
    return Objects.requireNonNull(EP.getPoint());
  }

  /**
   * consulo: replacement of IntelliJ {@code KeyedLazyInstanceEP} for the {@code com.perl5.subCallHandler} extension point,
   * binds a sub name ({@code key}) to the handler
   */
  @ExtensionAPI(ComponentScope.APPLICATION)
  public interface KeyedLazyInstance {
    @NotNull
    String getKey();

    @NotNull
    PerlSubCallHandler<?> getInstance();
  }

  /**
   * consulo: replacement of IntelliJ {@code KeyedExtensionCollector} over {@link KeyedLazyInstance} extensions
   */
  private static final class KeyedExtensionCollector {
    private static final ExtensionPointCacheKey<KeyedLazyInstance, Map<String, PerlSubCallHandler<?>>> CACHE_KEY =
      ExtensionPointCacheKey.create("PerlSubCallHandler.EP", walker -> {
        Map<String, PerlSubCallHandler<?>> result = new HashMap<>();
        walker.walk(instance -> result.putIfAbsent(instance.getKey(), instance.getInstance()));
        return result;
      });

    @NotNull
    ExtensionPoint<KeyedLazyInstance> getPoint() {
      return Application.get().getExtensionPoint(KeyedLazyInstance.class);
    }

    @Nullable
    PerlSubCallHandler<?> findSingle(@NotNull String key) {
      return getPoint().getOrBuildCache(CACHE_KEY).get(key);
    }
  }
}
