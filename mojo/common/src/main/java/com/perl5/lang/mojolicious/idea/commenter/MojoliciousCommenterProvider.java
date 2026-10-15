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

package com.perl5.lang.mojolicious.idea.commenter;

import consulo.language.Commenter;
import consulo.language.Language;
import consulo.codeEditor.Editor;
import consulo.language.file.FileViewProvider;
import consulo.language.psi.PsiFile;
import consulo.language.editor.MultipleLangCommentProvider;
import com.perl5.lang.mojolicious.psi.MojoliciousFileViewProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class MojoliciousCommenterProvider implements MultipleLangCommentProvider {
  @Override
  public @Nullable Commenter getLineCommenter(@NotNull PsiFile file,
                                              @NotNull Editor editor,
                                              @NotNull Language lineStartLanguage,
                                              @NotNull Language lineEndLanguage) {
    return Commenter.forLanguage(lineStartLanguage);
  }

  @Override
  public boolean canProcess(@NotNull PsiFile file, @NotNull FileViewProvider viewProvider) {
    return viewProvider instanceof MojoliciousFileViewProvider;
  }
}
