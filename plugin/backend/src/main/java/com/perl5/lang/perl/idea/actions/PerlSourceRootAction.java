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

package com.perl5.lang.perl.idea.actions;

import consulo.application.Application;
import consulo.content.base.ExcludedContentFolderTypeProvider;
import consulo.language.content.LanguageContentFolderScopes;
import consulo.language.content.ProjectRootsUtil;
import consulo.module.Module;
import consulo.module.content.ModuleRootManager;
import consulo.module.content.ProjectFileIndex;
import consulo.module.content.layer.ModifiableRootModel;
import consulo.module.content.layer.ModuleRootModel;
import consulo.util.lang.Comparing;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import consulo.project.Project;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.AnActionWithSyncUpdate;
import consulo.ui.ex.action.DumbAwareAction;
import consulo.module.content.layer.ContentEntry;
import consulo.module.content.layer.ContentFolder;
import consulo.virtualFileSystem.VirtualFile;
import consulo.language.util.IncorrectOperationException;
import consulo.util.collection.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * consulo: IntelliJ {@code MarkRootActionBase} does not exist in Consulo, the part of it used by perl actions is ported here
 * ({@link #actionPerformed}, {@link #update}, {@link #getSelection}, {@link RootsSelection})
 */
public abstract class PerlSourceRootAction extends DumbAwareAction implements AnActionWithSyncUpdate {

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    VirtualFile[] files = e.getData(VirtualFile.KEY_OF_ARRAY);
    final Module module = getModule(e, files);
    if (module == null) {
      return;
    }
    modifyRoots(e, module, files);
  }

  protected void modifyRoots(@NotNull AnActionEvent e, @NotNull Module module, VirtualFile @NotNull [] files) {
    final ModifiableRootModel model = ModuleRootManager.getInstance(module).getModifiableModel();
    for (VirtualFile file : files) {
      ContentEntry entry = findContentEntry(model, file);
      if (entry != null) {
        for (ContentFolder folder : entry.getFolders(LanguageContentFolderScopes.all())) {
          if (Comparing.equal(folder.getFile(), file)) {
            entry.removeFolder(folder);
            break;
          }
        }
        modifyRoots(file, entry);
      }
    }
    Application.get().runWriteAction(model::commit);
  }

  private static @Nullable ContentEntry findContentEntry(@NotNull ModuleRootModel model, @NotNull VirtualFile vFile) {
    for (ContentEntry contentEntry : model.getContentEntries()) {
      VirtualFile contentEntryFile = contentEntry.getFile();
      if (contentEntryFile != null && VirtualFileUtil.isAncestor(contentEntryFile, vFile, false)) {
        return contentEntry;
      }
    }
    return null;
  }

  @Override
  public void update(@NotNull AnActionEvent e) {
    RootsSelection selection = getSelection(e);
    Module module = selection.myModule;
    boolean enabled = module != null && (!selection.mySelectedRoots.isEmpty() || !selection.mySelectedDirectories.isEmpty()) &&
                      selection.mySelectedExcludeRoots.isEmpty() && isEnabled(selection, module);
    e.getPresentation().setVisible(enabled);
    e.getPresentation().setEnabled(enabled);
  }

  protected final boolean isEnabled(@NotNull RootsSelection selection, @NotNull Module module) {
    return isEnabled(getFilesFromSelection(selection), module);
  }

  protected final @NotNull List<VirtualFile> getFilesFromSelection(@NotNull RootsSelection selection) {
    List<VirtualFile> roots = new ArrayList<>();
    roots.addAll(selection.mySelectedDirectories);
    roots.addAll(ContainerUtil.map(selection.mySelectedRoots, ContentFolder::getFile));
    return roots;
  }

  protected abstract boolean isEnabled(@NotNull List<? extends VirtualFile> files, @NotNull Module module);

  protected void modifyRoots(VirtualFile file, ContentEntry entry) {
    throw new IncorrectOperationException();
  }

  protected static @NotNull RootsSelection getSelection(@NotNull AnActionEvent e) {
    VirtualFile[] files = e.getData(VirtualFile.KEY_OF_ARRAY);
    Module module = getModule(e, files);
    if (module == null) {
      return RootsSelection.EMPTY;
    }

    RootsSelection selection = new RootsSelection(module);
    for (VirtualFile file : files) {
      if (!file.isDirectory()) {
        return RootsSelection.EMPTY;
      }
      ContentFolder folder = ProjectRootsUtil.findContentFolderForDirectory(file, module.getProject());
      if (folder != null && ExcludedContentFolderTypeProvider.getInstance().equals(folder.getType())) {
        selection.mySelectedExcludeRoots.add(file);
      }
      else if (folder != null) {
        selection.mySelectedRoots.add(folder);
      }
      else {
        selection.mySelectedDirectories.add(file);
      }
    }
    return selection;
  }

  private static @Nullable Module getModule(@NotNull AnActionEvent e, VirtualFile @Nullable [] files) {
    if (files == null) {
      return null;
    }
    Module module = e.getData(Module.KEY);
    if (module == null) {
      module = findParentModule(e.getData(Project.KEY), files);
    }
    return module;
  }

  private static @Nullable Module findParentModule(@Nullable Project project, VirtualFile @NotNull [] files) {
    if (project == null) {
      return null;
    }
    Module result = null;
    ProjectFileIndex fileIndex = ProjectFileIndex.getInstance(project);
    for (VirtualFile file : files) {
      Module module = fileIndex.getModuleForFile(file, false);
      if (module == null) {
        return null;
      }
      if (result == null) {
        result = module;
      }
      else if (!result.equals(module)) {
        return null;
      }
    }
    return result;
  }

  protected static class RootsSelection {
    public static final RootsSelection EMPTY = new RootsSelection(null);

    public final List<ContentFolder> mySelectedRoots = new ArrayList<>();
    public final List<VirtualFile> mySelectedDirectories = new ArrayList<>();
    public final List<VirtualFile> mySelectedExcludeRoots = new ArrayList<>();
    public final @Nullable Module myModule;

    public RootsSelection(@Nullable Module module) {
      myModule = module;
    }
  }
}
