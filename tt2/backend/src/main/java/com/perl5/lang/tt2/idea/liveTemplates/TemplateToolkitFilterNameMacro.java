/*
 * Copyright 2015-2024 Alexandr Evstigneev
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

package com.perl5.lang.tt2.idea.liveTemplates;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.editor.completion.lookup.LookupElement;
import consulo.language.editor.template.Expression;
import consulo.language.editor.template.ExpressionContext;
import consulo.language.editor.template.macro.Macro;
import consulo.language.editor.template.Result;
import com.perl5.lang.tt2.TemplateToolkitFilterNames;
import consulo.localize.LocalizeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ExtensionImpl
public final class TemplateToolkitFilterNameMacro extends Macro implements TemplateToolkitFilterNames {

  @Override
  public @NotNull String getName() {
    return "tt2FilterName";
  }

  @Override
  public @NotNull LocalizeValue getPresentableName() {
    return LocalizeValue.of("tt2FilterName()");
  }

  @Override
  public @NotNull String getDefaultValue() {
    return "filtername";
  }

  @Override
  public @Nullable Result calculateResult(Expression @NotNull [] params, @Nullable ExpressionContext context) {
    return null;
  }

  @Override
  public @Nullable Result calculateQuickResult(Expression @NotNull [] params, @Nullable ExpressionContext context) {
    return calculateResult(params, context);
  }

  @Override
  public LookupElement @NotNull [] calculateLookupItems(Expression @NotNull [] params, @Nullable ExpressionContext context) {
    return LookupElements.getLOOKUP_ELEMENTS();
  }
}
