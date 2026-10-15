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

package com.perl5.lang.mason2.psi.impl;

import consulo.annotation.access.RequiredReadAction;
import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import com.perl5.lang.htmlmason.MasonCoreUtilCore;
import com.perl5.lang.mason2.idea.configuration.MasonSettings;
import com.perl5.lang.mason2.psi.MasonNamespaceDefinition;
import com.perl5.lang.perl.psi.PerlNamespaceElement;
import com.perl5.lang.perl.psi.PerlVariableDeclarationElement;
import com.perl5.lang.perl.psi.impl.PsiPerlNamespaceDefinitionImpl;
import com.perl5.lang.perl.psi.stubs.namespaces.PerlNamespaceDefinitionStub;
import consulo.localize.LocalizeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MasonNamespaceDefinitionImpl extends PsiPerlNamespaceDefinitionImpl implements MasonNamespaceDefinition {
  protected List<PerlVariableDeclarationElement> myImplicitVariables = null;
  /**
   * consulo: settings the implicit variables were built for, upstream uses the change counter of the project settings
   */
  protected MasonSettings myImplicitVariablesSettings;

  public MasonNamespaceDefinitionImpl(ASTNode node) {
    super(node);
  }

  public MasonNamespaceDefinitionImpl(PerlNamespaceDefinitionStub stub, IElementType nodeType) {
    super(stub, nodeType);
  }

  @RequiredReadAction
  protected @NotNull List<PerlVariableDeclarationElement> buildImplicitVariables(MasonSettings masonSettings) {
    List<PerlVariableDeclarationElement> newImplicitVariables = new ArrayList<>();

    if (isValid()) {
      MasonCoreUtilCore.fillVariablesList(this, newImplicitVariables, masonSettings.getGlobalVariables());
    }
    return newImplicitVariables;
  }

  @Override
  @RequiredReadAction
  public PerlNamespaceElement getNamespaceElement() {
    return null;
  }

  @Override
  @RequiredReadAction
  public @Nullable String getNamespaceName() {
    return MasonNamespaceDefinitionService.getInstance().getNamespaceName(this);
  }

  @Override
  @RequiredReadAction
  protected @Nullable String computeNamespaceName() {
    return MasonNamespaceDefinitionService.getInstance().computeNamespaceName(this);
  }

  @Override
  @RequiredReadAction
  public LocalizeValue getPresentableName() {
    return MasonNamespaceDefinitionService.getInstance().getPresentableName(this);
  }

  @Override
  public @NotNull MasonFileImpl getContainingFile() {
    return (MasonFileImpl)super.getContainingFile();
  }

  @Override
  @RequiredReadAction
  public @NotNull List<PerlVariableDeclarationElement> getImplicitVariables() {
    MasonSettings settings = MasonSettings.forPsiElement(this);
    if (myImplicitVariables == null || myImplicitVariablesSettings != settings) {
      myImplicitVariables = buildImplicitVariables(settings);
      myImplicitVariablesSettings = settings;
    }
    return myImplicitVariables;
  }
}
