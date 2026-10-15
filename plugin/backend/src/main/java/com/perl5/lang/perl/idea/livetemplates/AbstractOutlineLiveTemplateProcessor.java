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

package com.perl5.lang.perl.idea.livetemplates;

import consulo.language.editor.template.Template;
import consulo.language.editor.template.context.TemplateContext;
import consulo.language.editor.template.TemplateOptionalProcessor;
import consulo.language.Language;
import consulo.document.Document;
import consulo.codeEditor.Editor;
import consulo.document.RangeMarker;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.util.dataholder.KeyWithDefaultValue;
import consulo.util.lang.StringUtil;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.editor.util.PsiUtilBase;
import com.perl5.PerlBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public abstract class AbstractOutlineLiveTemplateProcessor implements TemplateOptionalProcessor {
  // consulo: Consulo template options are keyed; this processor overrides isEnabled() and is never visible
  private static final KeyWithDefaultValue<Boolean> KEY = KeyWithDefaultValue.create("perl5.outline.live.template.processor", true);

  @Override
  public KeyWithDefaultValue<Boolean> getKey() {
    return KEY;
  }

  /**
   * Checks if psi file should be handled by processor
   */
  protected abstract boolean isMyFile(PsiFile file);

  /**
   * Checks if marker is already there or this is empty line and no outline is necessary
   */
  protected abstract boolean shouldAddMarkerAtLineStartingAtOffset(CharSequence buffer, int offset);

  /**
   * Returns base language of the file, could take from psiFile though
   */
  protected abstract @NotNull Language getMyLanguage();

  /**
   * Returns outline marker text to insert
   */
  @SuppressWarnings("SameReturnValue")
  protected @NotNull String getOutlineMarker() {
    return "% ";
  }

  /**
   * Attempts to find an outline marker by the first psi element in the line.
   *
   * @param firstElement first element of the line
   * @return outline marker or null if not found
   */
  protected abstract @Nullable PsiElement getOutlineElement(PsiElement firstElement);

  @Override
  public LocalizeValue getOptionText() {
    return LocalizeValue.localizeTODO(PerlBundle.message("please.report.a.bug"));
  }

  @Override
  public boolean isEnabled(Template template) {
    return true;
  }

  @Override
  public boolean isVisible(@NotNull Template template) {
    return false;
  }

  @Override
  public void processText(Project project, Template template, Document document, RangeMarker templateRange, Editor editor) {
    // TODO consulo: Template has no public isInline(), inline templates (in-place refactorings etc.) are built as ad-hoc templates without
    //  a key, so all ad-hoc templates are skipped here, not only the inline ones
    if (StringUtil.isEmpty(template.getKey())) {
      return;
    }

    PsiFile file = PsiUtilBase.getPsiFileInEditor(editor, project);
    if (!isMyFile(file)) {
      return;
    }

    int startOffset = templateRange.getStartOffset();
    int startLine = document.getLineNumber(startOffset);
    int endLine = document.getLineNumber(templateRange.getEndOffset());
    if (startLine == endLine) {
      return;
    }

    int startLineBeginOffset = document.getLineStartOffset(startLine);

    assert file != null;
    PsiElement firstElement = file.getViewProvider().findElementAt(startLineBeginOffset, getMyLanguage());
    firstElement = getOutlineElement(firstElement);
    if (firstElement == null) {
      return;
    }

    CharSequence charsSequence = document.getCharsSequence();
    for (int currentLine = endLine; currentLine > startLine; currentLine--) {
      int lineStartOffset = document.getLineStartOffset(currentLine);
      if (shouldAddMarkerAtLineStartingAtOffset(charsSequence, lineStartOffset)) {
        document.insertString(lineStartOffset, getOutlineMarker());
      }
    }
  }
}
