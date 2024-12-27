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

package com.perl5.lang.perl.idea.highlighter;

import consulo.colorScheme.TextAttributesKey;
import consulo.language.ast.IElementType;
import consulo.language.ast.TokenSet;
import consulo.project.Project;
import org.jetbrains.annotations.NotNull;


public abstract class PerlSyntaxHighlighterEmbedded extends PerlSyntaxHighlighter {
  public PerlSyntaxHighlighterEmbedded(Project project) {
    super(project);
  }

  @Override
  public TextAttributesKey @NotNull [] getTokenHighlights(IElementType tokenType) {
    if (getMarkersTokenSet().contains(tokenType)) {
      return PerlSyntaxHighlighter.EMBED_MARKER_KEYS;
    }
    return super.getTokenHighlights(tokenType);
  }

  public abstract TokenSet getMarkersTokenSet();
}
