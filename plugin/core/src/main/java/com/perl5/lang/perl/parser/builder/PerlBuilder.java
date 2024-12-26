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

package com.perl5.lang.perl.parser.builder;

import com.perl5.lang.perl.PerlParserDefinition;
import com.perl5.lang.perl.lexer.PerlElementTypes;
import com.perl5.lang.perl.parser.PerlParserImpl;
import consulo.language.ast.IElementType;
import consulo.language.impl.parser.GeneratedParserUtilBase;
import consulo.language.parser.PsiBuilder;
import consulo.language.parser.PsiParser;

/**
 * This wrapper created to be able to store per-parsing data like pragmas, warnings and variables ?
 */
public class PerlBuilder extends GeneratedParserUtilBase.Builder implements PerlElementTypes {
  private final PerlParserImpl perlParser;

  // flags that sq strings should be converted to the use_vars_lazy_parsable_strings
  boolean isUseVarsContent = false;
  // flag shows that we are in the interpolated string. Involves additional checkings like space between $var and {hash_key}
  boolean isInterpolated = false;
  // flag allowes special variable names
  boolean isSpecialVariableNamesAllowed = true;

  public PerlBuilder(PsiBuilder builder, GeneratedParserUtilBase.ErrorState state, PsiParser parser) {
    super(builder, state, parser);
    perlParser = (PerlParserImpl)parser;
  }

  /**
   * Return token ahead of current, skips spaces and comments
   *
   * @param steps positive or negative steps number to get token
   * @return token data: type and text
   */
  public IElementType lookupToken(int steps) {
    assert steps != 0;
    int rawStep = 0;
    int step = steps / Math.abs(steps);

    IElementType rawTokenType = null;

    while (steps != 0) {
      rawStep += step;
      rawTokenType = rawLookup(rawStep);

      // reached end
      if (rawTokenType == null) {
        return null;
      }

      if (!PerlParserDefinition.WHITE_SPACE_AND_COMMENTS.contains(rawTokenType)) {
        steps -= step;
      }
    }

    return rawTokenType;
  }

  public boolean isUseVarsContent() {
    return isUseVarsContent;
  }

  public void setUseVarsContent(boolean newState) {
    isUseVarsContent = newState;
  }

  public boolean isInterpolated() {
    return isInterpolated;
  }

  public boolean setSpecialVariableNamesAllowed(boolean specialVariableNamesAllowed) {
    boolean oldValue = isSpecialVariableNamesAllowed;
    isSpecialVariableNamesAllowed = specialVariableNamesAllowed;
    return oldValue;
  }

  public PerlParserImpl getPerlParser() {
    return perlParser;
  }
}
