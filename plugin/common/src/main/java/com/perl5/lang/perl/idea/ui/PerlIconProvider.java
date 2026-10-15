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

package com.perl5.lang.perl.idea.ui;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.icon.IconDescriptor;
import consulo.language.icon.IconDescriptorUpdater;
import consulo.language.psi.PsiElement;
import consulo.language.ast.IElementType;
import consulo.language.psi.PsiUtilCore;
import com.perl5.PerlIcons;
import com.perl5.lang.perl.psi.PerlSubExpr;
import com.perl5.lang.perl.psi.PerlVariable;
import com.perl5.lang.perl.psi.PerlVariableDeclarationElement;
import com.perl5.lang.perl.psi.utils.PerlVariableType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import consulo.ui.image.Image;

import static com.perl5.lang.perl.parser.PerlElementTypesGenerated.*;

// consulo: IntelliJ IconProvider -> Consulo IconDescriptorUpdater
@ExtensionImpl
public class PerlIconProvider implements IconDescriptorUpdater {
  @RequiredReadAction
  @Override
  public void updateIcon(@NotNull IconDescriptor iconDescriptor, @NotNull PsiElement element, int flags) {
    Image icon = getIcon(element);
    if (icon != null) {
      iconDescriptor.setMainIcon(icon);
    }
  }

  public static @Nullable Image getIcon(@Nullable PsiElement element) {
    if (element instanceof PerlSubExpr) {
      return PerlIcons.ANON_SUB_ICON;
    }
    if (element instanceof PerlVariable perlVariable) {
      return getIcon(perlVariable.getActualType());
    }
    else if (element instanceof PerlVariableDeclarationElement variableDeclarationElement) {
      return getIcon(variableDeclarationElement.getActualType());
    }
    IElementType elementType = PsiUtilCore.getElementType(element);
    if (elementType == AFTER_MODIFIER) {
      return PerlIcons.AFTER_MODIFIER_GUTTER_ICON;
    }
    if (elementType == BEFORE_MODIFIER) {
      return PerlIcons.BEFORE_MODIFIER_GUTTER_ICON;
    }
    if (elementType == AROUND_MODIFIER) {
      return PerlIcons.AROUND_MODIFIER_GUTTER_ICON;
    }
    if (elementType == AUGMENT_MODIFIER) {
      return PerlIcons.AUGMENT_MODIFIER_GUTTER_ICON;
    }
    return null;
  }

  public static @Nullable Image getIcon(@Nullable PerlVariableType actualType) {
    return actualType == null ? null : actualType.getIcon();
  }
}

