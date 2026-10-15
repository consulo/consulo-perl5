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

package com.perl5.lang.pod.idea.editor;

import consulo.language.Language;
import consulo.language.editor.TargetElementUtil;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiReference;
import consulo.language.ast.IElementType;
import consulo.language.psi.PsiUtilCore;
import consulo.language.spellcheker.SpellcheckingStrategy;
import consulo.language.spellcheker.tokenizer.Tokenizer;
import com.perl5.lang.pod.PodLanguage;
import org.jetbrains.annotations.NotNull;

import static com.perl5.lang.pod.parser.PodElementTypesGenerated.POD_IDENTIFIER;

public class PodSpellCheckingStrategy extends SpellcheckingStrategy {
  @Override
  public @NotNull Language getLanguage() {
    return PodLanguage.INSTANCE;
  }

  @Override
  public @NotNull Tokenizer<?> getTokenizer(PsiElement element) {
    IElementType elementType = PsiUtilCore.getElementType(element);
    if (elementType == POD_IDENTIFIER) {
      PsiElement namedElement = TargetElementUtil.getNamedElement(element, 0);
      if (namedElement != null) {
        return EMPTY_TOKENIZER;
      }
      PsiFile podFile = element.getContainingFile().getViewProvider().getPsi(PodLanguage.INSTANCE);
      if (podFile == null) {
        return EMPTY_TOKENIZER;
      }
      PsiReference reference = podFile.findReferenceAt(element.getTextOffset());
      return reference != null ? EMPTY_TOKENIZER : TEXT_TOKENIZER;
    }

    return super.getTokenizer(element);
  }
}
