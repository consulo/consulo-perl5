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

package com.perl5.lang.perl.psi.impl;

import com.perl5.lang.perl.extensions.parser.PerlReferencesProvider;
import com.perl5.lang.perl.psi.PerlString;
import com.perl5.lang.perl.psi.PerlStringContentElement;
import com.perl5.lang.perl.psi.PerlVisitor;
import com.perl5.lang.perl.psi.PsiPerlStatement;
import com.perl5.lang.perl.psi.references.PerlNamespaceReference;
import consulo.language.ast.IElementType;
import consulo.language.psi.ElementManipulators;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.psi.PsiReference;
import consulo.language.psi.ReferenceProvidersRegistry;
import consulo.language.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PerlStringContentElementImpl extends PerlLeafPsiElementWithReferences implements PerlStringContentElement {
  public PerlStringContentElementImpl(@NotNull IElementType type, CharSequence text) {
    super(type, text);
  }

  @Override
  @NotNull
  public PsiReference  [] computeReferences() {
    List<PsiReference> result = new ArrayList<>();
    String valueText = ElementManipulators.getValueText(this);
    if (PerlString.looksLikePackage(valueText)) {
      result.add(new PerlNamespaceReference(PerlStringContentElementImpl.this));
    }
    else {
      PerlReferencesProvider referencesProvider =
        PsiTreeUtil.getParentOfType(PerlStringContentElementImpl.this, PerlReferencesProvider.class, true, PsiPerlStatement.class);

      if (referencesProvider != null) {
        PsiReference[] references = referencesProvider.getReferences(PerlStringContentElementImpl.this);
        if (references != null) {
          result.addAll(Arrays.asList(references));
        }
      }
    }
    result.addAll(Arrays.asList(ReferenceProvidersRegistry.getReferencesFromProviders(PerlStringContentElementImpl.this)));
    return result.toArray(PsiReference.EMPTY_ARRAY);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof PerlVisitor perlVisitor) {
      perlVisitor.visitStringContentElement(this);
    }
    else {
      super.accept(visitor);
    }
  }
}


