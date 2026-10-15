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

package com.perl5.lang.mason2;

import consulo.project.Project;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.stub.StubIndex;
import com.perl5.lang.htmlmason.MasonCoreUtil;
import com.perl5.lang.mason2.idea.configuration.Mason2SourceRootType;
import com.perl5.lang.mason2.psi.MasonNamespaceDefinition;
import com.perl5.lang.mason2.psi.stubs.MasonNamespaceDefitnitionsStubIndex;
import com.perl5.lang.perl.idea.project.PerlProjectManager;
import com.perl5.lang.perl.psi.PerlNamespaceDefinitionElement;
import com.perl5.lang.perl.util.PerlFileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public final class Mason2Util {
  private Mason2Util() {
  }

  public static @Nullable String getVirtualFileClassName(@NotNull Project project, @Nullable VirtualFile componentFile) {
    if (componentFile != null && componentFile.isValid()) {
      VirtualFile componentRoot = getComponentRoot(project, componentFile);
      if (componentRoot != null) {
        //noinspection ConstantConditions
        return Mason2UtilCore.getClassnameFromPath(VirtualFileUtil.getRelativePath(componentFile, componentRoot));
      }
    }

    return null;
  }

  public static @Nullable VirtualFile getComponentRoot(@NotNull Project project, @Nullable VirtualFile file) {
    return MasonCoreUtil.getComponentRoot(project, Mason2SourceRootType.INSTANCE, file);
  }

  /**
   * consulo: upstream takes the roots from the project settings, which provide the root type
   *
   * @return components roots of the modules with the Mason2 module extension enabled
   */
  public static @NotNull List<VirtualFile> getComponentRoots(@NotNull Project project) {
    return PerlProjectManager.getInstance(project).getModulesRootsOfType(Mason2SourceRootType.INSTANCE);
  }

  public static List<PerlNamespaceDefinitionElement> getMasonNamespacesByAbsolutePath(@NotNull Project project,
                                                                                      @NotNull String absolutePath) {
    return new ArrayList<>(
      StubIndex.getElements(
        MasonNamespaceDefitnitionsStubIndex.KEY,
        absolutePath,
        project,
        GlobalSearchScope.projectScope(project),
        MasonNamespaceDefinition.class
      )
    );
  }

  public static @NotNull List<PerlNamespaceDefinitionElement> collectComponentNamespacesByPaths(@NotNull Project project,
                                                                                                @NotNull List<String> componentPaths,
                                                                                                @NotNull VirtualFile anchorDir) {
    List<PerlNamespaceDefinitionElement> result = new ArrayList<>();
    for (String componentPath : componentPaths) {
      VirtualFile componentFile = null;
      if (componentPath
        .startsWith("" + VirtualFileUtil.VFS_SEPARATOR_CHAR)) // abs path relative to mason roots, see the Mason::Interp::_determine_parent_compc
      {
        for (VirtualFile componentRoot : getComponentRoots(project)) {
          componentFile = componentRoot.findFileByRelativePath(componentPath.substring(1));
          if (componentFile != null) {
            break;
          }
        }
      }
      else // relative path
      {
        componentFile = anchorDir.findFileByRelativePath(componentPath);
      }

      if (componentFile != null) {
        String absolutePath = PerlFileUtil.getPathRelativeToContentRoot(componentFile, project);
        if (absolutePath != null) {
          result.addAll(Mason2Util.getMasonNamespacesByAbsolutePath(project, absolutePath));
        }
      }
    }

    return result;
  }
}
