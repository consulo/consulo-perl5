/*
 * Copyright 2015-2019 Alexandr Evstigneev
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

package com.perl5.lang.perl.idea;

import com.perl5.lang.perl.PerlLanguage;
import com.perl5.lang.perl.parser.PerlParserUtil;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.refactoring.NamesValidator;
import consulo.project.Project;
import jakarta.annotation.Nonnull;
import org.jetbrains.annotations.NotNull;

@ExtensionImpl
public class PerlNamesValidator implements NamesValidator {

  @Override
  public boolean isKeyword(@NotNull String name, Project project) {
    return false;
  }

  @Override
  public boolean isIdentifier(@NotNull String name, Project project) {
    return PerlParserUtil.isIdentifier(name) || PerlParserUtil.isAmbiguousPackage(name);
  }

  @Nonnull
  @Override
  public Language getLanguage() {
    return PerlLanguage.INSTANCE;
  }
}
