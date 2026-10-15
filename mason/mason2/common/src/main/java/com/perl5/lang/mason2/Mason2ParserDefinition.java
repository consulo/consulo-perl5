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
import consulo.language.parser.PsiParser;
import consulo.language.version.LanguageVersion;
import consulo.language.file.FileViewProvider;
import consulo.language.psi.PsiFile;
import consulo.language.ast.IFileElementType;
import consulo.language.ast.TokenSet;
import com.perl5.lang.mason2.elementType.Mason2TokenSets;
import com.perl5.lang.mason2.psi.impl.MasonFileImpl;
import com.perl5.lang.perl.PerlParserDefinition;
import com.perl5.lang.mason2.parser.Mason2ParserImpl;
import org.jetbrains.annotations.NotNull;

import static com.perl5.lang.mason2.elementType.Mason2ElementTypes.PP_FILE;


@ExtensionImpl
public class Mason2ParserDefinition extends PerlParserDefinition {
  @Override
  public @NotNull Language getLanguage() {
    return Mason2Language.INSTANCE;
  }

  @Override
  public @NotNull IFileElementType getFileNodeType() {
    return PP_FILE;
  }

  @Override
  public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
    return new MasonFileImpl(viewProvider);
  }

  @Override
  public @NotNull TokenSet getCommentTokens(LanguageVersion languageVersion) {
    return Mason2TokenSets.COMMENTS;
  }

  @Override
  public @NotNull PsiParser createParser(LanguageVersion languageVersion) {
    return new Mason2ParserImpl();
  }
}
