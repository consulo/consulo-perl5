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

package com.perl5.lang.perl;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.version.LanguageVersion;
import consulo.language.ast.ASTNode;
import consulo.language.parser.ParserDefinition;
import consulo.language.parser.PsiParser;
import consulo.language.lexer.Lexer;
import consulo.language.file.FileViewProvider;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.ast.IElementType;
import consulo.language.ast.IFileElementType;
import consulo.language.ast.TokenSet;
import com.perl5.lang.perl.lexer.PerlLexer;
import com.perl5.lang.perl.lexer.PerlLexingContext;
import com.perl5.lang.perl.lexer.PerlTokenSetsEx;
import com.perl5.lang.perl.lexer.adapters.PerlMergingLexerAdapter;
import com.perl5.lang.perl.parser.PerlParserImpl;
import com.perl5.lang.perl.parser.elementTypes.PerlPsiElementFactory;
import com.perl5.lang.perl.psi.PerlLexerAwareParserDefinition;
import com.perl5.lang.perl.psi.impl.PerlFileImpl;
import com.perl5.lang.perl.psi.stubs.PerlStubElementTypes;
import org.jetbrains.annotations.NotNull;

import static com.perl5.lang.perl.lexer.PerlLexer.*;
import static com.perl5.lang.perl.parser.PerlElementTypesGenerated.*;

@ExtensionImpl
public class PerlParserDefinition implements ParserDefinition, PerlLexerAwareParserDefinition {
  @Override
  public @NotNull Language getLanguage() {
    return PerlLanguage.INSTANCE;
  }


  @Override
  public @NotNull Lexer createLexer(LanguageVersion languageVersion) {
    // TODO consulo: no project in ParserDefinition.createLexer(LanguageVersion); upstream passes it to the lexing context
    return new PerlMergingLexerAdapter(PerlLexingContext.create(null));
  }

  @Override
  public @NotNull TokenSet getWhitespaceTokens(LanguageVersion languageVersion) {
    return PerlTokenSetsEx.getWHITE_SPACES();
  }

  @Override
  public @NotNull TokenSet getCommentTokens(LanguageVersion languageVersion) {
    return PerlTokenSetsEx.getCOMMENTS();
  }

  @Override
  public @NotNull TokenSet getStringLiteralElements(LanguageVersion languageVersion) {
    return PerlTokenSetsEx.getLITERALS();
  }

  @Override
  public @NotNull PsiParser createParser(LanguageVersion languageVersion) {
    return PerlParserImpl.INSTANCE;
  }

  @Override
  public @NotNull IFileElementType getFileNodeType() {
    return PerlStubElementTypes.FILE;
  }

  @Override
  public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
    return new PerlFileImpl(viewProvider);
  }

  @Override
  public @NotNull SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
    return SpaceRequirements.MAY;
  }

  @Override
  public @NotNull PsiElement createElement(ASTNode node) {
    return PerlPsiElementFactory.create(node.getElementType(), node);
  }

  @Override
  public int getLexerStateFor(@NotNull ASTNode contextNode, @NotNull IElementType elementType) {
    if (elementType == HASH_INDEX) {
      return AFTER_VARIABLE;
    }
    else if (elementType == COMMENT_ANNOTATION) {
      return ANNOTATION;
    }
    else if (elementType == HEREDOC_QQ) {
      return STRING_QQ;
    }
    else if (elementType == HEREDOC_QX) {
      return STRING_QX;
    }
    else if (elementType == HEREDOC) {
      return STRING_Q;
    }
    else if (elementType == PARSABLE_STRING_USE_VARS) {
      return USE_VARS_STRING;
    }
    return PerlLexer.YYINITIAL;
  }
}