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

package com.perl5.lang.perl.idea.editor.smartkeys;

import consulo.language.editor.action.EnterHandlerDelegateAdapter;
import consulo.dataContext.DataContext;
import consulo.logging.Logger;
import consulo.codeEditor.Editor;
import consulo.codeEditor.action.EditorActionHandler;
import consulo.util.lang.ref.Ref;
import consulo.util.lang.ref.SimpleReference;
import consulo.language.psi.PsiFile;
import com.perl5.lang.perl.util.PerlTimeLogger;
import org.jetbrains.annotations.NotNull;

public abstract class PerlEnterHandler extends EnterHandlerDelegateAdapter {
  private static final Logger LOG = Logger.getInstance(PerlEnterHandler.class);

  protected PerlEnterHandler() {
  }

  @Override
  public final Result preprocessEnter(@NotNull PsiFile file,
                                      @NotNull Editor editor,
                                      @NotNull SimpleReference<Integer> caretOffset,
                                      @NotNull SimpleReference<Integer> caretAdvance,
                                      @NotNull DataContext dataContext,
                                      EditorActionHandler originalHandler) {
    PerlTimeLogger logger = PerlTimeLogger.create(LOG);
    // consulo: Consulo passes SimpleReference (Ref is its deprecated subclass); subclasses keep upstream Ref signatures,
    // so values are copied in and back out
    Ref<Integer> caretOffsetRef = Ref.create(caretOffset.get());
    Ref<Integer> caretAdvanceRef = Ref.create(caretAdvance.get());
    Result result = doPreprocessEnter(file, editor, caretOffsetRef, caretAdvanceRef, dataContext, originalHandler);
    caretOffset.set(caretOffsetRef.get());
    caretAdvance.set(caretAdvanceRef.get());
    logger.debug("Pre processed enter with " + getClass().getSimpleName());
    return result;
  }


  public Result doPreprocessEnter(@NotNull PsiFile file,
                                  @NotNull Editor editor,
                                  @NotNull Ref<Integer> caretOffset,
                                  @NotNull Ref<Integer> caretAdvance,
                                  @NotNull DataContext dataContext,
                                  EditorActionHandler originalHandler) {
    return super.preprocessEnter(file, editor, caretOffset, caretAdvance, dataContext, originalHandler);
  }

  @Override
  public final Result postProcessEnter(@NotNull PsiFile file, @NotNull Editor editor, @NotNull DataContext dataContext) {
    PerlTimeLogger logger = PerlTimeLogger.create(LOG);
    Result result = doPostProcessEnter(file, editor, dataContext);
    logger.debug("Post processed enter with " + getClass().getSimpleName());
    return result;
  }

  protected Result doPostProcessEnter(@NotNull PsiFile file, @NotNull Editor editor, @NotNull DataContext dataContext) {
    return super.postProcessEnter(file, editor, dataContext);
  }
}
