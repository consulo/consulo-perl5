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

package com.perl5.lang.mason2.psi.impl;

import consulo.application.Application;
import com.perl5.lang.mason2.psi.MasonNamespaceDefinition;
import consulo.localize.LocalizeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface MasonNamespaceDefinitionService {
  @Nullable
  String getNamespaceName(@NotNull MasonNamespaceDefinition namespaceDefinition);

  @Nullable
  String computeNamespaceName(@NotNull MasonNamespaceDefinition namespaceDefinition);

  LocalizeValue getPresentableName(@NotNull MasonNamespaceDefinition namespaceDefinition);

  static @NotNull MasonNamespaceDefinitionService getInstance() {
    return Application.get().getService(MasonNamespaceDefinitionService.class);
  }
}
