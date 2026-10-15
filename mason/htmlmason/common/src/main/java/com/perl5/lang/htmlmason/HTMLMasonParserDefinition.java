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
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.parser.PsiParser;
import consulo.language.lexer.Lexer;
import consulo.language.version.LanguageVersion;
import consulo.util.lang.StringUtil;
import consulo.language.file.FileViewProvider;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.impl.psi.DummyHolder;
import consulo.language.impl.ast.TreeUtil;
import consulo.language.ast.IElementType;
import consulo.language.ast.IFileElementType;
import consulo.language.ast.TokenSet;
import com.perl5.lang.htmlmason.elementType.HTMLMasonTokenSets;
import com.perl5.lang.htmlmason.lexer.HTMLMasonLexer;
import com.perl5.lang.htmlmason.lexer.HTMLMasonLexerAdapter;
import com.perl5.lang.htmlmason.parser.psi.impl.HTMLMasonFileImpl;
import com.perl5.lang.perl.PerlParserDefinition;
import com.perl5.lang.perl.lexer.PerlTemplatingLexer;
import com.perl5.lang.htmlmason.parser.HTMLMasonParserImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.perl5.lang.htmlmason.elementType.HTMLMasonElementTypes.*;

@ExtensionImpl
public class HTMLMasonParserDefinition extends PerlParserDefinition {
  @Override
  public @NotNull Language getLanguage() {
    return HTMLMasonLanguage.INSTANCE;
  }

  @Override
  public @NotNull Lexer createLexer(LanguageVersion languageVersion) {
    // TODO consulo: no project/file in ParserDefinition.createLexer(LanguageVersion); upstream passes the project to the lexer, which
    //  reads custom tags from the project settings, here the settings are per module, so the parser ignores custom tags
    return new HTMLMasonLexerAdapter(null, null, false);
  }

  @Override
  public @NotNull IFileElementType getFileNodeType() {
    return FILE;
  }

  @Override
  public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
    return new HTMLMasonFileImpl(viewProvider);
  }

  @Override
  public @NotNull TokenSet getCommentTokens(LanguageVersion languageVersion) {
    return HTMLMasonTokenSets.COMMENTS;
  }

  @Override
  public @NotNull PsiParser createParser(LanguageVersion languageVersion) {
    return new HTMLMasonParserImpl();
  }

  @Override
  public int getLexerStateFor(@NotNull ASTNode contextNode, @NotNull IElementType elementType) {
    return PerlTemplatingLexer.packState(super.getLexerStateFor(contextNode, elementType),
                                         getTemplatingState(contextNode, elementType));
  }

  private int getTemplatingState(@NotNull ASTNode contextNode, @NotNull IElementType elementType) {
    if (elementType == HTML_MASON_TEMPLATE_BLOCK_HTML) {
      return HTMLMasonLexer.YYINITIAL;
    }
    PsiElement parent = contextNode.getPsi().getParent();
    ASTNode run = parent instanceof DummyHolder ? Objects.requireNonNull(parent.getContext()).getNode() : contextNode;

    while (run != null) {
      if (StringUtil.containsLineBreak(run.getChars())) {
        return HTMLMasonLexer.PERL;
      }
      else if (run.getElementType() == HTML_MASON_LINE_OPENER) {
        return HTMLMasonLexer.PERL_LINE;
      }
      run = TreeUtil.prevLeaf(run);
    }

    return HTMLMasonLexer.YYINITIAL;
  }
}
