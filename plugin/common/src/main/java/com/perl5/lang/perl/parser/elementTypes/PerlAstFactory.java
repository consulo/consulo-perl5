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

package com.perl5.lang.perl.parser.elementTypes;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.impl.ast.ASTLeafFactory;
import consulo.language.impl.ast.LeafElement;
import consulo.language.impl.psi.PsiCommentImpl;
import consulo.language.ast.IElementType;
import consulo.language.version.LanguageVersion;
import com.perl5.lang.perl.parser.moose.psi.impl.PerlMooseKeywordElementImpl;
import com.perl5.lang.perl.parser.moose.psi.impl.PerlMooseKeywordSubNameElementImpl;
import com.perl5.lang.perl.psi.impl.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static com.perl5.lang.perl.parser.PerlElementTypesGenerated.*;
import static com.perl5.lang.perl.parser.moose.MooseElementTypes.*;

// consulo: ASTFactory is final in Consulo, custom leaves are created by ASTLeafFactory extensions (upstream: lang.ast.factory for Perl5)
@ExtensionImpl
public final class PerlAstFactory implements ASTLeafFactory {
  private final Map<IElementType, BiFunction<IElementType, CharSequence, LeafElement>> tokenFactories = new LinkedHashMap<>();

  {
    tokenFactories.put(ARRAY_NAME, PerlVariableNameElementImpl::new);
    tokenFactories.put(BUILTIN_ARGUMENTLESS, PerlSubNameElementImpl::new);
    tokenFactories.put(BUILTIN_LIST, PerlSubNameElementImpl::new);
    tokenFactories.put(BUILTIN_UNARY, PerlSubNameElementImpl::new);
    tokenFactories.put(COMMENT_BLOCK, PsiCommentImpl::new);
    tokenFactories.put(COMMENT_LINE, PsiCommentImpl::new);
    tokenFactories.put(CUSTOM_UNARY, PerlSubNameElementImpl::new);
    tokenFactories.put(GLOB_NAME, PerlVariableNameElementImpl::new);
    tokenFactories.put(HASH_NAME, PerlVariableNameElementImpl::new);
    tokenFactories.put(HEREDOC_END, PerlHeredocTerminatorElementImpl::new);
    tokenFactories.put(HEREDOC_END_INDENTABLE, PerlHeredocTerminatorElementImpl::new);
    tokenFactories.put(PACKAGE, PerlNamespaceElementImpl::new);
    tokenFactories.put(POD, PerlPodElement::new);
    tokenFactories.put(QUALIFYING_PACKAGE, PerlNamespaceElementImpl::new);
    tokenFactories.put(RESERVED_AFTER, PerlMooseKeywordElementImpl::new);
    tokenFactories.put(RESERVED_AROUND, PerlMooseKeywordElementImpl::new);
    tokenFactories.put(RESERVED_AUGMENT, PerlMooseKeywordElementImpl::new);
    tokenFactories.put(RESERVED_BEFORE, PerlMooseKeywordElementImpl::new);
    tokenFactories.put(RESERVED_EXTENDS, PerlMooseKeywordElementImpl::new);
    tokenFactories.put(RESERVED_HAS, PerlMooseKeywordElementImpl::new);
    tokenFactories.put(RESERVED_INNER, PerlMooseKeywordSubNameElementImpl::new);
    tokenFactories.put(RESERVED_META, PerlMooseKeywordElementImpl::new);
    tokenFactories.put(RESERVED_OVERRIDE, PerlMooseKeywordElementImpl::new);
    tokenFactories.put(RESERVED_SUPER, PerlMooseKeywordSubNameElementImpl::new);
    tokenFactories.put(RESERVED_WITH, PerlMooseKeywordElementImpl::new);
    tokenFactories.put(SCALAR_NAME, PerlVariableNameElementImpl::new);
    tokenFactories.put(STRING_CONTENT, PerlStringContentElementImpl::new);
    tokenFactories.put(STRING_CONTENT_QQ, PerlStringContentElementImpl::new);
    tokenFactories.put(STRING_CONTENT_XQ, PerlStringContentElementImpl::new);
    tokenFactories.put(SUB_NAME, PerlSubNameElementImpl::new);
    tokenFactories.put(TAG_PACKAGE, PerlNamespaceElementImpl::new);
    tokenFactories.put(VERSION_ELEMENT, PerlVersionElementImpl::new);
  }

  @Override
  public @Nullable LeafElement createLeaf(@NotNull IElementType type, @NotNull LanguageVersion languageVersion, @NotNull CharSequence text) {
    BiFunction<IElementType, CharSequence, LeafElement> factory = tokenFactories.get(type);
    return factory != null ? factory.apply(type, text) : null;
  }

  @Override
  public boolean test(@Nullable IElementType type) {
    return tokenFactories.containsKey(type);
  }
}
