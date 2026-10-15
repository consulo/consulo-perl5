/*
 * Copyright 2015-2026 Alexandr Evstigneev
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
import consulo.component.extension.ByClassGrouper;
import consulo.component.extension.ExtensionPointCacheKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class PerlNamespaceDefinitionHandlerKt {
  // private top-level property in PerlNamespaceDefinitionHandler.kt; package-private here to be visible from the interface
  // TODO consulo: IntelliJ ClassExtension (forClass declared in plugin.xml) is missing in Consulo; handlers are bound to classes
  //  by @ExtensionImpl of PerlNamespaceDefinitionHandlerKt.ClassExtensionPoint instead, upstream xml registration is not ported yet
  static final ClassExtension EP = new ClassExtension();

  private PerlNamespaceDefinitionHandlerKt() {
  }

  /**
   * consulo: replacement of IntelliJ {@code ClassExtensionPoint} for the {@code com.perl5.namespaceDefinitionHalder} extension point,
   * binds a class ({@code forClass}) to the handler
   */
  @ExtensionAPI(ComponentScope.APPLICATION)
  public interface ClassExtensionPoint {
    @NotNull
    Class<?> getForClass();

    @NotNull
    PerlNamespaceDefinitionHandler<?> getInstance();
  }

  /**
   * consulo: replacement of IntelliJ {@code ClassExtension} over {@link ClassExtensionPoint} extensions
   */
  static final class ClassExtension {
    private static final ExtensionPointCacheKey<ClassExtensionPoint, Function<Class, ClassExtensionPoint>> CACHE_KEY =
      ExtensionPointCacheKey.create("PerlNamespaceDefinitionHandlerKt.EP", ByClassGrouper.build(ClassExtensionPoint::getForClass));

    @Nullable
    PerlNamespaceDefinitionHandler<?> findSingle(@NotNull Class<?> cls) {
      ClassExtensionPoint extension =
        Application.get().getExtensionPoint(ClassExtensionPoint.class).getOrBuildCache(CACHE_KEY).apply(cls);
      return extension == null ? null : extension.getInstance();
    }
  }
}
