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

package com.perl5.lang.tt2.filetypes;

import consulo.language.editor.highlight.LayerDescriptor;
import consulo.language.editor.highlight.LayeredLexerEditorHighlighter;
import consulo.codeEditor.EditorHighlighter;
import consulo.language.editor.highlight.SyntaxHighlighter;
import consulo.language.editor.highlight.SyntaxHighlighterFactory;
import consulo.project.Project;
import consulo.virtualFileSystem.fileType.FileType;
import consulo.virtualFileSystem.VirtualFile;
import com.perl5.lang.perl.PerlLanguage;
import com.perl5.lang.perl.idea.highlighter.PerlEditorHighlighterProviderBase;
import com.perl5.lang.tt2.TemplateToolkitFileViewProvider;
import com.perl5.lang.tt2.elementTypes.TemplateToolkitElementTypes;
import com.perl5.lang.tt2.idea.highlighting.TemplateToolkitSyntaxHighlighter;
import com.perl5.lang.tt2.idea.settings.TemplateToolkitSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.perl5.lang.tt2.parser.TemplateToolkitElementTypesGenerated.TT2_HTML;

public class TemplateToolkitEditorHighlighterProvider extends PerlEditorHighlighterProviderBase {
  // consulo: Consulo keys EditorHighlighterProvider by file type (upstream xml filetype="Template Toolkit")
  @Override
  public @NotNull FileType getFileType() {
    return TemplateToolkitFileType.INSTANCE;
  }

  @Override
  protected @NotNull EditorHighlighter registerAdditionalLayers(@NotNull LayeredLexerEditorHighlighter highlighter,
                                                                @Nullable Project project,
                                                                @Nullable VirtualFile virtualFile) {
    highlighter.registerLayer(TT2_HTML, new LayerDescriptor(
      SyntaxHighlighterFactory.getSyntaxHighlighter(
        TemplateToolkitFileViewProvider.calcTemplateLanguage(project, virtualFile),
        project,
        virtualFile
      ),
      ""
    ));
    var perlLayerDescriptor = new LayerDescriptor(
      SyntaxHighlighterFactory.getSyntaxHighlighter(PerlLanguage.INSTANCE, project, virtualFile), "");
    highlighter.registerLayer(TemplateToolkitElementTypes.TT2_PERL_CODE, perlLayerDescriptor);
    highlighter.registerLayer(TemplateToolkitElementTypes.TT2_RAWPERL_CODE, perlLayerDescriptor);
    return highlighter;
  }

  @Override
  protected @NotNull SyntaxHighlighter createBaseSyntaxHighlighter(@Nullable Project project, @Nullable VirtualFile virtualFile) {
    return new TemplateToolkitSyntaxHighlighter(TemplateToolkitSettings.forFile(project, virtualFile));
  }
}
