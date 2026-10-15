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

package com.perl5.lang.perl.idea.run.prove;

import consulo.execution.action.Location;
import consulo.execution.action.PsiLocation;
import consulo.execution.test.sm.runner.SMTestLocator;
import consulo.project.Project;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiManager;
import consulo.language.psi.scope.GlobalSearchScope;
import com.perl5.lang.perl.idea.sdk.host.PerlHostData;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.List;

class PerlSMTestLocator implements SMTestLocator {
  private static final String FILE_PROTOCOL = "myfile";

  private final @NotNull PerlHostData<?, ?> myHostData;

  public PerlSMTestLocator(@NotNull PerlHostData<?, ?> hostData) {
    myHostData = hostData;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull List<Location> getLocation(@NotNull String protocol,
                                             @NotNull String path,
                                             @NotNull Project project,
                                             @NotNull GlobalSearchScope scope) {
    if (FILE_PROTOCOL.equals(protocol)) {
      String localPath = myHostData.getLocalPath(path);
      if (localPath != null) {
        VirtualFile virtualFile = VirtualFileUtil.findFileByIoFile(new File(localPath), true);
        if (virtualFile != null) {
          PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);
          if (psiFile != null) {
            return Collections.singletonList(new PsiLocation<>(psiFile));
          }
        }
      }
    }
    return Collections.emptyList();
  }
}
