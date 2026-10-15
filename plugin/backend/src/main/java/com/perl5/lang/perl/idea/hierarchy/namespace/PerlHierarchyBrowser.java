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

package com.perl5.lang.perl.idea.hierarchy.namespace;


import consulo.annotation.access.RequiredReadAction;
import consulo.language.editor.hierarchy.HierarchyKind;
import consulo.language.editor.hierarchy.HierarchyModel;
import consulo.language.editor.hierarchy.HierarchyRequest;
import consulo.language.editor.hierarchy.HierarchyViewType;
import consulo.language.editor.hierarchy.StandardHierarchyKinds;
import consulo.language.editor.hierarchy.StandardHierarchyViewTypes;
import consulo.ui.ex.tree.NodeDescriptor;
import consulo.language.psi.PsiElement;
import com.perl5.lang.perl.idea.hierarchy.namespace.treestructures.PerlSubTypesHierarchyTreeStructure;
import com.perl5.lang.perl.idea.hierarchy.namespace.treestructures.PerlSuperTypesHierarchyTreeStructure;
import com.perl5.lang.perl.psi.PerlNamespaceDefinitionElement;
import com.perl5.lang.perl.psi.properties.PerlIdentifierOwner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.List;
import consulo.language.editor.hierarchy.HierarchyNodeDescriptor;
import consulo.language.editor.hierarchy.HierarchyTreeStructure;


/**
 * consulo: IntelliJ TypeHierarchyBrowserBase -> Consulo HierarchyModel, the browser UI (trees, view actions, alphabetical sorting,
 * legend) is provided by the platform
 */
public class PerlHierarchyBrowser implements HierarchyModel<PsiElement> {
  private final @NotNull PsiElement myTarget;

  public PerlHierarchyBrowser(PsiElement element) {
    myTarget = element;
  }

  @Override
  public HierarchyKind getKind() {
    return StandardHierarchyKinds.TYPE;
  }

  @Override
  public PsiElement getTarget() {
    return myTarget;
  }

  // consulo: upstream prependActions() adds ViewSupertypesHierarchyAction, ViewSubtypesHierarchyAction and AlphaSortAction;
  // the type hierarchy tree is created too, but has no structure (getTypesHierarchyStructure() returns null)
  @Override
  public List<HierarchyViewType> getViewTypes() {
    return List.of(StandardHierarchyViewTypes.SUPERTYPES, StandardHierarchyViewTypes.SUBTYPES);
  }

  // consulo: upstream PerlTypeHierarchyProvider#browserActivated switches the browser to the supertypes view
  @RequiredReadAction
  @Override
  public HierarchyViewType getDefaultViewType() {
    return StandardHierarchyViewTypes.SUPERTYPES;
  }

  // consulo: no HierarchyModel counterpart, HierarchyNodeDescriptor#canBeDeleted() is false by default as well
  protected boolean isInterface(@NotNull PsiElement psiElement) {
    return false;
  }

  // consulo: no HierarchyModel counterpart, HierarchyNodeDescriptor#canBeDeleted() is false by default as well
  protected boolean canBeDeleted(PsiElement psiElement) {
    return false;
  }

  // consulo: no HierarchyModel counterpart, only used for deletion which is not allowed
  @RequiredReadAction
  protected @Nullable String getQualifiedName(PsiElement psiElement) {
    if (psiElement instanceof PerlIdentifierOwner identifierOwner) {
      return identifierOwner.getPresentableName().getNullIfEmpty();
    }

    return null;
  }

  // consulo: no HierarchyModel counterpart, the platform uses HierarchyNodeDescriptor#getHierarchyElement() (the same element)
  protected @Nullable PsiElement getElementFromDescriptor(@NotNull HierarchyNodeDescriptor descriptor) {
    if (!(descriptor instanceof PerlHierarchyNodeDescriptor hierarchyNodeDescriptor)) {
      return null;
    }
    return hierarchyNodeDescriptor.getPerlElement();
  }

  @RequiredReadAction
  @Override
  public boolean isApplicableElement(@NotNull PsiElement element) {
    return element instanceof PerlNamespaceDefinitionElement;
  }

  @RequiredReadAction
  @Override
  public @Nullable HierarchyTreeStructure createTreeStructure(@NotNull HierarchyRequest<PsiElement> request) {
    return createHierarchyTreeStructure(request.getViewType(), request.getTarget());
  }

  protected @Nullable HierarchyTreeStructure createHierarchyTreeStructure(@NotNull HierarchyViewType typeName, @NotNull PsiElement psiElement) {
    if (StandardHierarchyViewTypes.SUPERTYPES.equals(typeName)) {
      return getSuperTypesHierarchyStructure(psiElement);
    }
    else if (StandardHierarchyViewTypes.SUBTYPES.equals(typeName)) {
      return getSubTypesHierarchyStructure(psiElement);
    }
    else if (StandardHierarchyViewTypes.CLASS.equals(typeName)) {
      return getTypesHierarchyStructure(psiElement);
    }
    return null;
  }

  // consulo: no HierarchyModel counterpart, the platform browser chooses alphabetical or source order
  protected @Nullable Comparator<NodeDescriptor<?>> getComparator() {
    return null;
  }

  protected @Nullable HierarchyTreeStructure getSuperTypesHierarchyStructure(PsiElement psiElement) {
    return new PerlSuperTypesHierarchyTreeStructure(psiElement);
  }

  protected @Nullable HierarchyTreeStructure getSubTypesHierarchyStructure(PsiElement psiElement) {
    return new PerlSubTypesHierarchyTreeStructure(psiElement);
  }

  protected @Nullable HierarchyTreeStructure getTypesHierarchyStructure(PsiElement psiElement) {
    return null;
  }
}
