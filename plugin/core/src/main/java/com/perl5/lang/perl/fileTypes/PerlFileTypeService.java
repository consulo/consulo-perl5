/*
 * Copyright 2015-2024 Alexandr Evstigneev
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

package com.perl5.lang.perl.fileTypes;

import consulo.application.ApplicationManager;
import consulo.application.ReadAction;
import consulo.application.util.NotNullLazyValue;
import consulo.disposer.Disposable;
import consulo.logging.Logger;
import consulo.project.Project;
import consulo.project.ProjectManager;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileType;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class PerlFileTypeService implements Disposable {
  private static final Logger LOG = Logger.getInstance(PerlFileTypeService.class);

  private final NotNullLazyValue<LightDirectoryIndex<Function<VirtualFile, FileType>>> myDirectoryIndexProvider =
    NotNullLazyValue.createValue(() -> new LightDirectoryIndex<>(
      this,
      virtualFile -> null,
      directoryIndex -> ReadAction.run(() -> {
        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
          if (project.isDisposed()) {
            continue;
          }
          for (PerlFileTypeProvider fileTypeProvider : PerlFileTypeProvider.EP_NAME.getExtensionList()) {
            fileTypeProvider.addRoots(project, (root, function) -> {
              if (!root.isValid()) {
                LOG.warn("Attempt to create a descriptor for invalid file for " + root);
                return;
              }
              if (!root.isDirectory()) {
                LOG.warn("Attempt to create root for non-directory: " + root);
                return;
              }
              directoryIndex.putInfo(root, function);
            });
          }
        }
      })
    ));

  public PerlFileTypeService() {
    PerlFileTypeProvider.EP_NAME.addChangeListener(this::reset, this);
  }

  @Override
  public void dispose() {

  }

  void reset() {
    myDirectoryIndexProvider.get().resetIndex();
  }

  public static PerlFileTypeService getInstance() {
    return ApplicationManager.getApplication().getService(PerlFileTypeService.class);
  }

  public static @Nullable FileType getFileType(@Nullable VirtualFile virtualFile) {
    VirtualFile fileForAncestryCheck = virtualFile instanceof FakeVirtualFile ? virtualFile.getParent() : virtualFile;
    return getInstance().myDirectoryIndexProvider.get().getInfoForFile(fileForAncestryCheck).apply(virtualFile);
  }
}
