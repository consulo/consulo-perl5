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

package com.perl5.lang.tt2;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.ast.ASTNode;
import consulo.language.parser.ParserDefinition;
import consulo.language.parser.PsiParser;
import consulo.language.lexer.Lexer;
import consulo.language.version.LanguageVersion;
import consulo.language.file.FileViewProvider;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.ast.IFileElementType;
import consulo.language.ast.TokenSet;
import com.perl5.lang.perl.parser.elementTypes.PerlPsiElementFactory;
import com.perl5.lang.tt2.elementTypes.TemplateToolkitTokenSets;
import com.perl5.lang.tt2.idea.settings.TemplateToolkitSettings;
import com.perl5.lang.tt2.lexer.TemplateToolkitLexerAdapter;
import com.perl5.lang.tt2.parser.TemplateToolkitParser;
import com.perl5.lang.tt2.psi.impl.TemplateToolkitFileImpl;
import org.jetbrains.annotations.NotNull;

import static com.perl5.lang.tt2.elementTypes.TemplateToolkitElementTypes.TT2_FILE;


@ExtensionImpl
public class TemplateToolkitParserDefinition implements ParserDefinition {
  @Override
  public @NotNull Language getLanguage() {
    return TemplateToolkitLanguage.INSTANCE;
  }

  @Override
  public @NotNull Lexer createLexer(LanguageVersion languageVersion) {
    // TODO consulo: no project/file in ParserDefinition.createLexer(LanguageVersion); upstream passes the project to the lexer to read
    //  custom start/end/outline tags from the project settings, here the settings are per module, so the parser always uses the defaults
    return new TemplateToolkitLexerAdapter(TemplateToolkitSettings.DEFAULT);
  }

  @Override
  public @NotNull PsiParser createParser(LanguageVersion languageVersion) {
    return new TemplateToolkitParser();
  }

  @Override
  public @NotNull IFileElementType getFileNodeType() {
    return TT2_FILE;
  }

  @Override
  public @NotNull TokenSet getWhitespaceTokens(LanguageVersion languageVersion) {
    return TemplateToolkitTokenSets.WHITE_SPACES;
  }

  @Override
  public @NotNull TokenSet getCommentTokens(LanguageVersion languageVersion) {
    return TemplateToolkitTokenSets.COMMENTS;
  }

  @Override
  public @NotNull TokenSet getStringLiteralElements(LanguageVersion languageVersion) {
    return TemplateToolkitTokenSets.LITERALS;
  }

  @Override
  public @NotNull PsiElement createElement(ASTNode node) {
    return PerlPsiElementFactory.create(node.getElementType(), node);
  }

  @Override
  public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
    return new TemplateToolkitFileImpl(viewProvider);
  }
}
