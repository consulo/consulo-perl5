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

package com.perl5.lang.htmlmason.idea.formatter;

import consulo.language.Language;
import consulo.language.codeStyle.Alignment;
import consulo.language.codeStyle.FormattingContext;
import consulo.language.codeStyle.Indent;
import consulo.language.codeStyle.Wrap;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.codeStyle.CodeStyleSettings;
import consulo.xml.psi.formatter.xml.XmlFormattingPolicy;
import consulo.language.psi.PsiUtilCore;
import com.perl5.lang.htmlmason.HTMLMasonLanguage;
import com.perl5.lang.htmlmason.elementType.HTMLMasonElementTypes;
import com.perl5.lang.perl.idea.formatter.PerlXmlTemplateFormattingModelBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class HTMLMasonFormattingModelBuilder
  extends PerlXmlTemplateFormattingModelBuilder<HTMLMasonFormattingContext, HTMLMasonFormattingBlock> {
  @Override
  public @NotNull Language getLanguage() {
    return HTMLMasonLanguage.INSTANCE;
  }

  @Override
  protected HTMLMasonFormattingBlock createTemplateLanguageBlock(ASTNode node,
                                                                 CodeStyleSettings settings,
                                                                 XmlFormattingPolicy xmlFormattingPolicy,
                                                                 Indent indent,
                                                                 @Nullable Alignment alignment,
                                                                 @Nullable Wrap wrap,
                                                                 @NotNull HTMLMasonFormattingContext context) {
    return new HTMLMasonFormattingBlock(this, node, wrap, alignment, settings, xmlFormattingPolicy, indent, context);
  }

  @Override
  protected @NotNull HTMLMasonFormattingContext createContext(@NotNull FormattingContext formattingContext) {
    return new HTMLMasonFormattingContext(formattingContext);
  }

  @Override
  protected boolean isTemplateFile(PsiFile file) {
    return file.getLanguage().isKindOf(HTMLMasonLanguage.INSTANCE);
  }

  @Override
  public boolean isOuterLanguageElement(PsiElement element) {
    return PsiUtilCore.getElementType(element) == HTMLMasonElementTypes.HTML_MASON_OUTER_ELEMENT_TYPE;
  }

  @Override
  public boolean isMarkupLanguageElement(PsiElement element) {
    return PsiUtilCore.getElementType(element) == HTMLMasonElementTypes.HTML_MASON_TEMPLATE_BLOCK_HTML;
  }
}
