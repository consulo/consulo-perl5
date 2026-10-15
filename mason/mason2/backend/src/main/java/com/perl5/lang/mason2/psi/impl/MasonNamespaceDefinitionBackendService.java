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

import consulo.virtualFileSystem.util.VirtualFileUtil;
import consulo.virtualFileSystem.VirtualFile;
import com.perl5.lang.htmlmason.MasonCoreUtil;
import com.perl5.lang.mason2.Mason2Util;
import com.perl5.lang.mason2.Mason2UtilCore;
import com.perl5.lang.mason2.elementType.MasonNamespaceElementType;
import com.perl5.lang.mason2.psi.MasonNamespaceDefinition;
import com.perl5.lang.perl.util.PerlPackageUtilCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MasonNamespaceDefinitionBackendService implements MasonNamespaceDefinitionService {
  @Override
  public @Nullable String getNamespaceName(@NotNull MasonNamespaceDefinition namespaceDefinition) {
    @Nullable String absoluteComponentPath = MasonNamespaceElementType.getAbsoluteComponentPath(namespaceDefinition);
    if (absoluteComponentPath != null) {
      return Mason2UtilCore.getClassnameFromPath(absoluteComponentPath);
    }
    return null;
  }

  @Override
  public @NotNull String computeNamespaceName(@NotNull MasonNamespaceDefinition namespaceDefinition) {
    String virtualFileClassName = Mason2Util.getVirtualFileClassName(
      namespaceDefinition.getProject(),
      MasonCoreUtil.getContainingVirtualFile(namespaceDefinition.getContainingFile())
    );
    return virtualFileClassName != null ? virtualFileClassName : PerlPackageUtilCore.MAIN_NAMESPACE_NAME;
  }

  @Override
  public @Nullable String getPresentableName(@NotNull MasonNamespaceDefinition namespaceDefinition) {
    VirtualFile componentRoot = MasonBackendNamespaceDefinitionHandlerKt.getComponentRoot((MasonFileImpl)namespaceDefinition.getContainingFile());
    VirtualFile containingFile = MasonCoreUtil.getContainingVirtualFile(namespaceDefinition.getContainingFile());

    if (componentRoot != null && containingFile != null) {
      String componentPath = VirtualFileUtil.getRelativePath(containingFile, componentRoot);

      if (componentPath != null) {
        return VirtualFileUtil.VFS_SEPARATOR_CHAR + componentPath;
      }
    }

    return namespaceDefinition.getName();
  }
}
