/*
 * Copyright 2015-2022 Alexandr Evstigneev
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

package com.perl5.lang.perl.intellilang;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.inject.MultiHostInjector;
import consulo.language.inject.MultiHostRegistrar;
import consulo.document.util.TextRange;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiLanguageInjectionHost;
import consulo.language.impl.psi.PsiCommentImpl;
import consulo.language.psi.PsiUtilCore;
import com.perl5.lang.perl.idea.intellilang.PerlInjectionMarkersService;
import com.perl5.lang.perl.psi.PerlAnnotationInject;
import com.perl5.lang.perl.psi.utils.PerlAnnotations;
import org.jetbrains.annotations.NotNull;

import static com.perl5.lang.perl.parser.PerlElementTypesGenerated.COMMENT_BLOCK;
import static com.perl5.lang.perl.parser.PerlElementTypesGenerated.TAG_DATA;

@ExtensionImpl
public final class PerlDataLanguageInjector implements MultiHostInjector {
  @Override
  public void injectLanguages(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
    if (PsiUtilCore.getElementType(context) != COMMENT_BLOCK) {
      return;
    }
    var prevSibling = context.getPrevSibling();
    if (PsiUtilCore.getElementType(prevSibling) != TAG_DATA) {
      return;
    }
    PerlAnnotationInject injectAnnotation = PerlAnnotations.getAnyAnnotationByClass(prevSibling, PerlAnnotationInject.class);
    if (injectAnnotation == null) {
      return;
    }

    var languageMarker = injectAnnotation.getLanguageMarker();
    var injectedLanguage = PerlInjectionMarkersService.getInstance(prevSibling.getProject()).getLanguageByMarker(languageMarker);
    if (injectedLanguage == null) {
      return;
    }

    registrar.startInjecting(injectedLanguage);
    registrar.addPlace("", "", (PsiLanguageInjectionHost)context, TextRange.create(0, context.getTextLength()));
    registrar.doneInjecting();
  }

  // consulo: single element class instead of upstream elementsToInjectIn() list
  @Override
  public @NotNull Class<? extends PsiElement> getElementClass() {
    return PsiCommentImpl.class;
  }
}
