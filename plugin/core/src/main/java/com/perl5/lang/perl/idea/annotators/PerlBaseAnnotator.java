/*
 * Copyright 2015-2020 Alexandr Evstigneev
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

package com.perl5.lang.perl.idea.annotators;

import com.perl5.lang.perl.lexer.PerlElementTypes;
import consulo.codeEditor.CodeInsightColors;
import consulo.colorScheme.EditorColorsManager;
import consulo.colorScheme.EditorColorsScheme;
import consulo.colorScheme.TextAttributes;
import consulo.colorScheme.TextAttributesKey;
import consulo.language.editor.annotation.AnnotationBuilder;
import consulo.language.editor.annotation.AnnotationHolder;
import consulo.language.editor.annotation.Annotator;
import consulo.language.editor.annotation.HighlightSeverity;
import consulo.language.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public abstract class PerlBaseAnnotator implements Annotator, PerlElementTypes {
  EditorColorsScheme currentScheme = EditorColorsManager.getInstance().getGlobalScheme();

  public TextAttributes adjustTextAttributes(TextAttributes textAttributes, boolean isDeprecated) {
    if (isDeprecated) {
      textAttributes = TextAttributes.merge(textAttributes, currentScheme.getAttributes(CodeInsightColors.DEPRECATED_ATTRIBUTES));
    }
    return textAttributes;
  }

  public static void createInfoAnnotation(@NotNull AnnotationHolder holder,
                                          @Nullable PsiElement element,
                                          @Nullable String message,
                                          @NotNull TextAttributesKey key) {
    if (element == null) {
      return;
    }
    AnnotationBuilder annotationBuilder = message == null ?
                                          holder.newSilentAnnotation(HighlightSeverity.INFORMATION) :
                                          holder.newAnnotation(HighlightSeverity.INFORMATION, message);
    annotationBuilder.range(element).textAttributes(key).create();
  }
}
