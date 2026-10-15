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

package com.perl5.lang.tt2.idea.editor;

import consulo.language.editor.action.TypedHandlerDelegate;
import consulo.codeEditor.Editor;
import consulo.codeEditor.util.EditorModificationUtil;
import consulo.virtualFileSystem.fileType.FileType;
import consulo.project.Project;
import consulo.util.lang.StringUtil;
import consulo.language.file.FileViewProvider;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.ast.IElementType;
import com.perl5.lang.tt2.TemplateToolkitFileViewProvider;
import com.perl5.lang.tt2.TemplateToolkitLanguage;
import com.perl5.lang.tt2.idea.settings.TemplateToolkitSettings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.perl5.lang.tt2.parser.TemplateToolkitElementTypesGenerated.TT2_HTML;


public class TemplateToolkitTypedHandler extends TypedHandlerDelegate {
  @Override
  public @NotNull Result beforeCharTyped(char c,
                                         @NotNull Project project,
                                         @NotNull Editor editor,
                                         @NotNull PsiFile file,
                                         @NotNull FileType fileType) {
    Result result = processChar(c, project, editor, file);
    return result == null ? super.beforeCharTyped(c, project, editor, file, fileType) : result;
  }

  protected @Nullable Result processChar(char c, Project project, Editor editor, PsiFile file) {
    FileViewProvider viewProvider = file.getViewProvider();
    if (!(viewProvider instanceof TemplateToolkitFileViewProvider)) {
      return null;
    }

    TemplateToolkitSettings settings = TemplateToolkitSettings.forPsiElement(file);
    String openTag = settings.getStartTag();
    if (StringUtil.isEmpty(openTag) || c != openTag.charAt(openTag.length() - 1)) {
      return null;
    }

    int offset = editor.getCaretModel().getOffset();
    if (offset == 0) {
      return null;
    }

    PsiElement element = viewProvider.findElementAt(offset - 1, TemplateToolkitLanguage.INSTANCE);
    if (element == null) {
      return null;
    }
    IElementType tokenType = element.getNode().getElementType();
    if (tokenType != TT2_HTML) {
      return null;
    }

    String openTagPrefix = openTag.substring(0, openTag.length() - 1);
    if (openTagPrefix.isEmpty()) {
      return null;
    }

    int startOffset = offset - openTagPrefix.length();
    if (startOffset < 0) {
      return null;
    }

    if (StringUtil.equals(file.getText().subSequence(startOffset, offset), openTagPrefix)) {
      EditorModificationUtil.insertStringAtCaret(editor, c + "  " + settings.getEndTag(), false, true, 2);
      return Result.STOP;
    }
    return null;
  }
}
