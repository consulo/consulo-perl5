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


import consulo.language.lexer.DelegateLexer;
import consulo.language.lexer.Lexer;
import consulo.logging.Logger;
import consulo.application.progress.ProgressManager;
import consulo.project.Project;
import consulo.util.lang.lazy.ClearableLazyValue;
import consulo.language.file.FileViewProvider;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.ast.TokenType;
import consulo.language.ast.IElementType;
import consulo.language.ast.IReparseableElementType;
import consulo.language.ast.TokenSet;
import consulo.language.psi.PsiUtilCore;
import consulo.language.version.LanguageVersionUtil;
import com.perl5.lang.perl.PerlLanguage;
import com.perl5.lang.perl.parser.PerlParserImpl;
import com.perl5.lang.perl.parser.builder.PerlPsiBuilderFactory;
import com.perl5.lang.perl.psi.PerlLexerAwareParserDefinition;
import com.perl5.lang.perl.util.PerlTimeLogger;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;

import static com.perl5.lang.perl.idea.editor.PerlBraceMatcher.PERL_BRACES_MAP;
import static com.perl5.lang.perl.idea.editor.PerlBraceMatcher.PERL_BRACES_MAP_REVERSED;
import static com.perl5.lang.perl.parser.PerlElementTypesGenerated.*;
import consulo.language.impl.ast.LazyParseableElement;
import consulo.language.ast.ASTNode;
import consulo.language.Language;
import consulo.language.parser.ParserDefinition;
import consulo.language.parser.PsiBuilder;


public abstract class PerlReparseableElementType extends IReparseableElementType {
  private static final ClearableLazyValue<TokenSet> REPARSING_PREVENTING_CONTAINERS = ClearableLazyValue.notNull(
    () -> TokenSet.create(
      HEREDOC_QQ, HEREDOC_QX, STRING_DQ, STRING_XQ, MATCH_REGEX, COMPILE_REGEX, REPLACEMENT_REGEX
    ));
  protected static final Logger LOG = Logger.getInstance(PerlReparseableElementType.class);

  public PerlReparseableElementType(@NotNull @NonNls String debugName) {
    super(debugName, PerlLanguage.INSTANCE);
  }

  @Override
  public @Nullable ASTNode createNode(CharSequence text) {
    // consulo: IntelliJ ASTFactory.DefaultFactoryHolder.DEFAULT.createLazy() creates a plain LazyParseableElement for this type
    return new LazyParseableElement(this, text);
  }

  @Override
  public ASTNode parseContents(@NotNull ASTNode chameleon) {
    PerlTimeLogger logger = PerlTimeLogger.create(LOG);
    PsiElement parentElement = chameleon.getTreeParent().getPsi();
    Project project = parentElement.getProject();
    CharSequence newChars = chameleon.getChars();
    PsiBuilder builder = PerlPsiBuilderFactory.Companion.createBuilder(
      project,
      chameleon,
      createLexer(chameleon),
      getLanguage(),
      newChars);

    ASTNode result =
      PerlParserImpl.INSTANCE.parse(this, builder, LanguageVersionUtil.findLanguageVersion(getLanguage(), parentElement)).getFirstChildNode();
    logger.trace("Parsed: ", PerlTimeLogger.kb(newChars.length()), " kb of ", this);
    return result;
  }

  @Override
  public final boolean isParsable(@Nullable ASTNode parent,
                                  @NotNull CharSequence buffer,
                                  @NotNull Language fileLanguage,
                                  @NotNull Project project) {
    if (parent == null || !isParentOk(parent)) {
      return false;
    }
    return isReparseableOld(parent, buffer, fileLanguage, project);
  }

  protected boolean isParentOk(@NotNull ASTNode parent) {
    ASTNode run = parent;
    while (run != null) {
      if (REPARSING_PREVENTING_CONTAINERS.get().contains(PsiUtilCore.getElementType(run))) {
        return false;
      }
      run = run.getTreeParent();
    }
    return true;
  }

  protected abstract boolean isReparseableOld(@NotNull ASTNode parent,
                                              @NotNull CharSequence buffer,
                                              @NotNull Language fileLanguage,
                                              @NotNull Project project);

  @Override
  public String toString() {
    return "Perl5: " + super.toString();
  }

  protected static boolean checkBracesBalance(@NotNull Lexer lexer, Stack<? super IElementType> bracesStack) {
    while (true) {
      ProgressManager.checkCanceled();
      IElementType type = lexer.getTokenType();

      if (type == null) {
        //eof: checking balance
        return bracesStack.isEmpty();
      }

      if (bracesStack.isEmpty()) {
        //the last brace is not the last token
        return false;
      }

      if (PERL_BRACES_MAP.containsKey(type)) {
        bracesStack.push(type);
      }
      else {
        IElementType oppositeBrace = PERL_BRACES_MAP_REVERSED.get(type);
        if (oppositeBrace != null) {
          if (bracesStack.isEmpty() || bracesStack.pop() != oppositeBrace) {
            return false;
          }
        }
      }
      lexer.advance();
    }
  }

  protected static void skipSpaces(@NotNull Lexer flexAdapter) {
    while (flexAdapter.getTokenType() == TokenType.WHITE_SPACE) {
      flexAdapter.advance();
    }
  }

  protected static @NotNull DelegateLexer createLexerWithCustomInitialState(int startState, Lexer lexer) {
    return new DelegateLexer(lexer) {
      @Override
      public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        super.start(buffer, startOffset, endOffset, startState);
      }
    };
  }

  public static @NotNull Lexer createLexer(@NotNull ASTNode nodeToLex) {
    return createLexer(nodeToLex, nodeToLex.getElementType());
  }

  public static @NotNull Lexer createLexer(@NotNull ASTNode context, @NotNull IElementType elementType) {
    PsiElement psiElement = context.getPsi();
    PsiFile containingFile = psiElement.getContainingFile();
    FileViewProvider fileViewProvider = containingFile.getViewProvider();
    Language baseLanguage = fileViewProvider.getBaseLanguage();
    ParserDefinition parserDefinition = ParserDefinition.forLanguage(baseLanguage);
    // TODO consulo: no project in ParserDefinition.createLexer(LanguageVersion); upstream passes psiElement.getProject() here
    Lexer lexer = parserDefinition.createLexer(LanguageVersionUtil.findLanguageVersion(baseLanguage, containingFile));
    int alternativeInitialState = parserDefinition instanceof PerlLexerAwareParserDefinition lexerAwareParserDefinition ?
      lexerAwareParserDefinition.getLexerStateFor(context, elementType) : 0;
    return alternativeInitialState == 0 ? lexer : createLexerWithCustomInitialState(alternativeInitialState, lexer);
  }
}