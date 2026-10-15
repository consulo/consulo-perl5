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

package com.perl5.lang.mason2.psi.references;

import com.perl5.lang.mason2.Mason2Language;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.psi.PsiReferenceContributor;
import consulo.language.psi.PsiReferenceRegistrar;
import com.perl5.lang.mason2.psi.MasonMethodModifierName;
import com.perl5.lang.mason2.psi.references.providers.MasonMethodModifierNameReferenceProvider;
import org.jetbrains.annotations.NotNull;

import static consulo.language.pattern.PlatformPatterns.psiElement;

// consulo: upstream psi.referenceContributor language="Mason2"
@ExtensionImpl
public final class MasonReferenceContributor extends PsiReferenceContributor {
  @Override
  public @NotNull Language getLanguage() {
    return Mason2Language.INSTANCE;
  }

  @Override
  public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
    registrar.registerReferenceProvider(psiElement(MasonMethodModifierName.class), new MasonMethodModifierNameReferenceProvider());
  }
}
