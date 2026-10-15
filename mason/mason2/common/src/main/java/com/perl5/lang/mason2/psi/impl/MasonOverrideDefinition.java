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

package com.perl5.lang.mason2.psi.impl;

import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.impl.psi.LeafPsiElement;
import consulo.language.ast.IElementType;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.language.util.IncorrectOperationException;
import com.perl5.lang.perl.extensions.PerlImplicitVariablesProvider;
import com.perl5.lang.perl.parser.moose.psi.impl.PerlMooseOverrideStatement;
import com.perl5.lang.perl.psi.PerlSubNameElement;
import com.perl5.lang.perl.psi.PerlVariableDeclarationElement;
import com.perl5.lang.perl.psi.PsiPerlBlock;
import com.perl5.lang.perl.psi.impl.PerlImplicitVariableDeclaration;
import com.perl5.lang.perl.psi.stubs.subsdefinitions.PerlSubDefinitionStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class MasonOverrideDefinition extends PerlMooseOverrideStatement implements PerlImplicitVariablesProvider {
  private volatile List<PerlVariableDeclarationElement> myImplicitVariables;

  public MasonOverrideDefinition(@NotNull ASTNode node) {
    super(node);
  }

  public MasonOverrideDefinition(@NotNull PerlSubDefinitionStub stub, @NotNull IElementType nodeType) {
    super(stub, nodeType);
  }

  private @NotNull List<PerlVariableDeclarationElement> buildImplicitVariables() {
    List<PerlVariableDeclarationElement> newImplicitVariables = new ArrayList<>();
    if (isValid()) {
      newImplicitVariables.add(PerlImplicitVariableDeclaration.createInvocant(this));
    }
    return newImplicitVariables;
  }

  @Override
  public @NotNull PsiPerlBlock getSubDefinitionBody() {
    return findNotNullChildByClass(PsiPerlBlock.class);
  }

  @Override
  public @Nullable PsiElement getNameIdentifier() {
    return PsiTreeUtil.getChildOfType(this, PerlSubNameElement.class);
  }

  @Override
  protected @Nullable String getSubNameHeavy() {
    PsiElement nameIdentifier = getNameIdentifier();
    if (nameIdentifier == null) {
      return null;
    }
    ASTNode node = nameIdentifier.getNode();
    return node == null ? null : node.getText();
  }

  @Override
  public @NotNull PsiElement setName(@NotNull String name) throws IncorrectOperationException {
    if (name.isEmpty()) {
      throw new IncorrectOperationException("You can't set an empty method name");
    }
    PsiElement nameIdentifier = getNameIdentifier();
    if (nameIdentifier instanceof LeafPsiElement leafPsiElement) {
      leafPsiElement.replaceWithText(name);
    }

    return this;
  }

  @Override
  public @NotNull List<PerlVariableDeclarationElement> getImplicitVariables() {
    // upstream uses Kotlin `by lazy`; lazily computed here the same way as PerlMethodDefinitionMixin does
    List<PerlVariableDeclarationElement> result = myImplicitVariables;
    if (result == null) {
      result = buildImplicitVariables();
      myImplicitVariables = result;
    }
    return result;
  }
}
