/*
 * Copyright 2015-2026 Alexandr Evstigneev
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

import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.StubBasedPsiElement;
import consulo.language.ast.IElementType;
import consulo.language.util.IncorrectOperationException;
import com.perl5.PerlIcons;
import com.perl5.lang.perl.parser.PerlElementTypesGenerated;
import com.perl5.lang.perl.psi.PerlDeprecatable;
import com.perl5.lang.perl.psi.PerlNamespaceElement;
import com.perl5.lang.perl.psi.PerlStubBasedPsiElementBase;
import com.perl5.lang.perl.psi.PerlSubElement;
import com.perl5.lang.perl.psi.properties.PerlLabelScope;
import com.perl5.lang.perl.psi.properties.PerlNamespaceElementContainer;
import com.perl5.lang.perl.psi.stubs.PerlSubStub;
import com.perl5.lang.perl.psi.utils.PerlAnnotations;
import com.perl5.lang.perl.psi.utils.PerlPsiUtil;
import com.perl5.lang.perl.psi.utils.PerlSubAnnotations;
import com.perl5.lang.perl.util.PerlPackageUtilCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import consulo.ui.image.Image;

public abstract class PerlSubBase<Stub extends PerlSubStub<?>> extends PerlStubBasedPsiElementBase<Stub>
  implements PerlSubElement, StubBasedPsiElement<Stub>,
             PerlNamespaceElementContainer, PerlDeprecatable, PerlLabelScope {
  public PerlSubBase(@NotNull ASTNode node) {
    super(node);
  }

  public PerlSubBase(@NotNull Stub stub, @NotNull IElementType nodeType) {
    super(stub, nodeType);
  }

  @Override
  public @Nullable String getNamespaceName() {
    Stub stub = getGreenStub();
    if (stub != null) {
      return stub.getNamespaceName();
    }

    String namespace = getExplicitNamespaceName();
    if (namespace == null) {
      namespace = PerlPackageUtilCore.getContextNamespaceName(this);
    }

    return namespace;
  }


  @Override
  public @Nullable PsiElement setName(@NotNull String name) throws IncorrectOperationException {
    return PerlPsiUtil.renameNamedElement(this, name);
  }

  @Override
  public @Nullable String getName() {
    return getSubName();
  }

  @Override
  public @Nullable String getSubName() {
    Stub stub = getGreenStub();
    if (stub != null) {
      return stub.getSubName();
    }

    return getSubNameHeavy();
  }

  protected @Nullable String getSubNameHeavy() {
    PsiElement nameIdentifier = getNameIdentifier();
    return nameIdentifier == null ? null : nameIdentifier.getText();
  }

  @Override
  public @Nullable String getExplicitNamespaceName() {
    PerlNamespaceElement namespaceElement = getNamespaceElement();
    return namespaceElement == null ? null : namespaceElement.getCanonicalName();
  }

  @Override
  public @Nullable PsiElement getNameIdentifier() {
    return findChildByType(PerlElementTypesGenerated.SUB_NAME);
  }

  @Override
  public @Nullable PerlNamespaceElement getNamespaceElement() {
    return findChildByClass(PerlNamespaceElement.class);
  }

  @Override
  public @Nullable PerlSubAnnotations getAnnotations() {
    Stub stub = getGreenStub();
    if (stub != null) {
      return stub.getAnnotations();
    }
    return PerlSubAnnotations.createFromAnnotationsList(PerlAnnotations.collectAnnotations(this));
  }

  // TODO consulo: PsiElement#getIcon(int) does not exist in Consulo (element icons come from IconDescriptorUpdater),
  //  so this is no longer an override and is not called by the platform
  public @NotNull Image getIcon(int flags) {
    return isMethod() ? PerlIcons.METHOD_GUTTER_ICON : PerlIcons.SUB_GUTTER_ICON;
  }

  @Override
  public int getTextOffset() {
    PsiElement nameIdentifier = this.getNameIdentifier();
    return nameIdentifier == null ? super.getTextOffset() : nameIdentifier.getTextOffset();
  }

  @Override
  public String toString() {
    return super.toString() + "@" + (isValid() ? getCanonicalName() : "!INVALID!");
  }
}
