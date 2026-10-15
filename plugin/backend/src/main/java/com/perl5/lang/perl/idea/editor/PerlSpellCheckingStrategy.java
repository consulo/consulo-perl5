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

package com.perl5.lang.perl.idea.editor;

import consulo.language.Language;
import com.perl5.lang.perl.PerlLanguage;
import consulo.language.psi.ElementManipulators;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiLanguageInjectionHost;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.language.psi.PsiUtilCore;
import consulo.language.spellcheker.SpellcheckingStrategy;
import consulo.language.spellcheker.tokenizer.TokenConsumer;
import consulo.language.spellcheker.tokenizer.Tokenizer;
import consulo.language.spellcheker.tokenizer.splitter.IdentifierTokenSplitter;
import com.perl5.lang.perl.psi.mixins.PerlStringBareMixin;
import com.perl5.lang.perl.psi.utils.PerlPsiUtil;
import com.perl5.lang.perl.util.PerlInjectionUtil;
import org.jetbrains.annotations.NotNull;

import static com.perl5.lang.perl.lexer.PerlTokenSets.STRING_CONTENT_TOKENSET;

public class PerlSpellCheckingStrategy extends SpellcheckingStrategy {
  @Override
  public @NotNull Language getLanguage() {
    return PerlLanguage.INSTANCE;
  }

  private static final Tokenizer<PsiElement> IDENTIFIER_TOKENIZER = new Tokenizer<>() {
    @Override
    public void tokenize(@NotNull PsiElement element, TokenConsumer consumer) {

      consumer.consumeToken(element,
                            ElementManipulators.getValueText(element),
                            true,
                            ElementManipulators.getOffsetInElement(element),
                            ElementManipulators.getValueTextRange(element),
                            IdentifierTokenSplitter.getInstance());
    }
  };

  @Override
  public @NotNull Tokenizer<?> getTokenizer(PsiElement element) {
    if (STRING_CONTENT_TOKENSET.contains(PsiUtilCore.getElementType(element))) {
      PsiElement lightNameIdentifierOwner = PerlPsiUtil.getLightNameIdentifierOwner(element);
      if (lightNameIdentifierOwner != null) {
        return IDENTIFIER_TOKENIZER;
      }

      if (element.getParent() instanceof PerlStringBareMixin) {
        return SpellcheckingStrategy.TEXT_TOKENIZER;
      }

      PsiLanguageInjectionHost injectionHost = PsiTreeUtil.getParentOfType(element, PsiLanguageInjectionHost.class);
      if (PerlInjectionUtil.hasInjections(injectionHost)) {
        return SpellcheckingStrategy.EMPTY_TOKENIZER;
      }
      return SpellcheckingStrategy.TEXT_TOKENIZER;
    }

    return super.getTokenizer(element);
  }
}
