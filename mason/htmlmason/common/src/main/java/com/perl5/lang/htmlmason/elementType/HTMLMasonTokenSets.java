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

package com.perl5.lang.htmlmason.elementType;

import consulo.language.ast.TokenSet;
import com.perl5.lang.perl.lexer.PerlTokenSetsEx;

import static com.perl5.lang.htmlmason.elementType.HTMLMasonElementTypes.HTML_MASON_LINE_OPENER;
import static com.perl5.lang.htmlmason.elementType.HTMLMasonElementTypes.HTML_MASON_PERL_CLOSER;
import static com.perl5.lang.htmlmason.elementType.HTMLMasonElementTypes.HTML_MASON_PERL_OPENER;
import static com.perl5.lang.htmlmason.elementType.HTMLMasonElementTypes.HTML_MASON_TEMPLATE_BLOCK_HTML;


public final class HTMLMasonTokenSets {
  public static final HTMLMasonTokenSets INSTANCE = new HTMLMasonTokenSets();

  public static final TokenSet COMMENTS = TokenSet.orSet(
    PerlTokenSetsEx.getCOMMENTS(),
    TokenSet.create(
      HTML_MASON_LINE_OPENER,
      HTML_MASON_PERL_OPENER,
      HTML_MASON_PERL_CLOSER,
      HTML_MASON_TEMPLATE_BLOCK_HTML
    )
  );

  private HTMLMasonTokenSets() {
  }
}
