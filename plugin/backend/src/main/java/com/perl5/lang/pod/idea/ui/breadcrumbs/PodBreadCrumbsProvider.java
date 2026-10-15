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

package com.perl5.lang.pod.idea.ui.breadcrumbs;

import consulo.language.Language;
import consulo.util.lang.StringUtil;
import consulo.language.psi.PsiElement;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.language.icon.IconDescriptorUpdaters;
import com.perl5.lang.pod.PodLanguage;
import com.perl5.lang.pod.parser.psi.PodTitledSection;
import com.perl5.lang.pod.parser.psi.mixin.PodSectionItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import consulo.ui.image.Image;
import java.util.Objects;

// TODO consulo: Consulo has no breadcrumbs API (BreadcrumbsProvider was dropped in favour of the navigation bar), so this
//  class is not an extension; the methods are kept (getStructuralParentElement is used by usage grouping rules)
public class PodBreadCrumbsProvider {
  public Language[] getLanguages() {
    return PodLanguage.ARRAY;
  }

  public boolean acceptElement(@NotNull PsiElement element) {
    return element instanceof PodTitledSection titledSection && StringUtil.isNotEmpty(titledSection.getTitleText()) &&
           !(element instanceof PodSectionItem sectionItem && sectionItem.isBulleted());
  }

  public @Nullable Image getElementIcon(@NotNull PsiElement element) {
    return IconDescriptorUpdaters.getIcon(element, 0);
  }

  public @Nullable PsiElement getParent(@NotNull PsiElement element) {
    return getStructuralParentElement(element);
  }

  public static @Nullable PsiElement getStructuralParentElement(@NotNull PsiElement element) {
    PodTitledSection parentSection = PsiTreeUtil.getParentOfType(element, PodTitledSection.class);
    if (parentSection == null) {
      return null;
    }
    if (parentSection instanceof PodSectionItem podSectionItem && podSectionItem.isBulleted() ||
        StringUtil.isEmpty(parentSection.getTitleText())) {
      return getStructuralParentElement(parentSection);
    }
    return parentSection;
  }

  public @NotNull String getElementInfo(@NotNull PsiElement element) {
    if (element instanceof PodTitledSection titledSection) {
      return Objects.requireNonNull(titledSection.getTitleText());
    }

    throw new RuntimeException();
  }
}
