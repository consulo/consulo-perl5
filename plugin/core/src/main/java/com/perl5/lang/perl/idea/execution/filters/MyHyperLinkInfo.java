/*
 * Copyright 2015-2020 Alexandr Evstigneev
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

package com.perl5.lang.perl.idea.execution.filters;

import consulo.execution.ui.console.FileHyperlinkInfoBase;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

class MyHyperLinkInfo extends FileHyperlinkInfoBase {
  private final @NotNull String myFilePath;

  private final int myLine; // testing purposes

  public MyHyperLinkInfo(Project project, int documentLine, @NotNull String filePath) {
    super(project, documentLine, 0);
    myFilePath = filePath;
    myLine = documentLine;
  }

  @Override
  protected @Nullable VirtualFile getVirtualFile() {
    return VirtualFileUtil.findFileByIoFile(new File(myFilePath), false);
  }

  @Override
  public String toString() {
    return "line " + myLine + " in " + myFilePath;
  }
}
