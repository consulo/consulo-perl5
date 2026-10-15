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

package com.perl5.lang.perl.lexer;

import consulo.language.ast.TokenType;
import consulo.language.ast.TokenSet;
import org.jetbrains.annotations.NotNull;

import static com.perl5.lang.perl.lexer.PerlTokenSets.HEREDOC_BODIES_TOKENSET;
import static com.perl5.lang.perl.parser.PerlElementTypesGenerated.*;


public final class PerlTokenSetsEx {
  public static final PerlTokenSetsEx INSTANCE = new PerlTokenSetsEx();

  private static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);

  private static final TokenSet REAL_COMMENTS = TokenSet.create(
    COMMENT_LINE, COMMENT_BLOCK, COMMENT_ANNOTATION
  );

  private static final TokenSet COMMENTS = TokenSet.orSet(
    HEREDOC_BODIES_TOKENSET,
    REAL_COMMENTS,
    TokenSet.create(
      HEREDOC_END, HEREDOC_END_INDENTABLE
    )
  );

  private static final TokenSet WHITE_SPACE_AND_REAL_COMMENTS = TokenSet.orSet(WHITE_SPACES, REAL_COMMENTS);

  private static final TokenSet WHITE_SPACE_AND_COMMENTS = TokenSet.orSet(WHITE_SPACES, COMMENTS);

  private static final TokenSet MEANINGLESS_TOKENS = TokenSet.orSet(
    WHITE_SPACE_AND_COMMENTS,
    TokenSet.create(POD)
  );

  // fixme inline this
  private static final TokenSet LITERALS = PerlTokenSets.STRING_CONTENT_TOKENSET;

  private static final TokenSet IDENTIFIERS = TokenSet.orSet(
    PerlTokenSets.VARIABLE_NAMES,
    TokenSet.create(SUB_NAME, QUALIFYING_PACKAGE, PACKAGE, IDENTIFIER)
  );

  private PerlTokenSetsEx() {
  }

  public static @NotNull TokenSet getWHITE_SPACES() {
    return WHITE_SPACES;
  }

  public static @NotNull TokenSet getREAL_COMMENTS() {
    return REAL_COMMENTS;
  }

  public static @NotNull TokenSet getCOMMENTS() {
    return COMMENTS;
  }

  public static @NotNull TokenSet getWHITE_SPACE_AND_REAL_COMMENTS() {
    return WHITE_SPACE_AND_REAL_COMMENTS;
  }

  public static @NotNull TokenSet getWHITE_SPACE_AND_COMMENTS() {
    return WHITE_SPACE_AND_COMMENTS;
  }

  public static @NotNull TokenSet getMEANINGLESS_TOKENS() {
    return MEANINGLESS_TOKENS;
  }

  public static @NotNull TokenSet getLITERALS() {
    return LITERALS;
  }

  public static @NotNull TokenSet getIDENTIFIERS() {
    return IDENTIFIERS;
  }
}
