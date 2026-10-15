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

package com.perl5.lang.perl.idea.copyright;

import com.perl5.lang.perl.fileTypes.PerlFileTypePackage;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.copyright.BaseUpdateCopyrightsProvider;
import consulo.language.copyright.UpdatePsiFileCopyright;
import consulo.language.copyright.config.CopyrightFileConfig;
import consulo.language.copyright.config.CopyrightProfile;
import consulo.language.psi.PsiFile;
import consulo.virtualFileSystem.fileType.FileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * upstream: {@code <copyright.updater filetype="Perl5 Package"/>}
 */
@ExtensionImpl
public class PerlUpdateCopyrightsProvider extends BaseUpdateCopyrightsProvider {
  @Override
  public @NotNull FileType getFileType() {
    return PerlFileTypePackage.INSTANCE;
  }

  @Override
  public @Nullable UpdatePsiFileCopyright<CopyrightFileConfig> createInstance(PsiFile file,
                                                                             CopyrightProfile options) {
    return new UpdatePerlPackageFileCopyright(file, options);
  }

  @Override
  public @NotNull CopyrightFileConfig createDefaultOptions() {
    final CopyrightFileConfig options = new CopyrightFileConfig();
    options.setBlock(false);
    options.setFileTypeOverride(CopyrightFileConfig.USE_TEXT);
    options.setPrefixLines(false);
    return options;
  }
}
