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

package com.perl5.lang.perl.psi.references;

import com.perl5.lang.pod.PodLanguage;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.psi.PsiReferenceContributor;
import consulo.language.psi.PsiReferenceRegistrar;
import com.perl5.lang.pod.parser.psi.impl.PodIdentifierImpl;
import com.perl5.lang.pod.parser.psi.mixin.PodFormatterL;
import com.perl5.lang.pod.parser.psi.references.providers.PodFormatterLReferenceProvider;
import com.perl5.lang.pod.parser.psi.references.providers.PodIdentifierReferenceProvider;
import org.jetbrains.annotations.NotNull;

import static consulo.language.pattern.PlatformPatterns.psiElement;


@ExtensionImpl
public final class PodReferenceContributor extends PsiReferenceContributor {
  @Override
  public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
    registrar.registerReferenceProvider(psiElement(PodIdentifierImpl.class), new PodIdentifierReferenceProvider());
    registrar.registerReferenceProvider(psiElement(PodFormatterL.class), new PodFormatterLReferenceProvider());
  }

  @Override
  public @NotNull Language getLanguage() {
    return PodLanguage.INSTANCE;
  }
}
