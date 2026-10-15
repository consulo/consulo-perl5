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

package com.perl5.lang.htmlmason.idea.livetemplates;

import consulo.language.editor.template.context.BaseTemplateContextType;
import consulo.language.editor.template.context.TemplateActionContext;
import consulo.localize.LocalizeValue;
import consulo.xml.codeInsight.template.XmlContextType;
import consulo.language.Language;
import consulo.html.language.HTMLLanguage;
import consulo.xhtml.language.XHTMLLanguage;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiUtilCore;
import org.jetbrains.annotations.NotNull;


public abstract class AbstractMasonTemplateContextType extends BaseTemplateContextType {
  // consulo: context id is declared in upstream xml (<liveTemplateContext contextId=...>)
  public AbstractMasonTemplateContextType(@NotNull String contextId, @NotNull String presentableName) {
    super(contextId, LocalizeValue.localizeTODO(presentableName));
  }

  protected abstract boolean isMyFile(PsiFile file);

  @Override
  public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
    var psiFile = templateActionContext.getFile();
    var startOffset = templateActionContext.getStartOffset();
    return isMyLanguage(PsiUtilCore.getLanguageAtOffset(psiFile, startOffset))
           && !XmlContextType.isEmbeddedContent(psiFile, startOffset)
           && isMyFile(psiFile);
  }

  static boolean isMyLanguage(Language language) {
    return language.isKindOf(HTMLLanguage.INSTANCE) || language.isKindOf(XHTMLLanguage.INSTANCE);
  }
}
