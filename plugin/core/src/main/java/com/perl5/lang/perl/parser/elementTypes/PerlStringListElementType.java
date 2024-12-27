/*
 * Copyright 2015-2023 Alexandr Evstigneev
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

import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import consulo.language.ast.TokenSet;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiUtilCore;
import consulo.project.Project;
import consulo.util.lang.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.perl5.lang.perl.parser.PerlElementTypesGenerated.*;
import static com.perl5.lang.perl.psi.stubs.PerlStubElementTypes.USE_STATEMENT;

public class PerlStringListElementType extends PerlTwoQuotesQuoteLikeElementType {
  public PerlStringListElementType(@NotNull String debugName,
                                   @NotNull Class<? extends PsiElement> clazz) {
    super(debugName, clazz);
  }

  @Override
  protected boolean isOperatorToken(@Nullable IElementType tokenType) {
    return tokenType == RESERVED_QW;
  }

  @Override
  protected boolean isOperatorMandatory() {
    return true;
  }

  @Override
  protected boolean isOpenQuoteToken(@Nullable IElementType tokenType) {
    return tokenType == QUOTE_SINGLE_OPEN;
  }

  @Override
  protected boolean isCloseQuoteToken(@Nullable IElementType tokenType) {
    return tokenType == QUOTE_SINGLE_CLOSE;
  }

  @Override
  protected boolean isReparseableOld(@NotNull ASTNode parent,
                                     @NotNull CharSequence buffer,
                                     @NotNull Language fileLanguage,
                                     @NotNull Project project) {
    if (PsiUtilCore.getElementType(parent) == USE_STATEMENT) {
      @NotNull ASTNode[] children = parent.getChildren(TokenSet.create(PACKAGE));
      if (children.length == 1 && StringUtil.equals("vars", children[0].getChars())) {
        return false;
      }
    }
    return super.isReparseableOld(parent, buffer, fileLanguage, project);
  }
}
