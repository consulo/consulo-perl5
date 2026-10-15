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

package com.perl5.lang.perl.intellilang;

import consulo.language.editor.completion.CompletionUtilCore;
import consulo.language.Language;
import consulo.language.inject.MultiHostRegistrar;
import consulo.application.util.RecursionManager;
import consulo.document.util.TextRange;
import consulo.application.util.registry.Registry;
import consulo.language.psi.ElementManipulators;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiLanguageInjectionHost;
import consulo.language.psi.PsiReference;
import consulo.language.psi.scope.LocalSearchScope;
import consulo.language.psi.search.ReferencesSearch;
import consulo.application.util.CachedValueProvider;
import consulo.language.psi.util.LanguageCachedValueUtil;
import consulo.language.psi.PsiUtilCore;
import consulo.application.util.query.Query;
import com.perl5.lang.perl.PerlLanguage;
import com.perl5.lang.perl.idea.intellilang.PerlInjectionMarkersService;
import com.perl5.lang.perl.psi.*;
import com.perl5.lang.perl.psi.mixins.PerlStringMixin;
import com.perl5.lang.perl.psi.utils.PerlAnnotations;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static com.perl5.lang.perl.parser.PerlElementTypesGenerated.OPERATOR_ASSIGN;


public class PerlStringLanguageInjector extends PerlLiteralLanguageInjector {
  @Override
  public void injectLanguages(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement host) {
    if (!(host instanceof PerlStringMixin stringMixin) || !stringMixin.isValidHost() || PerlAnnotations.isInjectionSuppressed(host)) {
      return;
    }

    // before element
    PerlAnnotationInject injectAnnotation = PerlAnnotations.getAnyAnnotationByClass(stringMixin, PerlAnnotationInject.class);
    if (injectAnnotation != null) {
      injectByAnnotation(stringMixin, registrar, injectAnnotation);
    }

    // program context
    if (Registry.is("perl5.eval.auto.injection", true)) {
      PsiElement context = getPerlInjectionContext(stringMixin);
      if (context != null) {
        injectLanguage(stringMixin, registrar, PerlLanguage.INSTANCE);
      }
    }
  }

  @Contract("null->null;!null->!null")
  public static @Nullable PsiElement getInjectionContextOrSelf(@Nullable PsiElement context) {
    if (context instanceof PsiLanguageInjectionHost injectionHost) {
      return Objects.requireNonNullElse(getPerlInjectionContext(injectionHost), context);
    }
    return context;
  }

  // consulo: single element class instead of upstream elementsToInjectIn() list
  @Override
  public @NotNull Class<? extends PsiElement> getElementClass() {
    return PerlStringMixin.class;
  }

  public static @Nullable PsiElement getPerlInjectionContext(@NotNull PsiLanguageInjectionHost host) {
    return LanguageCachedValueUtil.getCachedValue(
      host, () -> {
        var result = RecursionManager.doPreventingRecursion(host, false, () -> computeInjectionContext(host));
        return CachedValueProvider.Result.create(result, host.getContainingFile());
      });
  }

  private static @Nullable PsiElement computeInjectionContext(@NotNull PsiLanguageInjectionHost host) {
    if (host instanceof PsiPerlStringXq) {
      return null;
    }
    PsiElement parent = host.getParent();
    if (parent instanceof PsiPerlEvalExpr) {
      return parent;
    }

    if (!(parent instanceof PsiPerlAssignExpr assignExpr)) {
      return null;
    }

    if (PsiUtilCore.getElementType(assignExpr.getRightOperatorElement(host)) != OPERATOR_ASSIGN) {
      return null;
    }

    PsiElement variable = assignExpr.getLeftPartOfAssignment(host);
    if (variable instanceof PerlVariableDeclarationExpr variableDeclarationExpr) {
      List<PsiPerlVariableDeclarationElement> variables = variableDeclarationExpr.getVariableDeclarationElementList();
      if (variables.size() != 1) {
        return null;
      }
      variable = variables.getFirst();
    }
    else if (variable instanceof PsiPerlScalarVariable scalarVariable) {
      PerlVariableDeclarationElement variableDeclarationElement = scalarVariable.getLexicalDeclaration();
      if (variableDeclarationElement == null) {
        return null;
      }
      variable = variableDeclarationElement;
    }
    else {
      return null;
    }
    PsiElement realVariable = CompletionUtilCore.getOriginalOrSelf(variable);
    Query<PsiReference> references =
      ReferencesSearch.search(realVariable, new LocalSearchScope(host.getContainingFile().getOriginalFile()));
    for (PsiReference reference : references) {
      PsiElement referenceElement = reference.getElement();
      PsiElement variableUsage = referenceElement.getParent();
      PsiElement usageContext = variableUsage.getParent();
      if (usageContext instanceof PsiPerlEvalExpr) {
        return usageContext;
      }
    }
    return null;
  }

  protected void injectByAnnotation(@NotNull PerlStringMixin host,
                                    @NotNull MultiHostRegistrar registrar,
                                    PerlAnnotationInject injectAnnotation) {
    String languageMarker = injectAnnotation.getLanguageMarker();
    if (languageMarker == null) {
      return;
    }
    injectLanguage(host, registrar, PerlInjectionMarkersService.getInstance(host.getProject()).getLanguageByMarker(languageMarker));
  }

  private void injectLanguage(@NotNull PerlStringMixin perlString,
                              @NotNull MultiHostRegistrar registrar,
                              @Nullable Language targetLanguage) {
    if (targetLanguage == null) {
      return;
    }
    TextRange contentRange = ElementManipulators.getValueTextRange(perlString);
    if (contentRange.isEmpty()) {
      return;
    }
    PsiElement openQuoteElement = perlString.getOpenQuoteElement();
    if (openQuoteElement == null) {
      return;
    }
    PsiElement closeQuoteElement = perlString.getCloseQuoteElement();
    PsiElement firstContentElement = openQuoteElement.getNextSibling();
    if (firstContentElement == closeQuoteElement) {
      return;
    }
    injectLanguageIntoPsiRange(firstContentElement, closeQuoteElement, registrar, targetLanguage);
  }
}
