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

package com.perl5.lang.mojolicious;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.parser.PsiParser;
import consulo.language.lexer.Lexer;
import consulo.util.lang.StringUtil;
import consulo.language.file.FileViewProvider;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.impl.psi.DummyHolder;
import consulo.language.impl.ast.TreeUtil;
import consulo.language.ast.IElementType;
import consulo.language.ast.IFileElementType;
import consulo.language.ast.TokenSet;
import consulo.language.version.LanguageVersion;
import com.perl5.lang.mojolicious.lexer.MojoliciousLexer;
import com.perl5.lang.mojolicious.lexer.MojoliciousLexerAdapter;
import com.perl5.lang.mojolicious.psi.impl.MojoliciousFileImpl;
import com.perl5.lang.perl.PerlParserDefinition;
import com.perl5.lang.perl.lexer.PerlTemplatingLexer;
import com.perl5.lang.mojolicious.parser.MojoliciousParser;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.perl5.lang.mojolicious.MojoliciousElementTypes.FILE;


@ExtensionImpl
public class MojoliciousParserDefinition extends PerlParserDefinition {
  @Override
  public @NotNull Language getLanguage() {
    return MojoliciousLanguage.INSTANCE;
  }

  @Override
  public @NotNull TokenSet getCommentTokens(LanguageVersion languageVersion) {
    return MojoTokenSets.COMMENTS;
  }

  @Override
  public @NotNull Lexer createLexer(LanguageVersion languageVersion) {
    // TODO consulo: no project in ParserDefinition.createLexer(LanguageVersion); upstream passes it to the lexer
    return new MojoliciousLexerAdapter(null, false);
  }

  @Override
  public @NotNull IFileElementType getFileNodeType() {
    return FILE;
  }

  @Override
  public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
    return new MojoliciousFileImpl(viewProvider);
  }

  @Override
  public @NotNull PsiParser createParser(LanguageVersion languageVersion) {
    return new MojoliciousParser();
  }

  @Override
  public int getLexerStateFor(@NotNull ASTNode contextNode,
                              @NotNull IElementType elementType) {
    return PerlTemplatingLexer.packState(super.getLexerStateFor(contextNode, elementType),
                                         getTemplatingState(contextNode, elementType));
  }

  private int getTemplatingState(@NotNull ASTNode contextNode, @NotNull IElementType elementType) {
    if (elementType == MojoliciousElementTypes.MOJO_TEMPLATE_BLOCK_HTML) {
      return MojoliciousLexer.YYINITIAL;
    }
    PsiElement parent = contextNode.getPsi().getParent();
    ASTNode run = parent instanceof DummyHolder ? Objects.requireNonNull(parent.getContext()).getNode() : contextNode;

    while (run != null) {
      if (StringUtil.containsLineBreak(run.getChars())) {
        return MojoliciousLexer.PERL_BLOCK;
      }
      else if (MojoTokenSets.LINE_OPENERS.contains(run.getElementType())) {
        return MojoliciousLexer.PERL_LINE;
      }
      run = TreeUtil.prevLeaf(run);
    }

    return MojoliciousLexer.YYINITIAL;
  }
}
