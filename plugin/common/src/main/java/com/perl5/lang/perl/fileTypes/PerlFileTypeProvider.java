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

package com.perl5.lang.perl.fileTypes;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.component.extension.ExtensionPointName;
import consulo.virtualFileSystem.fileType.FileType;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

@ExtensionAPI(ComponentScope.APPLICATION)
public interface PerlFileTypeProvider {
  ExtensionPointName<PerlFileTypeProvider> EP_NAME = ExtensionPointName.create(PerlFileTypeProvider.class);

  /**
   * Should pass root and type calculation function for each root with custom types
   * @param project project for settings
   * @param rootConsumer function should return a custom fileType or null
   */
  void addRoots(@NotNull Project project, BiConsumer<? super VirtualFile, Function<VirtualFile, @Nullable FileType>> rootConsumer);
}
