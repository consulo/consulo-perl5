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

package com.perl5.lang.perl.idea.editor.smartkeys;

import com.perl5.lang.perl.PerlLanguage;
import consulo.codeEditor.Editor;
import consulo.codeEditor.EditorEx;
import consulo.codeEditor.EditorHighlighter;
import consulo.codeEditor.HighlighterIterator;
import consulo.codeEditor.action.EditorActionHandler;
import consulo.dataContext.DataContext;
import consulo.document.Document;
import consulo.language.ast.IElementType;
import consulo.language.codeStyle.CodeStyleManager;
import consulo.language.editor.CodeInsightSettings;
import consulo.language.psi.PsiDocumentManager;
import consulo.language.psi.PsiFile;
import consulo.language.util.IncorrectOperationException;
import consulo.logging.Logger;
import consulo.util.lang.StringUtil;
import consulo.util.lang.ref.Ref;
import org.jetbrains.annotations.NotNull;

import static com.perl5.lang.perl.parser.PerlElementTypesGenerated.*;

public class PerlEnterInBracketsHandler extends PerlEnterHandler {
  private static final Logger LOG = Logger.getInstance(PerlEnterInBracketsHandler.class);

  @Override
  public Result doPreprocessEnter(@NotNull PsiFile file,
                                  @NotNull Editor editor,
                                  @NotNull Ref<Integer> caretOffset,
                                  @NotNull Ref<Integer> caretAdvance,
                                  @NotNull DataContext dataContext,
                                  EditorActionHandler originalHandler) {

    if (!file.getLanguage().is(PerlLanguage.INSTANCE) || !CodeInsightSettings.getInstance().SMART_INDENT_ON_ENTER) {
      return Result.Continue;
    }
    Integer currentOffset = caretOffset.get();
    if (currentOffset < 1) {
      return Result.Continue;
    }

    assert editor instanceof EditorEx;
    EditorHighlighter editorHighlighter = editor.getHighlighter();
    Document document = editor.getDocument();
    CharSequence documentChars = document.getCharsSequence();

    HighlighterIterator highlighterIterator = editorHighlighter.createIterator(currentOffset - 1);
    IElementType previousTokenType = PerlEditorUtil.getPreviousTokenType(highlighterIterator, false);
    if (highlighterIterator.atEnd() ||
        StringUtil.containsLineBreak(documentChars.subSequence(highlighterIterator.getStart(), currentOffset))) {
      return Result.Continue;
    }
    int openTokenStart = highlighterIterator.getStart();

    IElementType nextTokenType = PerlEditorUtil.getNextTokenType(highlighterIterator, false);
    if (highlighterIterator.atEnd() ||
        StringUtil.containsLineBreak(documentChars.subSequence(currentOffset, highlighterIterator.getEnd()))) {
      return Result.Continue;
    }

    if (previousTokenType == LEFT_BRACKET && nextTokenType == RIGHT_BRACKET) {
      doIndent(file, editor, dataContext, originalHandler, document);
    }
    else if (previousTokenType == QUOTE_SINGLE_OPEN && nextTokenType == QUOTE_SINGLE_CLOSE && openTokenStart > 0) {
      IElementType qwTokenType = PerlEditorUtil.getPreviousTokenType(editorHighlighter.createIterator(openTokenStart - 1), false);
      if (qwTokenType == RESERVED_QW) {
        doIndent(file, editor, dataContext, originalHandler, document);
      }
    }
    return Result.Continue;
  }

  private void doIndent(@NotNull PsiFile file,
                        @NotNull Editor editor,
                        @NotNull DataContext dataContext,
                        EditorActionHandler originalHandler, Document document) {
    originalHandler.execute(editor, editor.getCaretModel().getCurrentCaret(), dataContext);
    PsiDocumentManager.getInstance(file.getProject()).commitDocument(document);
    try {
      CodeStyleManager.getInstance(file.getProject()).adjustLineIndent(file, editor.getCaretModel().getOffset());
    }
    catch (IncorrectOperationException e) {
      LOG.error(e);
    }
  }
}
