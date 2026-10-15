/*
 * Copyright 2015-2024 Alexandr Evstigneev
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

package com.perl5.lang.perl.idea.intentions;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.intention.IntentionMetaData;
import consulo.localize.LocalizeValue;
import consulo.language.editor.inspection.FileModifier;
import consulo.undoRedo.CommandProcessor;
import consulo.codeEditor.Editor;
import consulo.project.Project;
import consulo.ui.InputBoxBuilder;
import consulo.ui.MessageBoxes;
import consulo.util.lang.StringUtil;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.util.IncorrectOperationException;
import com.perl5.PerlBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;


// TODO consulo: upstream descriptionDirectoryName is intentionDescriptions/StringToHeredocConverter, Consulo looks up intentionDescriptions/StringToHeredocIntention
@ExtensionImpl
@IntentionMetaData(ignoreId = "perl5.StringToHeredocIntention", categories = {"Perl"}, fileExtensions = "pm")
public class StringToHeredocIntention extends StringToLastHeredocIntention {

  @Override
  public @Nullable FileModifier getFileModifierForPreview(@NotNull PsiFile target) {
    return new StringToHeredocIntention() {
      @Override
      public boolean startInWriteAction() {
        return true;
      }

      @Override
      public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        var oldMarker = ourMarker;
        try {
          ourMarker = "CUSTOM_MARKER";
          doInvoke(project, editor, element);
        }
        finally {
          ourMarker = oldMarker;
        }
      }
    };
  }

  @Override
  public boolean startInWriteAction() {
    return false;
  }

  @Override
  public @Nullable PsiElement getElementToMakeWritable(@NotNull PsiFile currentFile) {
    return currentFile;
  }

  @Override
  public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
    // consulo: Messages are Swing dialogs (not available in web), the unified input box is asynchronous; the conversion
    // happens after the intention command is finished, so it runs in its own write command
    InputBoxBuilder.text()
      .title(LocalizeValue.of(PerlBundle.message("perl.intention.heredoc.dialog.title")))
      .text(LocalizeValue.of(PerlBundle.message("perl.intention.heredoc.dialog.prompt")))
      .asQuestion()
      .value(ourMarker)
      .showAsync(project)
      .whenComplete((markerText, error) -> {
        if (StringUtil.isEmpty(markerText)) {
          MessageBoxes.okError(LocalizeValue.of(PerlBundle.message("perl.intention.heredoc.error.message")))
            .title(LocalizeValue.of(PerlBundle.message("perl.intention.heredoc.error.title")))
            .showAsync(project);
          return;
        }
        if (!element.isValid()) {
          return;
        }
        ourMarker = markerText;
        CommandProcessor.getInstance().newCommand()
          .project(project)
          .name(getText())
          .inWriteAction()
          .run(() -> doInvoke(project, editor, element));
      });
  }

  @Override
  public @NotNull LocalizeValue getText() {
    return LocalizeValue.localizeTODO(PerlBundle.message("perl.intention.heredoc.title"));
  }

  @TestOnly
  public static void doWithMarker(@NotNull String marker, @NotNull Runnable runnable) {
    var currentMarker = ourMarker;
    // TODO consulo: no TestDialogManager/TestDialog in Consulo; upstream stubbed the input dialog to return `marker` and confirm dialogs with YES
    try {
      runnable.run();
    }
    finally {
      ourMarker = currentMarker;
    }
  }
}
