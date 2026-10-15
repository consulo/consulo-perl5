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

package com.perl5.lang.perl.idea.regexp;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.parser.PsiParser;
import consulo.language.lexer.Lexer;
import consulo.language.version.LanguageVersion;
import consulo.language.file.FileViewProvider;
import consulo.language.psi.PsiFile;
import consulo.language.ast.IFileElementType;
import org.intellij.lang.regexp.*;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

import static org.intellij.lang.regexp.RegExpCapability.POSIX_BRACKET_EXPRESSIONS;


@ExtensionImpl
public class Perl5RegexpParserDefinition extends RegExpParserDefinition {
  private static final IFileElementType PERL5_REGEXP_FILE = new IFileElementType("PERL5_REGEXP_FILE", Perl5RegexpLanguage.INSTANCE);
  private final EnumSet<RegExpCapability> CAPABILITIES = EnumSet.of(
    POSIX_BRACKET_EXPRESSIONS
  );

  // consulo: parser definitions are registered by language
  @Override
  public @NotNull Language getLanguage() {
    return Perl5RegexpLanguage.INSTANCE;
  }

  @Override
  public @NotNull Lexer createLexer(@NotNull LanguageVersion languageVersion) {
    return new RegExpLexer(CAPABILITIES);
  }

  @Override
  public @NotNull PsiParser createParser(@NotNull LanguageVersion languageVersion) {
    return new RegExpParser(CAPABILITIES);
  }

  @Override
  public @NotNull IFileElementType getFileNodeType() {
    return PERL5_REGEXP_FILE;
  }

  @Override
  public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
    return new RegExpFile(viewProvider, Perl5RegexpLanguage.INSTANCE);
  }
}
