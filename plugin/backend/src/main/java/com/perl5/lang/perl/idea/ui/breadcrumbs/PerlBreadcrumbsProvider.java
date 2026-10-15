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

package com.perl5.lang.perl.idea.ui.breadcrumbs;

import com.perl5.lang.perl.PerlLanguage;
import com.perl5.lang.perl.psi.PerlFile;
import com.perl5.lang.perl.psi.PerlMethodModifier;
import com.perl5.lang.perl.psi.PerlNamespaceDefinitionElement;
import com.perl5.lang.perl.psi.PerlSubDefinitionElement;
import com.perl5.lang.perl.psi.PerlSubExpr;
import com.perl5.lang.perl.psi.PsiPerlBlock;
import com.perl5.lang.perl.psi.impl.PerlPolyNamedElement;
import com.perl5.lang.perl.psi.light.PerlDelegatingLightNamedElement;
import com.perl5.lang.perl.psi.properties.PerlBlockOwner;
import consulo.annotation.access.RequiredReadAction;
import consulo.language.Language;
import consulo.language.icon.IconDescriptorUpdaters;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.navigation.ItemPresentation;
import consulo.ui.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

// TODO consulo: Consulo has no breadcrumbs API (BreadcrumbsProvider was dropped in favour of the navigation bar), so this
//  class is not an extension; the methods are kept (getStructuralParentElement is used by usage grouping rules)
public class PerlBreadcrumbsProvider {

  public Language[] getLanguages() {
    return PerlLanguage.ARRAY;
  }

  public boolean acceptElement(@NotNull PsiElement element) {
    return element instanceof PerlFile ||
           element instanceof PerlMethodModifier ||
           element instanceof PerlSubDefinitionElement subDefinitionElement && subDefinitionElement.getSubName() != null ||
           element instanceof PerlSubExpr && (!(element.getParent() instanceof PerlBlockOwner)) ||
           element instanceof PerlNamespaceDefinitionElement namespaceDefinitionElement &&
           namespaceDefinitionElement.getNamespaceName() != null;
  }

  @RequiredReadAction
  public @Nullable PsiElement getParent(@NotNull PsiElement element) {
    PsiElement structuralParent = getStructuralParentElement(element);
    return structuralParent instanceof PsiFile ? null : structuralParent;
  }

  @RequiredReadAction
  public static @Nullable PsiElement getStructuralParentElement(@NotNull PsiElement element) {
    switch (element) {
      case PerlFile ignored -> {
        return null;
      }
      case PerlNamespaceDefinitionElement namespaceDefinition -> {
        return namespaceDefinition.getContainingFile();
      }
      case PerlSubDefinitionElement subDefinition -> {
        return PsiTreeUtil.getParentOfType(subDefinition, PerlNamespaceDefinitionElement.class);
      }
      default -> {
      }
    }

    PsiElement nearestParent =
      PsiTreeUtil.getParentOfType(element,
                                  PerlSubDefinitionElement.class,
                                  PerlNamespaceDefinitionElement.class,
                                  PerlMethodModifier.class,
                                  PerlSubExpr.class);
    if (nearestParent == null ||
        nearestParent instanceof PerlMethodModifier ||
        nearestParent instanceof PerlSubDefinitionElement ||
        nearestParent instanceof PerlNamespaceDefinitionElement) {
      return nearestParent;
    }

    PsiPerlBlock exprBlock = ((PerlSubExpr)nearestParent).getBlock();
    if (exprBlock == null) {
      return getStructuralParentElement(nearestParent.getParent());
    }

    PerlPolyNamedElement<?> polyNamedElement = PsiTreeUtil.getParentOfType(nearestParent, PerlPolyNamedElement.class);
    if (polyNamedElement != null) {
      for (PerlDelegatingLightNamedElement<?> lightNamedElement : polyNamedElement.getLightElements()) {
        if (lightNamedElement instanceof PerlSubDefinitionElement subDefinitionElement &&
            exprBlock.equals(subDefinitionElement.getSubDefinitionBody())) {
          return lightNamedElement;
        }
      }
    }
    return nearestParent;
  }

  @RequiredReadAction
  public @Nullable Image getElementIcon(@NotNull PsiElement element) {
    return IconDescriptorUpdaters.getIcon(element, 0);
  }

  public @NotNull String getElementInfo(@NotNull PsiElement element) {
    switch (element) {
      case PerlFile file -> {
        return file.getName();
      }
      case PerlSubDefinitionElement subDefinition -> {
        return subDefinition.getSubName() + "()";
      }
      case PerlNamespaceDefinitionElement namespaceDefinition -> {
        return Objects.requireNonNull(namespaceDefinition.getNamespaceName());
      }
      case PerlMethodModifier methodModifier -> {
        ItemPresentation presentation = methodModifier.getPresentation();
        if (presentation != null) {
          return presentation.getPresentableText().get();
        }
      }
      case PerlSubExpr ignored -> {
        return "sub()";
      }
      default -> {
      }
    }
    throw new RuntimeException("Can't happen: " + element);
  }
}
