/*
 * Copyright 2015-2023 Alexandr Evstigneev
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

import consulo.application.ApplicationManager;
import consulo.content.ContentFolderTypeProvider;
import consulo.content.base.ExcludedContentFolderTypeProvider;
import consulo.language.content.LanguageContentFolderScopes;
import consulo.language.content.TestContentFolderTypeProvider;
import consulo.module.content.ModuleRootManager;
import consulo.module.content.layer.ContentEntry;
import consulo.module.content.layer.ContentFolder;
import consulo.module.content.layer.ModifiableRootModel;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import consulo.module.Module;
import consulo.project.Project;
import consulo.module.content.ProjectFileIndex;
import consulo.virtualFileSystem.VirtualFile;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.annotation.access.RequiredWriteAction;
import com.perl5.lang.perl.idea.actions.PerlMarkLibrarySourceRootAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class PerlDirectoryInfoCollector {
  private final Set<VirtualFile> myExternalLibRoots = new LinkedHashSet<>();
  private final Map<Module, ModuleRootInfo> myModulesInfo = new HashMap<>();
  private final ProjectFileIndex myProjectFileIndex;
  private final Project myProject;

  PerlDirectoryInfoCollector(@NotNull Project project) {
    myProject = project;
    myProjectFileIndex = ProjectFileIndex.getInstance(project);
  }

  public void addLibRoot(@NotNull Module module, @Nullable VirtualFile virtualFile) {
    getModuleRootInfo(module).addLibRoot(virtualFile);
  }

  private @NotNull ModuleRootInfo getModuleRootInfo(@NotNull Module module) {
    return myModulesInfo.computeIfAbsent(module, it -> new ModuleRootInfo());
  }

  public void addExternalLibRoot(@Nullable VirtualFile virtualFile) {
    if (virtualFile != null) {
      myExternalLibRoots.add(virtualFile);
    }
  }

  public void addExcludedRoot(@NotNull Module module, @Nullable VirtualFile virtualFile) {
    if (virtualFile != null && !myProjectFileIndex.isExcluded(virtualFile)) {
      getModuleRootInfo(module).addExcludedRoot(virtualFile);
    }
  }

  public void addTestRoot(@NotNull Module module, @Nullable VirtualFile virtualFile) {
    if (virtualFile != null && !myProjectFileIndex.isInTestSourceContent(virtualFile)) {
      getModuleRootInfo(module).addTestRoot(virtualFile);
    }
  }

  private void commitExcluded(@NotNull Module module ) {
    if (module.isDisposed()) {
      return;
    }
    var excludedRoots = getModuleRootInfo(module).getExcludedRoots();
    if( excludedRoots.isEmpty()){
      return;
    }

    // TODO consulo: IntelliJ MarkExcludeRootAction#modifyRoots is missing, same logic is in #modifyRoots
    modifyRoots(module, excludedRoots.toArray(VirtualFile.EMPTY_ARRAY), ExcludedContentFolderTypeProvider.getInstance());
    excludedRoots.clear();
  }

  private void commitTestRoots(@NotNull Module module) {
    if (module.isDisposed() ) {
      return;
    }

    var testRoots = getModuleRootInfo(module).getTestRoots();
    if( testRoots.isEmpty()){
      return;
    }

    // TODO consulo: IntelliJ MarkTestSourceRootAction#modifyRoots is missing, same logic is in #modifyRoots
    modifyRoots(module, testRoots.toArray(VirtualFile.EMPTY_ARRAY), TestContentFolderTypeProvider.getInstance());
    testRoots.clear();
  }

  /**
   * Marks {@code files} of the {@code module} with {@code folderType}, same as IntelliJ {@code MarkRootActionBase#modifyRoots}
   * TODO consulo: upstream also saves the project after the commit
   */
  private static void modifyRoots(@NotNull Module module, VirtualFile @NotNull [] files, @NotNull ContentFolderTypeProvider folderType) {
    ModifiableRootModel model = ModuleRootManager.getInstance(module).getModifiableModel();
    for (VirtualFile file : files) {
      ContentEntry entry = findContentEntry(model, file);
      if (entry != null) {
        for (ContentFolder folder : entry.getFolders(LanguageContentFolderScopes.all())) {
          if (file.equals(folder.getFile())) {
            entry.removeFolder(folder);
            break;
          }
        }
        entry.addFolder(file, folderType);
      }
    }
    ApplicationManager.getApplication().runWriteAction(model::commit);
  }

  private static @Nullable ContentEntry findContentEntry(@NotNull ModifiableRootModel model, @NotNull VirtualFile file) {
    for (ContentEntry contentEntry : model.getContentEntries()) {
      VirtualFile contentEntryFile = contentEntry.getFile();
      if (contentEntryFile != null && VirtualFileUtil.isAncestor(contentEntryFile, file, false)) {
        return contentEntry;
      }
    }
    return null;
  }

  @RequiredWriteAction
  void commit(){
    if( myProject.isDisposed()){
      return;
    }
    for (Module module : myModulesInfo.keySet()) {
      if( !module.isDisposed()){
        commitLibRoots(module);
        commitTestRoots(module);
        commitExcluded(module);
      }
    }
    commitExternalLibRoots(myProject);
  }

  private void commitExternalLibRoots(@NotNull Project project){
    if( project.isDisposed() || myExternalLibRoots.isEmpty()){
      return;
    }
    var projectRootManager = PerlProjectManager.getInstance(project);
    projectRootManager.addExternalLibraries(new ArrayList<>(myExternalLibRoots));
    myExternalLibRoots.clear();
  }

  private void commitLibRoots(@NotNull Module module) {
    if (module.isDisposed()) {
      return;
    }

    var libRoots = getModuleRootInfo(module).getLibRoots();
    new PerlMarkLibrarySourceRootAction().markRoot(module, new ArrayList<>(libRoots));
    libRoots.clear();
  }
}
