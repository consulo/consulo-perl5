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

package com.perl5.lang.pod.parser.psi.mixin;

import consulo.annotation.access.RequiredReadAction;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.ast.IElementType;
import consulo.language.psi.PsiUtilCore;
import com.perl5.lang.pod.parser.psi.PodRenderingContext;
import com.perl5.lang.pod.parser.psi.stubs.PodSectionStub;
import com.perl5.lang.pod.parser.psi.util.PodRenderUtil;
import consulo.localize.LocalizeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.perl5.lang.pod.parser.PodElementTypesGenerated.POD_NUMBER;

public class PodSectionItem extends PodTitledSectionMixin {
  public PodSectionItem(@NotNull ASTNode node) {
    super(node);
  }

  public PodSectionItem(@NotNull PodSectionStub stub, @NotNull IElementType nodeType) {
    super(stub, nodeType);
  }

  @Override
  @RequiredReadAction
  public LocalizeValue getPresentableText() {
    PodSectionStub greenStub = getGreenStub();
    if (greenStub != null) {
      String content = greenStub.getContent();
      return content.isEmpty() ? LocalizeValue.empty() : LocalizeValue.of(content.substring(1));
    }

    if (isTargetable()) {
      return super.getPresentableText();
    }
    PsiElement contentBlock = getContentBlock();
    if (contentBlock == null) {
      return LocalizeValue.empty();
    }
    return LocalizeValue.of(PodRenderUtil.renderPsiElementAsText(contentBlock)).truncateWithEllipsis(140);
  }

  @Override
  @RequiredReadAction
  public @Nullable String getTitleText() {
    PodSectionStub greenStub = getGreenStub();
    if (greenStub != null) {
      String content = greenStub.getContent();
      return content.isEmpty() ? "" : content.substring(1);
    }
    return super.getTitleText();
  }

  /**
   * @return true iff this item may be targeted from link (has title)
   */
  @RequiredReadAction
  public boolean isTargetable() {
    PodSectionStub stub = getGreenStub();
    if (stub != null) {
      return stub.getContent().startsWith("+");
    }
    return getTitleElement() != null;
  }

  /**
   * Checks if this item defines bullteted list, as described in http://perldoc.perl.org/perlpodspec.html#About-%3dover...%3dback-Regions
   *
   * @return true if list should be bulleted
   */
  @RequiredReadAction
  public boolean isBulleted() {
    return !isTargetable() && !isNumbered();
  }

  /**
   * @return true iff this item is numbered one
   */
  @RequiredReadAction
  public boolean isNumbered() {
    if (isTargetable()) {
      return false;
    }
    PsiElement run = getFirstChild();
    while (run != null) {
      if (PsiUtilCore.getElementType(run) == POD_NUMBER) {
        return true;
      }
      run = run.getNextSibling();
    }
    return false;
  }

  /**
   * @return true iff item should be rendered with {@code <li>..</li>} tags
   */
  @RequiredReadAction
  private boolean isRenderAsListItem() {
    PsiElement container = getParent();
    return container instanceof PodOverSectionContent overSectionContent &&
           (overSectionContent.isBulleted() || overSectionContent.isNumbered());
  }

  @Override
  @RequiredReadAction
  public void renderElementAsHTML(StringBuilder builder, PodRenderingContext context) {
    boolean isListItem = isRenderAsListItem();

    if (isListItem) {
      builder.append("<li>");
      super.renderElementAsHTML(builder, context);
      builder.append("</li>");
    }
    else {
      builder.append("<dt style=\"padding-bottom:4px;font-weight:bold;\">");
      super.renderElementTitleAsHTML(builder, context);
      builder.append("</dt>");

      StringBuilder elementBuilder = new StringBuilder();
      super.renderElementContentAsHTML(elementBuilder, context);
      if (!elementBuilder.isEmpty()) {
        builder.append("<dd style=\"padding-top:6px;\">");
        builder.append(elementBuilder);
        builder.append("</dd>");
      }
    }
  }
}
