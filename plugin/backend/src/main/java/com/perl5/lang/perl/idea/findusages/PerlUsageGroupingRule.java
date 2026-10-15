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

package com.perl5.lang.perl.idea.findusages;

import consulo.annotation.access.RequiredReadAction;
import consulo.navigation.ItemPresentation;
import consulo.util.lang.StringUtil;
import consulo.language.psi.PsiElement;

import consulo.usage.rule.PsiElementUsage;
import consulo.usage.rule.SingleParentUsageGroupingRule;
import com.perl5.lang.perl.PerlLanguage;
import com.perl5.lang.perl.idea.ui.breadcrumbs.PerlBreadcrumbsProvider;
import com.perl5.lang.perl.psi.PerlFile;
import com.perl5.lang.perl.psi.PerlMethodModifier;
import com.perl5.lang.perl.psi.PerlNamespaceDefinitionElement;
import com.perl5.lang.perl.psi.PerlSubDefinitionElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.usage.PsiElementUsageGroupBase;
import consulo.usage.PsiNamedElementUsageGroupBase;
import consulo.usage.Usage;
import consulo.usage.UsageGroup;
import consulo.usage.UsageTarget;
import consulo.usage.UsageView;

class PerlUsageGroupingRule extends SingleParentUsageGroupingRule {
  @Override
  @RequiredReadAction
  protected @Nullable UsageGroup getParentGroupFor(@NotNull Usage usage, UsageTarget @NotNull [] targets) {
    if (!(usage instanceof PsiElementUsage psiElementUsage)) {
      return null;
    }
    PsiElement element = psiElementUsage.getElement();
    if (element == null || !element.getLanguage().isKindOf(PerlLanguage.INSTANCE)) {
      return null;
    }
    return computeParentGroupFor(element);
  }

  @RequiredReadAction
  private @Nullable UsageGroup computeParentGroupFor(@Nullable PsiElement element) {
    if (element == null) {
      return null;
    }
    PsiElement structuralParentElement = PerlBreadcrumbsProvider.getStructuralParentElement(element);

    if (structuralParentElement instanceof PerlFile perlFile) {
      structuralParentElement = perlFile.getNamespaceDefinitionElement();
    }

    if (structuralParentElement == null) {
      return null;
    }

    if (structuralParentElement instanceof PerlSubDefinitionElement subDefinitionElement) {
      String name = StringUtil.notNullize(subDefinitionElement.getCanonicalName());
      return new PsiNamedElementUsageGroupBase<>((PerlSubDefinitionElement)structuralParentElement) {
        @Override
        public @NotNull String getText(@Nullable UsageView view) {
          return name;
        }
      };
    }

    if (structuralParentElement instanceof PerlNamespaceDefinitionElement namespaceDefinitionElement) {
      return new PsiNamedElementUsageGroupBase<>(namespaceDefinitionElement);
    }

    if (structuralParentElement instanceof PerlMethodModifier) {
      return new PsiElementUsageGroupBase<>((PerlMethodModifier)structuralParentElement) {
        @Override
        @RequiredReadAction
        public @NotNull String getText(@Nullable UsageView view) {
          PerlMethodModifier modifier = getElement();
          if (modifier != null) {
            ItemPresentation presentation = modifier.getPresentation();
            if (presentation != null) {
              return presentation.getPresentableText().get();
            }
          }
          return super.getText(view);
        }
      };
    }

    return computeParentGroupFor(structuralParentElement.getParent());
  }
}
