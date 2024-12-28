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

package com.perl5.lang.perl.idea.filetemplates;

import com.perl5.lang.perl.fileTypes.PerlFileType;
import consulo.fileTemplate.CreateFromTemplateHandler;
import consulo.fileTemplate.FileTemplate;
import consulo.language.file.FileTypeManager;
import consulo.language.psi.PsiDirectory;
import consulo.virtualFileSystem.fileType.FileType;
import org.jetbrains.annotations.NotNull;


public class PerlCreateFileFromTemplateHandler implements CreateFromTemplateHandler {
  public static final PerlCreateFileFromTemplateHandler INSTANCE = new PerlCreateFileFromTemplateHandler();

  @Override
  public boolean handlesTemplate(@NotNull FileTemplate template) {
    FileType templateFileType = FileTypeManager.getInstance().getFileTypeByExtension(template.getExtension());
    return templateFileType instanceof PerlFileType;
  }

  @Override
  public boolean canCreate(PsiDirectory @NotNull [] dirs) {
    return false;
  }
}
