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

package com.perl5.lang.mason2.idea.highlighter;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import com.perl5.lang.mason2.Mason2Language;
import consulo.language.editor.highlight.SyntaxHighlighter;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import com.perl5.lang.mason2.filetypes.MasonPurePerlComponentFileType;
import com.perl5.lang.perl.idea.highlighter.PerlSyntaxHighlighter;
import com.perl5.lang.perl.idea.highlighter.PerlSyntaxHighlighterFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@ExtensionImpl
public class MasonSyntaxHighlighterFactory extends PerlSyntaxHighlighterFactory {
  @Override
  public @NotNull Language getLanguage() {
    return Mason2Language.INSTANCE;
  }

  @Override
  public @NotNull SyntaxHighlighter getSyntaxHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile) {
    if (virtualFile != null && virtualFile.getFileType() == MasonPurePerlComponentFileType.INSTANCE) {
      return new PerlSyntaxHighlighter(project);
    }
    return new MasonSyntaxHighlighter(project);
  }
}
