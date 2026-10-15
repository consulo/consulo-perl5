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

package com.perl5.lang.htmlmason.filetypes;

import com.perl5.lang.htmlmason.idea.configuration.HTMLMasonSettings;
import com.perl5.lang.htmlmason.idea.configuration.HTMLMasonSourceRootType;
import com.perl5.lang.perl.fileTypes.PerlFileTypeProvider;
import consulo.module.Module;
import consulo.module.ModuleManager;
import consulo.perl5.mason.module.HTMLMasonModuleExtension;
import consulo.project.Project;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class HTMLMasonFileTypeProvider implements PerlFileTypeProvider {
  /**
   * consulo: settings are per module (HTML::Mason module extension), components roots of a module use the settings of the module. The
   * roots index is reset on roots changes, which include module extensions changes.
   */
  @Override
  public void addRoots(@NotNull Project project, BiConsumer<? super VirtualFile, Function<VirtualFile, @Nullable FileType>> rootConsumer) {
    for (Module module : ModuleManager.getInstance(project).getModules()) {
      HTMLMasonModuleExtension extension = module.isDisposed() ? null : module.getExtension(HTMLMasonModuleExtension.class);
      if (extension == null) {
        continue;
      }
      HTMLMasonSettings settings = extension.getSettings();
      for (VirtualFile root : HTMLMasonSourceRootType.INSTANCE.getRoots(module)) {
        rootConsumer.accept(root, virtualFile -> {
          String virtualFileName = virtualFile.getName();
          if (StringUtil.equals(settings.getAutoHandlerName(), virtualFileName) ||
              StringUtil.equals(settings.getDefaultHandlerName(), virtualFileName) ||
              settings.isVirtualFileNameMatches(virtualFile)
          ) {
            return HTMLMasonFileType.INSTANCE;
          }
          return null;
        });
      }
    }
  }
}
