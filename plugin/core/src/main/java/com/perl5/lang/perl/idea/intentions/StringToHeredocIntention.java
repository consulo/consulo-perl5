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

import com.perl5.PerlBundle;
import consulo.application.WriteAction;
import consulo.codeEditor.Editor;
import consulo.language.editor.inspection.FileModifier;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.util.IncorrectOperationException;
import consulo.project.Project;
import consulo.ui.ex.awt.Messages;
import consulo.util.lang.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;


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
    String markerText = Messages.showInputDialog(
      project,
      PerlBundle.message("perl.intention.heredoc.dialog.prompt"),
      PerlBundle.message("perl.intention.heredoc.dialog.title"),
      Messages.getQuestionIcon(),
      ourMarker,
      null);
    if (StringUtil.isEmpty(markerText)) {
      Messages.showErrorDialog(
        project,
        PerlBundle.message("perl.intention.heredoc.error.message"),
        PerlBundle.message("perl.intention.heredoc.error.title")
      );
      return;
    }
    ourMarker = markerText;
    WriteAction.run(() -> doInvoke(project, editor, element));
  }

  @Override
  public @NotNull String getText() {
    return PerlBundle.message("perl.intention.heredoc.title");
  }

  @TestOnly
  public static void doWithMarker(@NotNull String marker, @NotNull Runnable runnable) {
    var currentMarker = ourMarker;
    //TestDialogManager.setTestInputDialog(message -> marker);
    //TestDialogManager.setTestDialog(TestDialog.YES);
    try {
      runnable.run();
    }
    finally {
      ourMarker = currentMarker;
    }
  }
}
