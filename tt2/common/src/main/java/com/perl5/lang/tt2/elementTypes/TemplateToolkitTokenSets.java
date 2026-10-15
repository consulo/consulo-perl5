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

package com.perl5.lang.tt2.elementTypes;

import consulo.language.ast.TokenType;
import consulo.language.ast.TokenSet;

import static com.perl5.lang.tt2.elementTypes.TemplateToolkitElementTypes.BLOCK_COMMENT;
import static com.perl5.lang.tt2.parser.TemplateToolkitElementTypesGenerated.*;


public final class TemplateToolkitTokenSets {
  public static final TemplateToolkitTokenSets INSTANCE = new TemplateToolkitTokenSets();

  public static final TokenSet WHITE_SPACES = TokenSet.create(
    TokenType.WHITE_SPACE
  );

  public static final TokenSet COMMENTS = TokenSet.create(
    TT2_HTML,
    LINE_COMMENT,
    BLOCK_COMMENT
  );

  public static final TokenSet WHITESPACES_AND_COMMENTS = TokenSet.orSet(
    WHITE_SPACES, COMMENTS
  );

  public static final TokenSet LITERALS = TokenSet.create(
    TT2_STRING_CONTENT
  );

  private TemplateToolkitTokenSets() {
  }
}
