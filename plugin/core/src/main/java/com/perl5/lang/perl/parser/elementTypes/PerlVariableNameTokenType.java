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

package com.perl5.lang.perl.parser.elementTypes;

import com.perl5.lang.perl.lexer.PerlTokenSets;
import com.perl5.lang.perl.psi.impl.PerlVariableNameElementImpl;
import consulo.document.util.TextRange;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiUtilCore;
import org.jetbrains.annotations.NotNull;

public class PerlVariableNameTokenType extends PerlReparseableTokenType {
  public PerlVariableNameTokenType(@NotNull String debugName) {
    super(debugName, PerlVariableNameElementImpl.class);
  }

  @Override
  protected @NotNull TextRange getLexerConfirmationRange(@NotNull ASTNode leaf) {
    ASTNode parent = leaf.getTreeParent();
    return PerlTokenSets.VARIABLES.contains(PsiUtilCore.getElementType(parent)) ? parent.getTextRange() : TextRange.EMPTY_RANGE;
  }
}
