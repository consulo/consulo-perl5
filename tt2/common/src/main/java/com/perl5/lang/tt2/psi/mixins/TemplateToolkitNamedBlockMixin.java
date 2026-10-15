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

package com.perl5.lang.tt2.psi.mixins;

import consulo.language.ast.ASTNode;
import consulo.util.lang.StringUtil;
import consulo.language.psi.PsiElement;
import consulo.language.impl.psi.LeafPsiElement;
import consulo.language.util.IncorrectOperationException;
import com.perl5.lang.tt2.TemplateToolkitBundle;
import com.perl5.lang.tt2.TemplateToolkitIcons;
import com.perl5.lang.tt2.psi.PsiBlockName;
import com.perl5.lang.tt2.psi.TemplateToolkitNamedBlock;
import com.perl5.lang.tt2.psi.impl.TemplateToolkitCompositeElementImpl;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import consulo.ui.image.Image;


public abstract class TemplateToolkitNamedBlockMixin extends TemplateToolkitCompositeElementImpl implements TemplateToolkitNamedBlock {
  public TemplateToolkitNamedBlockMixin(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public @Nullable PsiElement getNameIdentifier() {
    PsiBlockName blockName = getBlockDirective().getBlockName();
    if (blockName == null) {
      return null;
    }

    return blockName.getFirstChild();
  }

  @Override
  public PsiElement setName(@NonNls @NotNull String name) throws IncorrectOperationException {
    if (StringUtil.isEmpty(name)) {
      throw new IncorrectOperationException(TemplateToolkitBundle.message("ttk2.error.empty.block.name"));
    }

    PsiElement nameIdentifier = getNameIdentifier();
    if (nameIdentifier instanceof LeafPsiElement leafPsiElement) {
      leafPsiElement.replaceWithText(name);
    }

    return this;
  }

  @Override
  public String getName() {
    PsiElement nameIdentifier = getNameIdentifier();
    return nameIdentifier == null ? super.getName() : nameIdentifier.getText();
  }

  @Override
  public int getTextOffset() {
    PsiElement nameIdentifier = getNameIdentifier();
    return nameIdentifier == null ? super.getTextOffset() : nameIdentifier.getTextOffset();
  }

  // consulo: PsiElement#getIcon(int) does not exist in Consulo (element icons come from IconDescriptorUpdater),
  //  so this is no longer an override; it is called by consulo.perl5.tt2.TemplateToolkitIconDescriptorUpdater
  public @Nullable Image getIcon(int flags) {
    return TemplateToolkitIcons.TTK2_BLOCK_ICON;
  }
}
