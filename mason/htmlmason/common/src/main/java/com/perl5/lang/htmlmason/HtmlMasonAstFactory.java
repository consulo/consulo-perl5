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

package com.perl5.lang.htmlmason;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.impl.ast.ASTFactory;
import consulo.language.impl.ast.ASTLeafFactory;
import consulo.language.impl.ast.LeafElement;
import consulo.language.impl.psi.template.OuterLanguageElementImpl;
import consulo.language.ast.IElementType;
import consulo.language.version.LanguageVersion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.perl5.lang.htmlmason.elementType.HTMLMasonElementTypes.HTML_MASON_OUTER_ELEMENT_TYPE;

// consulo: IntelliJ language ASTFactory -> Consulo ASTLeafFactory, selected by test(IElementType)
//  (upstream: lang.ast.factory for HTML::Mason Templating Language)
@ExtensionImpl
public class HtmlMasonAstFactory implements ASTLeafFactory {
  @Override
  public @Nullable LeafElement createLeaf(@NotNull IElementType type, @NotNull LanguageVersion languageVersion, @NotNull CharSequence text) {
    if (type == HTML_MASON_OUTER_ELEMENT_TYPE) {
      return new OuterLanguageElementImpl(type, text);
    }
    return ASTFactory.leaf(type, languageVersion, text);
  }

  @Override
  public boolean test(IElementType type) {
    return type == HTML_MASON_OUTER_ELEMENT_TYPE;
  }
}
