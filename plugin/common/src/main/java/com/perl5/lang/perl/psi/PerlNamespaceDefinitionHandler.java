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

import com.perl5.lang.perl.extensions.packageprocessor.PerlExportDescriptor;
import com.perl5.lang.perl.util.processors.PerlNamespaceEntityProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.perl5.lang.perl.psi.PerlNamespaceDefinitionHandlerKt.EP;

/**
 * Provides additional functionality for the namespacees.
 */
public interface PerlNamespaceDefinitionHandler<Ns extends PerlNamespaceDefinitionElement> {

  @SuppressWarnings("UnusedReturnValue")
  boolean processExportDescriptors(@NotNull Ns namespace, @NotNull PerlNamespaceEntityProcessor<? super PerlExportDescriptor> processor);

  @NotNull
  List<PerlNamespaceDefinitionElement> getParentNamespaceDefinitions(@NotNull Ns namespace);

  @NotNull
  List<PerlNamespaceDefinitionElement> getChildNamespaceDefinitions(@NotNull Ns namespace);

  default void collectLinearISA(@NotNull Ns namespace, @NotNull Set<String> recursionMap, @NotNull List<String> result) {
    namespace.getMro().getLinearISA(
      getParentNamespaceDefinitions(namespace), recursionMap, result
    );
  }

  @SuppressWarnings("unchecked")
  static <T extends PerlNamespaceDefinitionElement> @NotNull PerlNamespaceDefinitionHandler<T> instance(@NotNull T ns) {
    return (PerlNamespaceDefinitionHandler<T>)instance(ns.getClass());
  }

  static @NotNull PerlNamespaceDefinitionHandler<?> instance(@NotNull Class<?> cls) {
    return Objects.requireNonNull(EP.findSingle(cls));
  }

  // TODO consulo: Kotlin companion member `inline fun <reified T : PerlNamespaceElement> instance(): T = instance(T::class.java) as T`
  //  has no Java equivalent (reified type parameter) and is not callable from Java; omitted (no callers in the repository)
}
