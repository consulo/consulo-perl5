/*
 * Copyright 2015-2024 Alexandr Evstigneev
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

package com.perl5.lang.perl.idea.structureView.groupers;

import consulo.project.ui.view.tree.AbstractTreeNode;

import consulo.navigation.ItemPresentation;
import consulo.language.psi.PsiElementNavigationItem;
import consulo.util.lang.StringUtil;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.SmartPointerManager;
import consulo.language.psi.SmartPsiElementPointer;
import consulo.util.collection.FactoryMap;
import com.perl5.PerlBundle;
import com.perl5.PerlIcons;
import com.perl5.lang.perl.idea.structureView.elements.PerlStructureViewElement;
import com.perl5.lang.perl.parser.moose.psi.impl.PerlMooseAttributeHandler;
import com.perl5.lang.perl.psi.impl.PerlSubCallElement;
import com.perl5.lang.perl.psi.light.PerlLightMethodDefinitionElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import consulo.localize.LocalizeValue;
import consulo.ui.image.Image;
import java.util.*;
import consulo.fileEditor.structureView.tree.ActionPresentation;
import consulo.fileEditor.structureView.tree.Group;
import consulo.ide.impl.idea.ide.util.treeView.smartTree.GroupWrapper;
import consulo.fileEditor.structureView.tree.Grouper;
import consulo.fileEditor.structureView.tree.TreeElement;

public class PerlAttributeGrouper implements Grouper, ActionPresentation {
  private static final String GROUPER_ID = "perl.structure.group.attributes";

  @Override
  // consulo: parent is passed as Object (it is an AbstractTreeNode)
  public @NotNull Collection<Group> group(@NotNull Object parent, @NotNull Collection<TreeElement> children) {
    if (children.isEmpty() || parent instanceof GroupWrapper groupWrapper && groupWrapper.getValue() instanceof AttributeGroup) {
      return Collections.emptyList();
    }

    Map<PerlSubCallElement, AttributeGroup> groupMap = FactoryMap.create(AttributeGroup::new);

    for (TreeElement childTreeElement : children) {
      if (!(childTreeElement instanceof PerlStructureViewElement structureViewElement)) {
        continue;
      }
      Object value = structureViewElement.getValue();
      if (!(value instanceof PerlLightMethodDefinitionElement<?> lightMethodDefinitionElement)) {
        continue;
      }
      PsiElement delegate = lightMethodDefinitionElement.getDelegate();
      if (!PerlMooseAttributeHandler.isMooseAttributeWrapper(delegate)) {
        continue;
      }
      assert delegate instanceof PerlSubCallElement;
      groupMap.get(delegate).addChild(childTreeElement);
    }

    return new ArrayList<>(groupMap.values());
  }

  @Override
  public @NotNull ActionPresentation getPresentation() {
    return this;
  }

  @Override
  public @NotNull String getName() {
    return GROUPER_ID;
  }

  @Override
  public @NotNull LocalizeValue getText() {
    return LocalizeValue.localizeTODO(PerlBundle.message("perl.structure.group.attributes.title"));
  }

  @Override
  public @NotNull LocalizeValue getDescription() {
    return LocalizeValue.localizeTODO(PerlBundle.message("perl.structure.group.attributes.description"));
  }

  @Override
  public Image getIcon() {
    return PerlIcons.ATTRIBUTE_GROUP_ICON;
  }

  private static class AttributeGroup implements Group, ItemPresentation, PsiElementNavigationItem {
    private final @NotNull SmartPsiElementPointer<PerlSubCallElement> mySubCallElementPointer;
    private final @NotNull List<TreeElement> myChildren = new ArrayList<>();

    public AttributeGroup(@NotNull PerlSubCallElement attributeWrapper) {
      mySubCallElementPointer = SmartPointerManager.createPointer(attributeWrapper);
    }

    public void addChild(@NotNull TreeElement child) {
      myChildren.add(child);
    }

    @Override
    public @NotNull ItemPresentation getPresentation() {
      return this;
    }

    @Override
    public @NotNull Collection<TreeElement> getChildren() {
      return myChildren;
    }

    @Override
    public @Nullable String getPresentableText() {
      PerlSubCallElement element = mySubCallElementPointer.getElement();
      if (element == null) {
        return PerlBundle.message("perl.presentation.invalid");
      }
      List<String> namesList = PerlMooseAttributeHandler.notNullFrom(element).getAttributesNames(element);
      String names = namesList.isEmpty() ? PerlBundle.message("perl.structure.attributes.unknown") : StringUtil.join(namesList, ", ");
      return StringUtil.shortenPathWithEllipsis(names, 30);
    }

    @Override
    public @Nullable String getLocationString() {
      PsiFile containingFile = mySubCallElementPointer.getContainingFile();
      return containingFile == null ? PerlBundle.message("perl.presentation.invalid") : containingFile.getName();
    }

    @Override
    public @Nullable Image getIcon(boolean unused) {
      return PerlIcons.ATTRIBUTE_GUTTER_ICON;
    }

    @Override
    public @Nullable PsiElement getTargetElement() {
      return mySubCallElementPointer.getElement();
    }

    @Override
    public @Nullable String getName() {
      return "Attribute name";
    }

    @Override
    public void navigate(boolean requestFocus) {
      PerlSubCallElement element = mySubCallElementPointer.getElement();
      if (element != null) {
        element.navigate(requestFocus);
      }
    }

    @Override
    public boolean canNavigate() {
      PerlSubCallElement element = mySubCallElementPointer.getElement();
      return element != null && element.canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
      PerlSubCallElement element = mySubCallElementPointer.getElement();
      return element != null && element.canNavigateToSource();
    }
  }
}
