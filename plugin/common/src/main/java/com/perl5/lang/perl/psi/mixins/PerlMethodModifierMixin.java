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

package com.perl5.lang.perl.psi.mixins;

import consulo.annotation.access.RequiredReadAction;
import consulo.language.controlFlow.Instruction;
import consulo.language.ast.ASTNode;
import consulo.localize.LocalizeValue;
import consulo.navigation.ItemPresentation;
import consulo.util.lang.lazy.ClearableLazyValue;
import consulo.language.psi.PsiElement;
import consulo.language.psi.util.PsiTreeUtil;
import com.perl5.lang.perl.idea.codeInsight.controlFlow.PerlControlFlowBuilder;
import com.perl5.lang.perl.idea.presentations.PerlItemPresentationSimpleDynamicLocation;
import com.perl5.lang.perl.idea.ui.PerlIconProvider;
import com.perl5.lang.perl.psi.PerlMethodModifier;
import com.perl5.lang.perl.psi.PerlSubNameElement;
import com.perl5.lang.perl.psi.impl.PerlCompositeElementImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import consulo.ui.image.Image;

public abstract class PerlMethodModifierMixin extends PerlCompositeElementImpl implements PerlMethodModifier {
  private final ClearableLazyValue<Instruction[]> myControlFlow = PerlControlFlowBuilder.createLazy(this);

  public PerlMethodModifierMixin(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  @RequiredReadAction
  public @Nullable String getName() {
    PerlSubNameElement subNameElement = getSubNameElement();
    return subNameElement == null ? null : subNameElement.getText();
  }

  @RequiredReadAction
  private @Nullable PerlSubNameElement getSubNameElement() {
    return PsiTreeUtil.getChildOfType(this, PerlSubNameElement.class);
  }

  // TODO consulo: PsiElement#getIcon(int) does not exist in Consulo (element icons come from IconDescriptorUpdater),
  //  so this is no longer an override and is not called by the platform
  public Image getIcon(int flags) {
    return PerlIconProvider.getIcon(this);
  }

  @Override
  @RequiredReadAction
  public ItemPresentation getPresentation() {
    String name = getName();
    return name == null ? null
      : new PerlItemPresentationSimpleDynamicLocation(this, LocalizeValue.of(getModifierText() + " " + name + "()"));
  }

  @Override
  @RequiredReadAction
  public @NotNull PsiElement getNavigationElement() {
    PerlSubNameElement subNameElement = getSubNameElement();
    return subNameElement == null ? this : subNameElement;
  }

  @Override
  public @NotNull Instruction[] getControlFlow() {
    return myControlFlow.get();
  }

  @Override
  public void subtreeChanged() {
    myControlFlow.clear();
  }
}
