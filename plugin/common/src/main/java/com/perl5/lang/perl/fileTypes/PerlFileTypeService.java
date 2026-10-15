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

package com.perl5.lang.perl.fileTypes;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.disposer.Disposable;
import jakarta.inject.Singleton;
import consulo.application.ApplicationManager;
import consulo.application.ReadAction;
import consulo.logging.Logger;
import consulo.virtualFileSystem.fileType.FileType;
import consulo.language.plain.PlainTextFileType;
import consulo.project.Project;
import consulo.project.ProjectManager;
import consulo.application.util.NotNullLazyValue;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.NullVirtualFile;
import consulo.virtualFileSystem.StubVirtualFile;
import consulo.ide.impl.idea.util.indexing.LightDirectoryIndex;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;


@ServiceAPI(ComponentScope.APPLICATION)
@ServiceImpl
@Singleton
public class PerlFileTypeService implements Disposable {
  private static final Logger LOG = Logger.getInstance(PerlFileTypeService.class);

  private final NotNullLazyValue<LightDirectoryIndex<Function<VirtualFile, FileType>>> myDirectoryIndexProvider =
    NotNullLazyValue.createValue(() -> new LightDirectoryIndex<>(
      this,
      virtualFile -> PlainTextFileType.INSTANCE,
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
    // TODO consulo: no extension point change listeners (dynamic plugins); upstream resets the index on PerlFileTypeProvider changes
    //noinspection deprecation
    //PerlFileTypeProvider.EP_NAME.addChangeListener(this::reset, this);
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
    // consulo: FakeVirtualFile (a file which is not created yet) is internal; it's the only platform StubVirtualFile with a parent
    VirtualFile fileForAncestryCheck =
      virtualFile instanceof StubVirtualFile && !(virtualFile instanceof NullVirtualFile) ? virtualFile.getParent() : virtualFile;
    var fileTypeFromIndex = getInstance().myDirectoryIndexProvider.get().getInfoForFile(fileForAncestryCheck).apply(virtualFile);
    return fileTypeFromIndex == PlainTextFileType.INSTANCE ? null : fileTypeFromIndex;
  }
}
