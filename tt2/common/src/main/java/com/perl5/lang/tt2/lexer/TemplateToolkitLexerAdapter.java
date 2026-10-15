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

package com.perl5.lang.tt2.lexer;

import com.perl5.lang.perl.lexer.PerlLexerWithCustomStatesAdapter;
import com.perl5.lang.tt2.idea.settings.TemplateToolkitSettings;
import org.jetbrains.annotations.NotNull;


public class TemplateToolkitLexerAdapter extends PerlLexerWithCustomStatesAdapter {
  /**
   * consulo: upstream takes the project to read its settings, here the settings of the module of the lexed file are passed
   */
  public TemplateToolkitLexerAdapter(@NotNull TemplateToolkitSettings settings) {
    super(new TemplateToolkitLexer(settings));
  }
}
