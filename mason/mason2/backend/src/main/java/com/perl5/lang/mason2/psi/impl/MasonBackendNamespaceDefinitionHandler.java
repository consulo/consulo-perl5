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

import consulo.project.Project;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.stub.StubIndex;
import com.perl5.lang.htmlmason.MasonCoreUtil;
import com.perl5.lang.mason2.Mason2Constants;
import com.perl5.lang.mason2.Mason2Util;
import com.perl5.lang.mason2.idea.configuration.MasonSettings;
import com.perl5.lang.mason2.psi.MasonNamespaceDefinition;
import com.perl5.lang.mason2.psi.stubs.MasonNamespaceDefitnitionsStubIndex;
import com.perl5.lang.mason2.psi.stubs.MasonParentNamespacesStubIndex;
import com.perl5.lang.perl.psi.PerlDefaultBackendNamespaceDefinitionHandler;
import com.perl5.lang.perl.psi.PerlNamespaceDefinitionElement;
import com.perl5.lang.perl.psi.PerlNamespaceDefinitionHandler;
import com.perl5.lang.perl.util.PerlFileUtil;
import com.perl5.lang.perl.util.PerlNamespaceUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.perl5.lang.mason2.psi.impl.MasonBackendNamespaceDefinitionHandlerKt.getComponentPath;
import static com.perl5.lang.mason2.psi.impl.MasonBackendNamespaceDefinitionHandlerKt.getParentNamespaceFromAutobase;

public final class MasonBackendNamespaceDefinitionHandler extends PerlDefaultBackendNamespaceDefinitionHandler<MasonNamespaceDefinitionImpl> {
  @Override
  public @NotNull List<PerlNamespaceDefinitionElement> getParentNamespaceDefinitions(@NotNull MasonNamespaceDefinitionImpl namespace) {
    List<String> parentsPaths = namespace.getParentNamespacesNames();

    VirtualFile containingFile = MasonCoreUtil.getContainingVirtualFile(namespace.getContainingFile());
    List<PerlNamespaceDefinitionElement> parentsNamespaces;
    if (!parentsPaths.isEmpty() && containingFile != null) {
      parentsNamespaces = Mason2Util.collectComponentNamespacesByPaths(namespace.getProject(), parentsPaths, containingFile.getParent());
    }
    else {
      @Nullable String autobaseParent = getParentNamespaceFromAutobase(namespace);
      if (autobaseParent != null) {
        parentsNamespaces = Mason2Util.getMasonNamespacesByAbsolutePath(namespace.getProject(), autobaseParent);
      }
      else {
        parentsNamespaces = new ArrayList<>();
      }
    }

    if (parentsNamespaces.isEmpty()) {
      parentsNamespaces.addAll(
        PerlNamespaceUtil.getNamespaceDefinitions(
          namespace.getProject(),
          namespace.getResolveScope(),
          Mason2Constants.MASON_DEFAULT_COMPONENT_PARENT
        )
      );
    }

    return parentsNamespaces;
  }

  @Override
  public @NotNull List<PerlNamespaceDefinitionElement> getChildNamespaceDefinitions(@NotNull MasonNamespaceDefinitionImpl namespace) {
    Project project = namespace.getProject();
    // consulo: settings of the module of the component
    MasonSettings masonSettings = MasonSettings.forPsiElement(namespace);
    List<PerlNamespaceDefinitionElement> childNamespaces = new ArrayList<>();


    // collect psi children
    GlobalSearchScope projectScope = GlobalSearchScope.projectScope(project);
    @Nullable String componentPath = getComponentPath(namespace);
    if (componentPath != null) {
      List<String> relativePaths = new ArrayList<>();
      List<String> exactPaths = new ArrayList<>();

      StubIndex.getInstance().processAllKeys(MasonParentNamespacesStubIndex.KEY, project, parentPath -> {
        if (parentPath.charAt(0) == VirtualFileUtil.VFS_SEPARATOR_CHAR) { // absolute path, should be equal
          if (componentPath.equals(parentPath.substring(1))) {
            exactPaths.add(parentPath);
          }
        }
        else if (componentPath.endsWith(parentPath)) {  // relative path
          relativePaths.add(parentPath);
        }
        return true;
      });

      for (String parentPath : exactPaths) {
        childNamespaces.addAll(
          StubIndex.getElements(
            MasonParentNamespacesStubIndex.KEY,
            parentPath,
            project,
            projectScope,
            MasonNamespaceDefinition.class
          )
        );
      }

      for (String parentPath : relativePaths) {
        for (MasonNamespaceDefinition masonNamespaceDefinition : StubIndex.getElements(
          MasonParentNamespacesStubIndex.KEY,
          parentPath,
          project,
          projectScope,
          MasonNamespaceDefinition.class
        )) {
          if (PerlNamespaceDefinitionHandler.instance(masonNamespaceDefinition)
            .getParentNamespaceDefinitions(masonNamespaceDefinition).contains(namespace)
          ) {
            childNamespaces.add(masonNamespaceDefinition);
          }
        }
      }
    }


    // collect autobased children
    if (masonSettings.getAutobaseNames().contains(namespace.getContainingFile().getName())) {
      VirtualFile containingFile = MasonCoreUtil.getContainingVirtualFile(namespace.getContainingFile());
      if (containingFile != null) {
        String basePath = PerlFileUtil.getPathRelativeToContentRoot(containingFile.getParent(), project);

        if (basePath != null) {
          List<String> componentPaths = new ArrayList<>();
          StubIndex.getInstance().processAllKeys(
            MasonNamespaceDefitnitionsStubIndex.KEY, project, indexedPath -> {
              if (indexedPath.startsWith(basePath)) {
                componentPaths.add(indexedPath);
              }
              return true;
            }
          );

          for (String compoPath : componentPaths) {
            for (MasonNamespaceDefinition namespaceDefinition : StubIndex.getElements(
              MasonNamespaceDefitnitionsStubIndex.KEY,
              compoPath,
              project,
              projectScope,
              MasonNamespaceDefinition.class
            )) {
              if (PerlNamespaceDefinitionHandler.instance(namespaceDefinition)
                    .getParentNamespaceDefinitions(namespaceDefinition).contains(namespace)
                  && !childNamespaces.contains(namespaceDefinition)
              ) {
                childNamespaces.add(namespaceDefinition);
              }
            }
          }
        }
      }
    }
    return childNamespaces;
  }
}
