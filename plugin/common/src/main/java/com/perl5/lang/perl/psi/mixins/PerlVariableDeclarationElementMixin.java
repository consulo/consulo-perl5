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
import consulo.annotation.access.RequiredWriteAction;
import consulo.language.ast.ASTNode;
import consulo.localize.LocalizeValue;
import consulo.navigation.ItemPresentation;
import consulo.util.lang.StringUtil;
import consulo.language.psi.PsiElement;
import consulo.language.psi.scope.LocalSearchScope;
import consulo.content.scope.SearchScope;
import consulo.language.ast.IElementType;
import consulo.language.util.IncorrectOperationException;
import com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlScalarValue;
import com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlValue;
import com.perl5.lang.perl.idea.presentations.PerlItemPresentationSimpleDynamicLocation;
import com.perl5.lang.perl.psi.*;
import com.perl5.lang.perl.psi.properties.PerlLexicalScope;
import com.perl5.lang.perl.psi.stubs.variables.PerlVariableDeclarationStub;
import com.perl5.lang.perl.psi.utils.PerlPsiUtil;
import com.perl5.lang.perl.psi.utils.PerlVariableAnnotations;
import com.perl5.lang.perl.psi.utils.PerlVariableType;
import com.perl5.lang.perl.util.PerlPackageUtilCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import consulo.language.icon.IconDescriptorUpdaters;
import consulo.ui.image.Image;

import java.util.Objects;

import static com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlValues.UNKNOWN_VALUE;

/**
 * Stubbed wrapper for variables declarations
 */
public class PerlVariableDeclarationElementMixin extends PerlStubBasedPsiElementBase<PerlVariableDeclarationStub>
  implements PerlVariableDeclarationElement {
  public PerlVariableDeclarationElementMixin(ASTNode node) {
    super(node);
  }

  public PerlVariableDeclarationElementMixin(@NotNull PerlVariableDeclarationStub stub, @NotNull IElementType nodeType) {
    super(stub, nodeType);
  }

  @Override
  @RequiredReadAction
  public @NotNull PerlVariable getVariable() {
    return Objects.requireNonNull(findChildByClass(PerlVariable.class), () -> "Unable to find variable in: " + getText());
  }

  @Override
  @RequiredReadAction
  public @Nullable PsiElement getNameIdentifier() {
    return getVariable().getVariableNameElement();
  }

  @Override
  @RequiredReadAction
  public String getVariableName() {
    return getName();
  }

  @Override
  @RequiredReadAction
  public String getName() {
    PerlVariableDeclarationStub stub = getGreenStub();
    if (stub != null) {
      return stub.getVariableName();
    }
    return getVariable().getName();
  }

  @Override
  @RequiredWriteAction
  public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
    PerlPsiUtil.renameNamedElement(this, name);
    return this;
  }

  @Override
  @RequiredReadAction
  public int getTextOffset() {
    PsiElement nameIdentifier = getNameIdentifier();

    return nameIdentifier == null
           ? super.getTextOffset()
           : nameIdentifier.getTextOffset();
  }

  @Override
  @RequiredReadAction
  public @Nullable String getExplicitNamespaceName() {
    return getVariable().getExplicitNamespaceName();
  }

  @Override
  public @NotNull PerlValue getDeclaredValue() {
    PerlValue valueFromAnnotations = PerlVariableDeclarationElement.super.getDeclaredValue();
    if (!valueFromAnnotations.isUnknown()) {
      return valueFromAnnotations;
    }
    return getPsiDeclaredValue();
  }

  public @NotNull PerlValue getPsiDeclaredValue() {
    PerlVariableDeclarationStub stub = getGreenStub();
    if (stub != null) {
      return stub.getDeclaredValue();
    }
    PerlVariableDeclarationExpr declaration = getDeclarationExpression();
    return declaration == null ? UNKNOWN_VALUE :
           PerlScalarValue.create(declaration.getDeclarationType());
  }

  @Override
  @RequiredReadAction
  public @NotNull String getNamespaceName() {
    PerlVariableDeclarationStub stub = getGreenStub();
    if (stub != null) {
      return stub.getNamespaceName();
    }
    String qualifierName = getVariable().getExplicitNamespaceName();
    if (StringUtil.isNotEmpty(qualifierName)) {
      return qualifierName;
    }
    return PerlPackageUtilCore.getContextNamespaceName(getVariable());
  }

  @Override
  @RequiredReadAction
  public PerlVariableType getActualType() {
    PerlVariableDeclarationStub stub = getGreenStub();
    if (stub != null) {
      return stub.getActualType();
    }
    return getVariable().getActualType();
  }

  @Override
  public @NotNull SearchScope getUseScope() {
    if (isLexicalDeclaration()) {
      PsiElement lexicalScope = PerlLexicalScope.from(this);
      if (lexicalScope instanceof PerlSubDefinition) {
        lexicalScope = getContainingFile();
      }
      else if (lexicalScope instanceof PerlForeachCompound) {
        lexicalScope = PerlLexicalScope.from(lexicalScope);
      }
      if (lexicalScope != null) {
        return new LocalSearchScope(lexicalScope);
      }
    }

    return super.getUseScope();
  }

  @Override
  @RequiredReadAction
  public boolean isLexicalDeclaration() {
    if (getGreenStub() != null) {
      return false;
    }
    PsiElement parent = getParent();
    return parent instanceof PerlLexicalVariableDeclarationMarker ||
           isInvocantDeclaration() || isLocalDeclaration();
  }

  @Override
  @RequiredReadAction
  public boolean isInvocantDeclaration() {
    return getParent() instanceof PsiPerlMethodSignatureInvocant;
  }

  @Override
  @RequiredReadAction
  public boolean isLocalDeclaration() {
    return getParent() instanceof PsiPerlVariableDeclarationLocal;
  }

  @Override
  @RequiredReadAction
  public boolean isGlobalDeclaration() {
    return !isLexicalDeclaration();
  }

  @Override
  @RequiredReadAction
  public ItemPresentation getPresentation() {
    return new PerlItemPresentationSimpleDynamicLocation(this, LocalizeValue.ofNullable(getName()));
  }


  // TODO consulo: PsiElement#getIcon(int) does not exist in Consulo (element icons come from IconDescriptorUpdater),
  //  so this is no longer an override and is not called by the platform
  @RequiredReadAction
  public @Nullable Image getIcon(int flags) {
    Image iconByType = getIconByType(getActualType());
    return iconByType == null ? IconDescriptorUpdaters.getIcon(this, flags) : iconByType;
  }

  @Override
  public @Nullable PerlVariableAnnotations getVariableAnnotations() {
    PerlVariableAnnotations variableAnnotations;

    PerlVariableDeclarationStub stub = getGreenStub();
    variableAnnotations = stub != null ? stub.getVariableAnnotations() : getLocalVariableAnnotations();

    if (!variableAnnotations.isEmpty()) {
      return variableAnnotations;
    }

    return null;
  }

  @Override
  public @NotNull PerlVariableAnnotations getLocalVariableAnnotations() {
    return PerlVariableAnnotations.from(this);
  }
}
