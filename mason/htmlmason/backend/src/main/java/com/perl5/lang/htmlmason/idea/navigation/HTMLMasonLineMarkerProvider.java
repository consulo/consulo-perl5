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

package com.perl5.lang.htmlmason.idea.navigation;

import consulo.language.editor.gutter.RelatedItemLineMarkerInfo;
import consulo.language.editor.gutter.RelatedItemLineMarkerProvider;
import consulo.language.editor.ui.navigation.NavigationGutterIconBuilder;
import consulo.application.AllIcons;
import consulo.util.lang.StringUtil;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import com.perl5.lang.htmlmason.HTMLMasonLanguage;
import com.perl5.lang.htmlmason.HTMLMasonUtil;
import consulo.language.Language;
import com.perl5.lang.htmlmason.HtmlMasonBundle;
import com.perl5.lang.htmlmason.parser.psi.HTMLMasonMethodDefinition;
import com.perl5.lang.htmlmason.parser.psi.impl.HTMLMasonFileImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;


public class HTMLMasonLineMarkerProvider extends RelatedItemLineMarkerProvider {
  @Override
  public @NotNull Language getLanguage() {
    return HTMLMasonLanguage.INSTANCE;
  }

  @Override
  protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo> result) {
    if (element instanceof HTMLMasonFileImpl htmlMasonFile) {
      HTMLMasonFileImpl parentComponent = HTMLMasonUtil.getParentComponent(htmlMasonFile);
      if (parentComponent != null) {
        NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder
          .create(AllIcons.Gutter.ImplementingMethod)
          .setTargets(parentComponent)
          .setTooltipText(HtmlMasonBundle.message("tooltip.parent.component"));

        result.add(builder.createLineMarkerInfo(element));
      }

      List<HTMLMasonFileImpl> childComponents = HTMLMasonUtil.getChildComponents(htmlMasonFile);
      if (!childComponents.isEmpty()) {
        NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder
          .create(AllIcons.Gutter.ImplementedMethod)
          .setTargets(childComponents)
          .setTooltipText(HtmlMasonBundle.message("tooltip.child.components"));

        result.add(builder.createLineMarkerInfo(element));
      }
    }
    else if (element instanceof HTMLMasonMethodDefinition masonMethodDefinition) {
      String methodName = masonMethodDefinition.getName();
      PsiFile component = element.getContainingFile();
      var identifier = masonMethodDefinition.getNameIdentifier();
      var markerTarget = identifier == null ? element.getFirstChild() : identifier;

      if (StringUtil.isNotEmpty(methodName) && component instanceof HTMLMasonFileImpl htmlMasonFile) {
        // method in parent components
        HTMLMasonMethodDefinition methodDefinition = HTMLMasonUtil.findMethodDefinitionByNameInParents(htmlMasonFile, methodName);
        if (methodDefinition != null) {
          NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder
            .create(AllIcons.Gutter.OverridingMethod)
            .setTargets(methodDefinition)
            .setTooltipText(HtmlMasonBundle.message("tooltip.overriding.method"));

          result.add(builder.createLineMarkerInfo(markerTarget));
        }

        // method in subcomponents
        List<HTMLMasonMethodDefinition> methodDefinitions =
          HTMLMasonUtil.findMethodDefinitionByNameInChildComponents(htmlMasonFile, methodName);
        if (!methodDefinitions.isEmpty()) {
          NavigationGutterIconBuilder<PsiElement> builder = NavigationGutterIconBuilder
            .create(AllIcons.Gutter.OverridenMethod)
            .setTargets(methodDefinitions)
            .setTooltipText(HtmlMasonBundle.message("tooltip.overridden.methods"));

          result.add(builder.createLineMarkerInfo(markerTarget));
        }
      }
    }
  }
}
