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

package com.perl5.lang.perl.extensions.packageprocessor;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.application.Application;
import consulo.component.extension.ExtensionPoint;
import consulo.component.extension.ExtensionPointCacheKey;
import consulo.disposer.Disposable;
import consulo.application.ApplicationManager;
import consulo.util.lang.lazy.ClearableLazyValue;
import jakarta.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ServiceAPI(ComponentScope.APPLICATION)
@ServiceImpl
@Singleton
public final class PerlPackageProcessorService implements Disposable {
  // TODO consulo: IntelliJ KeyedExtensionCollector (keys declared in plugin.xml) is missing in Consulo; package processors are bound
  //  to package names by @ExtensionImpl of PerlPackageProcessorService.KeyedLazyInstance instead, upstream xml registrations are not ported yet
  public static final KeyedExtensionCollector EP = new KeyedExtensionCollector();

  private final ClearableLazyValue<Integer> myVersionProvider = ClearableLazyValue.atomicNotNull(() -> {
    int version = 0;
    for (KeyedLazyInstance instance : Objects.requireNonNull(EP.getPoint()).getExtensionList()) {
      version += instance.getInstance().getVersion();
    }
    return version;
  });

  public PerlPackageProcessorService() {
    // TODO consulo: no extension point change listeners (no dynamic plugins); upstream drops the version on packageProcessor EP changes
    //Objects.requireNonNull(EP.getPoint()).addChangeListener(myVersionProvider::drop, this);
  }

  public static int getVersion() {
    return ApplicationManager.getApplication().getService(PerlPackageProcessorService.class).myVersionProvider.get();
  }

  @Override
  public void dispose() {
  }

  /**
   * consulo: replacement of IntelliJ {@code KeyedLazyInstanceEP} for the {@code com.perl5.packageProcessor} extension point,
   * binds a package name ({@code key}) to the processor
   */
  @ExtensionAPI(ComponentScope.APPLICATION)
  public interface KeyedLazyInstance {
    @NotNull
    String getKey();

    @NotNull
    PerlPackageProcessor getInstance();
  }

  /**
   * consulo: replacement of IntelliJ {@code KeyedExtensionCollector} over {@link KeyedLazyInstance} extensions
   */
  public static final class KeyedExtensionCollector {
    private static final ExtensionPointCacheKey<KeyedLazyInstance, Map<String, PerlPackageProcessor>> CACHE_KEY =
      ExtensionPointCacheKey.create("PerlPackageProcessorService.EP", walker -> {
        Map<String, PerlPackageProcessor> result = new HashMap<>();
        walker.walk(instance -> result.putIfAbsent(instance.getKey(), instance.getInstance()));
        return result;
      });

    private KeyedExtensionCollector() {
    }

    public @NotNull ExtensionPoint<KeyedLazyInstance> getPoint() {
      return Application.get().getExtensionPoint(KeyedLazyInstance.class);
    }

    public @Nullable PerlPackageProcessor findSingle(@NotNull String key) {
      return getPoint().getOrBuildCache(CACHE_KEY).get(key);
    }
  }
}
