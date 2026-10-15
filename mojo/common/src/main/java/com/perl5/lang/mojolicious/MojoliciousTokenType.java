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

package com.perl5.lang.mojolicious;

import consulo.language.ast.ASTNode;
import consulo.language.impl.psi.LeafPsiElement;
import consulo.language.impl.psi.PsiCommentImpl;
import consulo.language.impl.psi.template.OuterLanguageElementImpl;
import consulo.language.ast.IElementType;
import consulo.language.ast.ILeafElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import static com.perl5.lang.mojolicious.MojoliciousElementTypes.MOJO_OUTER_ELEMENT_TYPE;


public class MojoliciousTokenType extends IElementType implements ILeafElementType {
  public MojoliciousTokenType(@NotNull @NonNls String debugName) {
    super(debugName, MojoliciousLanguage.INSTANCE);
  }

  @Override
  public String toString() {
    return "Mojolicious: " + super.toString();
  }

  @Override
  public @NotNull ASTNode createLeafNode(@NotNull CharSequence leafText) {
    if (MojoTokenSets.COMMENTS.contains(this)) {
      return new PsiCommentImpl(this, leafText);
    }
    else if (this == MOJO_OUTER_ELEMENT_TYPE) {
      return new OuterLanguageElementImpl(this, leafText);
    }
    else {
      return new LeafPsiElement(this, leafText);
    }
  }
}
