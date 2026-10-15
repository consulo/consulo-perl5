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

package com.perl5.lang.perl.idea.project;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import jakarta.inject.Singleton;
import jakarta.inject.Inject;
import consulo.disposer.Disposable;
import consulo.project.Project;
import consulo.module.content.layer.event.ModuleRootEvent;
import consulo.module.content.layer.event.ModuleRootListener;
import consulo.disposer.Disposer;
import consulo.virtualFileSystem.VirtualFile;
import consulo.ide.impl.idea.util.indexing.LightDirectoryIndex;
import consulo.component.messagebus.MessageBusConnection;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@ServiceAPI(ComponentScope.PROJECT)
@ServiceImpl
@Singleton
public class PerlDirectoryIndex implements Disposable {
  private final @NotNull LightDirectoryIndex<PerlDirectoryIndexEntry> myIndex;
  private final @NotNull Function<VirtualFile, VirtualFile> myRootComputator;

  @Inject

  public PerlDirectoryIndex(@NotNull Project project) {
    myIndex = createIndexForRoots(() -> PerlProjectManager.getInstance(project).getAllLibraryRoots());
    myRootComputator = createRootComputator(myIndex);
    MessageBusConnection connection = project.getMessageBus().connect(this);
    connection.subscribe(ModuleRootListener.class, new ModuleRootListener() {
      @Override
      public void rootsChanged(@NotNull ModuleRootEvent event) {
        myIndex.resetIndex();
      }
    });
  }

  /**
   * @return a computator for roots, for external usage. Can be used for roots with dynamic entries
   * @implSpec we could probably just create a simpler clone of {@link LightDirectoryIndex}
   */
  @ApiStatus.Experimental
  public @NotNull Function<VirtualFile, VirtualFile> createRootComputator(@NotNull List<VirtualFile> roots) {
    Disposable disposable = Disposable.newDisposable();
    Function<VirtualFile, VirtualFile> result = createRootComputator(createIndexForRoots(() -> roots));
    disposable.dispose();
    return result;
  }

  private @NotNull LightDirectoryIndex<PerlDirectoryIndexEntry> createIndexForRoots(Supplier<? extends List<VirtualFile>> libraryRootsSupplier) {
    return new LightDirectoryIndex<>(this, PerlDirectoryIndexEntry.EMPTY,
                                     it -> libraryRootsSupplier.get().forEach(root -> it.putInfo(root, new PerlDirectoryIndexEntry(root))));
  }

  @Override
  public void dispose() {
    myIndex.resetIndex();
  }

  private static Function<@Nullable VirtualFile, @Nullable VirtualFile> createRootComputator(@NotNull LightDirectoryIndex<? extends PerlDirectoryIndexEntry> index) {
    return virtualFile -> {
      if (virtualFile == null) {
        return null;
      }
      PerlDirectoryIndexEntry indexEntry = index.getInfoForFile(virtualFile);
      if (indexEntry == PerlDirectoryIndexEntry.EMPTY) {
        return null;
      }
      return indexEntry.getRoot();
    };
  }

  @Contract("null->null")
  public @Nullable VirtualFile getRoot(@Nullable VirtualFile virtualFile) {
    return myRootComputator.apply(virtualFile);
  }

  public static @NotNull PerlDirectoryIndex getInstance(@NotNull Project project) {
    return project.getService(PerlDirectoryIndex.class);
  }
}
