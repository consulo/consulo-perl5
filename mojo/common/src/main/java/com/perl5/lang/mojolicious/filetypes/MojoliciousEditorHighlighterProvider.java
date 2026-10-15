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

package com.perl5.lang.mojolicious.filetypes;

import consulo.html.language.HtmlFileType;
import consulo.language.editor.highlight.LayerDescriptor;
import consulo.language.editor.highlight.LayeredLexerEditorHighlighter;
import consulo.codeEditor.EditorHighlighter;
import consulo.language.editor.highlight.SyntaxHighlighter;
import consulo.language.editor.highlight.SyntaxHighlighterFactory;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileType;
import com.perl5.lang.mojolicious.idea.highlighter.MojoliciousSyntaxHighlighter;
import com.perl5.lang.perl.idea.highlighter.PerlEditorHighlighterProviderBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static com.perl5.lang.mojolicious.MojoliciousElementTypes.MOJO_TEMPLATE_BLOCK_HTML;

public class MojoliciousEditorHighlighterProvider extends PerlEditorHighlighterProviderBase {
  // consulo: Consulo keys EditorHighlighterProvider by file type
  @Override
  public @NotNull FileType getFileType() {
    return MojoliciousFileType.INSTANCE;
  }

  @Override
  protected @NotNull EditorHighlighter registerAdditionalLayers(@NotNull LayeredLexerEditorHighlighter highlighter,
                                                                @Nullable Project project,
                                                                @Nullable VirtualFile virtualFile) {
    highlighter.registerLayer(MOJO_TEMPLATE_BLOCK_HTML, new LayerDescriptor(
      Objects.requireNonNull(SyntaxHighlighterFactory.getSyntaxHighlighter(HtmlFileType.INSTANCE, project, virtualFile)), ""));
    return highlighter;
  }

  @Override
  protected @NotNull SyntaxHighlighter createBaseSyntaxHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile) {
    return new MojoliciousSyntaxHighlighter(project);
  }
}
