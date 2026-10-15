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

package com.perl5.lang.perl.psi.impl;

import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiManager;
import consulo.language.util.IncorrectOperationException;
import com.perl5.PerlIcons;
import com.perl5.lang.perl.psi.PerlNamespaceDefinitionWithIdentifier;
import com.perl5.lang.perl.psi.PerlNamespaceElement;
import com.perl5.lang.perl.psi.mro.PerlMroType;
import com.perl5.lang.perl.psi.utils.PerlNamespaceAnnotations;
import com.perl5.lang.perl.util.PerlPackageUtilCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import consulo.ui.image.Image;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PerlImplicitNamespaceDefinition extends PerlImplicitElement implements PerlNamespaceDefinitionWithIdentifier {
  private final @NotNull String myNamespaceName;

  public PerlImplicitNamespaceDefinition(@NotNull PsiManager manager,
                                         @NotNull String namespaceName,
                                         @Nullable PsiElement parent) {
    super(manager, parent);
    myNamespaceName = PerlPackageUtilCore.getCanonicalNamespaceName(namespaceName);
  }

  @Override
  public String toString() {
    return "Implicit namespace: " + getPresentableName();
  }

  @Override
  public @NotNull PerlMroType getMroType() {
    return PerlMroType.DFS;
  }

  @Override
  public String getName() {
    return getNamespaceName();
  }

  @Override
  public @NotNull String getNamespaceName() {
    return myNamespaceName;
  }

  // TODO consulo: PsiElement#getIcon(int) does not exist in Consulo (element icons come from IconDescriptorUpdater),
  //  so this is no longer an override and is not called by the platform
  public @Nullable Image getIcon(int flags) {
    return PerlIcons.PACKAGE_GUTTER_ICON;
  }

  @Override
  public @NotNull List<String> getParentNamespacesNames() {
    return Collections.emptyList();
  }

  @Override
  public @Nullable PerlNamespaceAnnotations getAnnotations() {
    return null;
  }

  @Override
  public @NotNull List<String> getEXPORT() {
    return Collections.emptyList();
  }

  @Override
  public @NotNull List<String> getEXPORT_OK() {
    return Collections.emptyList();
  }

  @Override
  public @NotNull Map<String, List<String>> getEXPORT_TAGS() {
    return Collections.emptyMap();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PerlImplicitNamespaceDefinition that)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    return getNamespaceName().equals(that.getNamespaceName());
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + getNamespaceName().hashCode();
    return result;
  }

  @Override
  public @Nullable PsiElement getNameIdentifier() {
    return null;
  }

  @Override
  public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
    return this;
  }

  @Override
  public @Nullable PerlNamespaceElement getNamespaceElement() {
    return null;
  }
}
