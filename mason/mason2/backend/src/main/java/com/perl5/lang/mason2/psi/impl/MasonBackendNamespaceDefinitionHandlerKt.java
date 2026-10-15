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

package com.perl5.lang.mason2.psi.impl;

import consulo.virtualFileSystem.util.VirtualFileUtil;
import consulo.virtualFileSystem.VirtualFile;
import com.perl5.lang.htmlmason.MasonCoreUtil;
import com.perl5.lang.mason2.Mason2Util;
import com.perl5.lang.mason2.idea.configuration.MasonSettings;
import com.perl5.lang.mason2.psi.MasonNamespaceDefinition;
import com.perl5.lang.perl.util.PerlFileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Top-level declarations of MasonBackendNamespaceDefinitionHandler.kt upstream.
 * Upstream file-private functions are package-private here, they are used by {@link MasonBackendNamespaceDefinitionHandler}
 */
public final class MasonBackendNamespaceDefinitionHandlerKt {
  private MasonBackendNamespaceDefinitionHandlerKt() {
  }

  static @Nullable String getParentNamespaceFromAutobase(@NotNull MasonNamespaceDefinitionImpl namespaceDefinition) {
    // autobase
    @Nullable VirtualFile componentRoot = getComponentRoot(namespaceDefinition.getContainingFile());
    VirtualFile containingFile = MasonCoreUtil.getContainingVirtualFile(namespaceDefinition.getContainingFile());

    if (componentRoot != null && containingFile != null) {
      @Nullable VirtualFile parentComponentFile =
        getParentComponentFile(namespaceDefinition, componentRoot, containingFile.getParent(), containingFile);
      if (parentComponentFile != null) { // found autobase class
        return PerlFileUtil.getPathRelativeToContentRoot(parentComponentFile, namespaceDefinition.getProject());
      }
    }
    return null;
  }

  /**
   * Recursively traversing paths and looking for autobase
   *
   * @param componentRoot    component root we are search in
   * @param currentDirectory directory we are currently in	 *
   * @param childFile        current file (just to speed things up)
   * @return parent component virtual file or null if not found
   */
  static @Nullable VirtualFile getParentComponentFile(
    @NotNull MasonNamespaceDefinition namespaceDefinition,
    @NotNull VirtualFile componentRoot,
    @NotNull VirtualFile currentDirectory,
    @NotNull VirtualFile childFile
  ) {
    // check in current dir
    // consulo: settings of the module of the component
    List<String> autobaseNames = new ArrayList<>(MasonSettings.forPsiElement(namespaceDefinition).getAutobaseNames());

    if (Objects.equals(childFile.getParent(), currentDirectory) && autobaseNames.contains(childFile.getName())) {// avoid cyclic inheritance
      autobaseNames = autobaseNames.subList(0, autobaseNames.lastIndexOf(childFile.getName()));
    }

    for (int i = autobaseNames.size() - 1; i >= 0; i--) {
      VirtualFile potentialParent = VirtualFileUtil.findRelativeFile(currentDirectory, autobaseNames.get(i));
      if (potentialParent != null && potentialParent.exists() && !potentialParent.equals(childFile)) {
        return potentialParent;
      }
    }

    // move up or exit
    if (!componentRoot.equals(currentDirectory)) {
      return getParentComponentFile(namespaceDefinition, componentRoot, currentDirectory.getParent(), childFile);
    }
    return null;
  }

  /**
   * Returns file path relative to one of the component roots or project root if not under component root
   *
   * @return path, relative to root, null if it's LightVirtualFile without original
   */
  static @Nullable String getComponentPath(@NotNull MasonNamespaceDefinition namespaceDefinition) {
    VirtualFile containingFile = MasonCoreUtil.getContainingVirtualFile(namespaceDefinition.getContainingFile());
    if (containingFile != null) {
      VirtualFile containingRoot = Mason2Util.getComponentRoot(namespaceDefinition.getProject(), containingFile);
      if (containingRoot != null) {
        return VirtualFileUtil.getRelativePath(containingFile, containingRoot);
      }
    }
    return null;
  }

  /**
   * Returns VFS object representing component root
   *
   * @return component root
   */
  public static @Nullable VirtualFile getComponentRoot(@NotNull MasonFileImpl masonFile) {
    return Mason2Util.getComponentRoot(masonFile.getProject(), MasonCoreUtil.getContainingVirtualFile(masonFile));
  }
}
