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

package com.perl5.lang.mason2;

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
import com.perl5.lang.mason2.lexer.Mason2TemplatingLexer;
import com.perl5.lang.mason2.lexer.Mason2TemplatingLexerAdapter;
import com.perl5.lang.mason2.psi.impl.MasonTemplatingFileImpl;
import com.perl5.lang.perl.lexer.PerlTemplatingLexer;
import com.perl5.lang.mason2.parser.Mason2TemplatingParserImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.perl5.lang.mason2.elementType.Mason2ElementTypes.*;


@ExtensionImpl
public class Mason2TemplatingParserDefinition extends Mason2ParserDefinition {
  @Override
  public @NotNull Language getLanguage() {
    return Mason2TemplatingLanguage.INSTANCE;
  }

  @Override
  public @NotNull IFileElementType getFileNodeType() {
    return COMPONENT_FILE;
  }

  @Override
  public @NotNull Lexer createLexer(LanguageVersion languageVersion) {
    // TODO consulo: no project in ParserDefinition.createLexer(LanguageVersion); upstream passes it to the lexer
    return new Mason2TemplatingLexerAdapter(null, false);
  }

  @Override
  public @NotNull PsiParser createParser(LanguageVersion languageVersion) {
    return new Mason2TemplatingParserImpl();
  }

  @Override
  public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
    return new MasonTemplatingFileImpl(viewProvider);
  }

  @Override
  public int getLexerStateFor(@NotNull ASTNode contextNode, @NotNull IElementType elementType) {
    return PerlTemplatingLexer.packState(super.getLexerStateFor(contextNode, elementType),
                                         getTemplatingState(contextNode, elementType));
  }

  private int getTemplatingState(@NotNull ASTNode contextNode, @NotNull IElementType elementType) {
    if (elementType == MASON_TEMPLATE_BLOCK_HTML) {
      return Mason2TemplatingLexer.YYINITIAL;
    }
    PsiElement parent = contextNode.getPsi().getParent();
    ASTNode run = parent instanceof DummyHolder ? Objects.requireNonNull(parent.getContext()).getNode() : contextNode;

    while (run != null) {
      if (StringUtil.containsLineBreak(run.getChars())) {
        return Mason2TemplatingLexer.PERL;
      }
      else if (run.getElementType() == MASON_LINE_OPENER) {
        return Mason2TemplatingLexer.PERL_LINE;
      }
      run = TreeUtil.prevLeaf(run);
    }

    return Mason2TemplatingLexer.YYINITIAL;
  }
}
