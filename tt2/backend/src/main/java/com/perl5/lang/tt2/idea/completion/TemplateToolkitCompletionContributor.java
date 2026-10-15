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

package com.perl5.lang.tt2.idea.completion;

import com.perl5.lang.tt2.TemplateToolkitLanguage;
import consulo.language.Language;
import consulo.language.editor.completion.CompletionContributor;
import consulo.language.editor.completion.CompletionType;
import org.jetbrains.annotations.NotNull;

import static com.perl5.lang.tt2.elementTypes.TemplateToolkitElementPatterns.BLOCK_NAME_USAGE_PATTERN;
import static com.perl5.lang.tt2.elementTypes.TemplateToolkitElementPatterns.FILTER_NAME_PATTERN;


public class TemplateToolkitCompletionContributor extends CompletionContributor {
  @Override
  public @NotNull Language getLanguage() {
    return TemplateToolkitLanguage.INSTANCE;
  }

  public TemplateToolkitCompletionContributor() {
    extend(
      CompletionType.BASIC,
      FILTER_NAME_PATTERN,
      new TemplateToolkitFilterNamesCompletionProvider()
    );

    extend(
      CompletionType.BASIC,
      BLOCK_NAME_USAGE_PATTERN,
      new TemplateToolkitBlocksCompletionProvider()
    );
  }
}
