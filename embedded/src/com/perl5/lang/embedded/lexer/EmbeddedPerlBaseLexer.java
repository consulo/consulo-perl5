/*
 * Copyright 2015-2017 Alexandr Evstigneev
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

package com.perl5.lang.embedded.lexer;

import com.intellij.openapi.util.text.StringUtil;
import com.perl5.lang.perl.lexer.PerlTemplatingLexer;
import org.jetbrains.annotations.Nullable;

public abstract class EmbeddedPerlBaseLexer extends PerlTemplatingLexer {
  private static final CommentEndCalculator COMMENT_END_CALCULATOR = commentText -> StringUtil.indexOf(commentText, "?>");

  @Nullable
  @Override
  protected CommentEndCalculator getCommentEndCalculator() {
    return COMMENT_END_CALCULATOR;
  }
}