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

package com.perl5.lang.mojolicious.idea.projectView;

import consulo.annotation.component.ExtensionImpl;
import consulo.ui.ex.tree.PresentationData;
import consulo.project.ui.view.tree.TreeStructureProvider;
import consulo.project.ui.view.tree.ViewSettings;
import consulo.project.ui.view.tree.PsiDirectoryNode;
import consulo.project.ui.view.tree.AbstractTreeNode;
import consulo.project.Project;
import consulo.util.lang.StringUtil;
import consulo.util.collection.ContainerUtil;
import com.perl5.lang.mojolicious.model.MojoProject;
import com.perl5.lang.mojolicious.model.MojoProjectManager;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;

@ExtensionImpl
public class MojoTreeStructureProvider implements TreeStructureProvider {
  private final @NotNull MojoProjectManager myMojoProjectManager;

  @Inject
  public MojoTreeStructureProvider(@NotNull Project project) {
    myMojoProjectManager = MojoProjectManager.getInstance(project);
  }

  @Override
  @SuppressWarnings("rawtypes")
  public @Unmodifiable @NotNull Collection<AbstractTreeNode> modify(@NotNull AbstractTreeNode parent,
                                                                    @NotNull Collection<AbstractTreeNode> children,
                                                                    ViewSettings settings) {
    if (!myMojoProjectManager.isMojoAvailable()) {
      return children;
    }

    return ContainerUtil.map(children, node -> {
      if (!(node instanceof PsiDirectoryNode directoryNode)) {
        return node;
      }

      MojoProject mojoProject = myMojoProjectManager.getMojoProject(directoryNode.getVirtualFile());
      if (mojoProject == null) {
        return node;
      }

      return new PsiDirectoryNode(node.getProject(), directoryNode.getValue(), directoryNode.getSettings()) {
        @Override
        protected void updateImpl(@NotNull PresentationData data) {
          super.updateImpl(data);
          data.setIcon(mojoProject.getIcon());
          if (StringUtil.isEmpty(data.getLocationString())) {
            data.setLocationString(mojoProject.getTypeName().toLowerCase());
          }
        }
      };
    });
  }
}
