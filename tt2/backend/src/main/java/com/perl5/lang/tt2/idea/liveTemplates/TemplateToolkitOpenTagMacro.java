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

package com.perl5.lang.tt2.idea.liveTemplates;


import consulo.language.ast.IElementType;
import com.perl5.lang.tt2.idea.settings.TemplateToolkitSettings;
import com.perl5.lang.tt2.utils.TemplateToolkitPsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.perl5.lang.tt2.parser.TemplateToolkitElementTypesGenerated.TT2_OUTLINE_TAG;
import consulo.language.editor.template.Expression;
import consulo.language.editor.template.ExpressionContext;
import consulo.language.editor.template.macro.Macro;
import consulo.language.editor.template.Result;
import consulo.language.editor.template.context.TemplateContextType;
import consulo.language.editor.template.TextResult;
import consulo.localize.LocalizeValue;


public class TemplateToolkitOpenTagMacro extends Macro {
  @Override
  public String getName() {
    return "tt2OpenMarker";
  }

  @Override
  public LocalizeValue getPresentableName() {
    return LocalizeValue.of("tt2OpenMarker()");
  }

  @Override
  public @Nullable Result calculateQuickResult(@NotNull Expression @NotNull [] params, ExpressionContext context) {
    // consulo: settings of the module of the edited file
    TemplateToolkitSettings settings = TemplateToolkitSettings.forEditor(context.getProject(), context.getEditor());
    return getResultByTokenType(settings, TemplateToolkitPsiUtil.getLastOpenMarker(context.getEditor()));
  }

  protected @Nullable Result getResultByTokenType(TemplateToolkitSettings settings, IElementType tokenType) {
    if (tokenType == TT2_OUTLINE_TAG) {
      return new TextResult(settings.getOutlineTag());
    }
    return new TextResult(settings.getStartTag());
  }


  @Override
  public @Nullable Result calculateResult(@NotNull Expression @NotNull [] params, ExpressionContext context) {
    return calculateQuickResult(params, context);
  }

  @Override
  public boolean isAcceptableInContext(TemplateContextType context) {
    return context instanceof TemplateToolkitTemplateContextType;
  }
}
