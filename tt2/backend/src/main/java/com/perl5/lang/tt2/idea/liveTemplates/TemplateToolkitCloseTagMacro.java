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

import consulo.language.editor.template.Result;
import consulo.language.editor.template.TextResult;
import consulo.localize.LocalizeValue;
import consulo.language.ast.IElementType;
import com.perl5.lang.tt2.idea.settings.TemplateToolkitSettings;
import org.jetbrains.annotations.Nullable;

import static com.perl5.lang.tt2.parser.TemplateToolkitElementTypesGenerated.TT2_OUTLINE_TAG;


public class TemplateToolkitCloseTagMacro extends TemplateToolkitOpenTagMacro {
  @Override
  public String getName() {
    return "tt2CloseMarker";
  }

  @Override
  public LocalizeValue getPresentableName() {
    return LocalizeValue.of("tt2CloseMarker()");
  }

  @Override
  protected @Nullable Result getResultByTokenType(TemplateToolkitSettings settings, IElementType tokenType) {
    if (tokenType == TT2_OUTLINE_TAG) {
      return new TextResult("");
    }
    return new TextResult(settings.getEndTag());
  }
}
