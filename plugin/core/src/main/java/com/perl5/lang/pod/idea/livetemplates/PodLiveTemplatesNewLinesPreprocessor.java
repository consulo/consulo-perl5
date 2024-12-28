/*
 * Copyright 2015-2019 Alexandr Evstigneev
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

package com.perl5.lang.pod.idea.livetemplates;

import com.perl5.lang.pod.PodLanguage;
import consulo.application.WriteAction;
import consulo.codeEditor.Editor;
import consulo.document.Document;
import consulo.language.editor.template.TemplatePreprocessor;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiUtilCore;

/**
 * Handles merging of the newlines in template and editor
 */
public class PodLiveTemplatesNewLinesPreprocessor implements TemplatePreprocessor {
  @Override
  public void preprocessTemplate(Editor editor, PsiFile file, final int caretOffset, String textToInsert, String templateText) {
    if (!PsiUtilCore.getLanguageAtOffset(file, caretOffset).isKindOf(PodLanguage.INSTANCE)) {
      return;
    }

    Document document = editor.getDocument();
    CharSequence editorText = document.getCharsSequence();
    int editorNewlines = 0;
    for (; editorNewlines + caretOffset < editorText.length(); editorNewlines++) {
      if (editorText.charAt(caretOffset + editorNewlines) != '\n') {
        break;
      }
    }

    if (editorNewlines < 2) {
      return;
    }

    int endOffset = caretOffset + editorNewlines - 1;
    WriteAction.run(() -> document.deleteString(caretOffset, endOffset));
  }
}
