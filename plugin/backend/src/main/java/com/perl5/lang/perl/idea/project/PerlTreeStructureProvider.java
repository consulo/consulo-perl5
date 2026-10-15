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

package com.perl5.lang.perl.idea.project;

import consulo.ui.ex.tree.PresentationData;
import consulo.project.ui.view.tree.TreeStructureProvider;
import consulo.project.ui.view.tree.ViewSettings;
import consulo.project.ui.view.tree.PsiDirectoryNode;
import consulo.project.ui.view.tree.AbstractTreeNode;
import consulo.project.Project;
import com.perl5.lang.perl.idea.configuration.settings.sdk.PerlSourceRootEditHandler;
import consulo.annotation.component.ExtensionImpl;
import jakarta.inject.Inject;
import consulo.virtualFileSystem.VirtualFile;
import consulo.util.collection.ContainerUtil;
import com.perl5.lang.perl.idea.modules.PerlSourceRootType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.Map;

@ExtensionImpl
public class PerlTreeStructureProvider implements TreeStructureProvider {
  private final @NotNull PerlProjectManager myProjectManager;

  @Inject
  public PerlTreeStructureProvider(Project project) {
    myProjectManager = PerlProjectManager.getInstance(project);
  }

  @Override
  @SuppressWarnings("rawtypes")
  public @Unmodifiable @NotNull Collection<AbstractTreeNode> modify(@NotNull AbstractTreeNode parent,
                                                                    @NotNull Collection<AbstractTreeNode> children,
                                                                    ViewSettings settings) {
    if (!myProjectManager.isPerlEnabled()) {
      return children;
    }

    Map<VirtualFile, PerlSourceRootType> roots = myProjectManager.getAllModulesRoots();
    return ContainerUtil.map(children, node -> {
      if (!(node instanceof PsiDirectoryNode directoryNode)) {
        return node;
      }

      Project nodeProject = node.getProject();
      if (nodeProject == null) {
        return node;
      }

      // fixme support different roots here
      VirtualFile virtualFile = directoryNode.getVirtualFile();
      if (virtualFile != null && roots.containsKey(virtualFile)) {
        PerlSourceRootType rootType = roots.get(virtualFile);
        PerlSourceRootEditHandler handler = rootType.getEditHandler();
        node = new PsiDirectoryNode(node.getProject(), directoryNode.getValue(), directoryNode.getSettings()) {
          @Override
          protected void updateImpl(@NotNull PresentationData data) {
            super.updateImpl(data);
            data.setIcon(handler.getRootIcon());
            data.setLocationString(handler.getRootTypeName().toLowerCase());
          }
        };
      }

      return node;
    });
  }
}
