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

package com.perl5.lang.perl.extensions.parser;

import com.perl5.lang.perl.parser.builder.PerlBuilder;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.component.extension.ExtensionPointName;
import consulo.language.ast.IElementType;
import consulo.language.ast.TokenSet;
import consulo.util.lang.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

@ExtensionAPI(ComponentScope.APPLICATION)
public abstract class PerlParserExtension {
  public static final ExtensionPointName<PerlParserExtension> EP_NAME = ExtensionPointName.create(PerlParserExtension.class);

  /**
   * Returns list of extendable tokensets. Loader will attempt to add them into builder
   * Should return list of pairs: token to extend - TokenSet of extended tokens
   * Reqired to avoid extra TERM expressions in PSI tree
   *
   * @return list of pairs to extend
   */
  public @NotNull List<Pair<IElementType, TokenSet>> getExtensionSets() {
    return Collections.emptyList();
  }

  /**
   * Parse method. Attempt to parse term
   * You may re-use PerlParser static methods to implement native perl expressions
   *
   * @param b PerlBuilder
   * @param l parsing level
   * @return parsing result
   */
  public boolean parseTerm(PerlBuilder b, int l) {
    return false;
  }

  /**
   * Callback to add highlighting tokens
   */
  public void addHighlighting() {
  }
}
