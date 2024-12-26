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

package com.perl5.lang.perl.psi.impl;

import consulo.language.ast.IElementType;
import consulo.language.impl.psi.LeafPsiElement;
import consulo.language.psi.PsiComment;
import org.jetbrains.annotations.NotNull;

public class PerlPodElement extends LeafPsiElement implements PsiComment {
  public PerlPodElement(@NotNull IElementType type, CharSequence text) {
    super(type, text);
  }

  @Override
  public @NotNull IElementType getTokenType() {
    return getElementType();
  }
}
