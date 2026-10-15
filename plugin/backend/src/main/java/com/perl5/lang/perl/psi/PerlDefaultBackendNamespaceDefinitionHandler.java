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

import consulo.util.lang.StringUtil;
import consulo.language.psi.scope.GlobalSearchScope;
import com.perl5.lang.perl.extensions.packageprocessor.PerlExportDescriptor;
import com.perl5.lang.perl.util.PerlNamespaceUtil;
import com.perl5.lang.perl.util.PerlPackageUtil;
import com.perl5.lang.perl.util.processors.PerlNamespaceEntityProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class PerlDefaultBackendNamespaceDefinitionHandler<T extends PerlNamespaceDefinitionElement> implements PerlNamespaceDefinitionHandler<T> {
  @Override
  public boolean processExportDescriptors(
    @NotNull T namespace,
    @NotNull PerlNamespaceEntityProcessor<? super PerlExportDescriptor> processor
  ) {
    String namespaceName = namespace.getNamespaceName();
    if (namespaceName == null) {
      return true;
    }
    if (StringUtil.isEmpty(namespaceName)) {
      return true;
    }
    return PerlNamespaceUtil.processExportDescriptors(
      namespace.getProject(), GlobalSearchScope.fileScope(namespace.getContainingFile().getOriginalFile()), namespaceName, processor
    );
  }

  @Override
  public @NotNull List<PerlNamespaceDefinitionElement> getParentNamespaceDefinitions(@NotNull T namespace) {
    return PerlNamespaceUtil.collectNamespaceDefinitions(namespace.getProject(), namespace.getParentNamespacesNames());
  }

  @Override
  public @NotNull List<PerlNamespaceDefinitionElement> getChildNamespaceDefinitions(@NotNull T namespace) {
    return PerlPackageUtil.getChildNamespaces(namespace.getProject(), namespace.getNamespaceName());
  }
}
