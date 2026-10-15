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

package com.perl5.lang.htmlmason.parser.psi;

import com.perl5.lang.htmlmason.HTMLMasonUtil;
import com.perl5.lang.htmlmason.parser.psi.impl.HTMLMasonFileImpl;
import consulo.annotation.access.RequiredReadAction;
import consulo.language.psi.ElementDescriptionLocation;
import consulo.language.psi.ElementDescriptionProvider;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiNamedElement;
import consulo.localize.LocalizeValue;
import consulo.usage.UsageViewLongNameLocation;
import consulo.usage.UsageViewNodeTextLocation;
import consulo.usage.UsageViewShortNameLocation;
import consulo.usage.UsageViewTypeLocation;
import org.jetbrains.annotations.NotNull;

public class HTMLMasonElementDescriptionProvider implements ElementDescriptionProvider {
  @Override
  @RequiredReadAction
  public LocalizeValue getElementDescription(@NotNull PsiElement element, @NotNull ElementDescriptionLocation location) {
    if (!(element instanceof HTMLMasonNamedElement namedElement)) {
      return LocalizeValue.empty();
    }
    if (location == UsageViewLongNameLocation.INSTANCE) {
      return LocalizeValue.of(
        HTMLMasonUtil.getAbsoluteComponentPath(((HTMLMasonFileImpl)element.getContainingFile())) + ":" +
          ((PsiNamedElement)element).getName()
      );
    }
    else if (location == UsageViewNodeTextLocation.INSTANCE) {
      return LocalizeValue.of(namedElement.getName() + HTMLMasonUtil.getArgumentsListAsString((HTMLMasonParametrizedEntity)element));
    }
    else if (location == UsageViewShortNameLocation.INSTANCE) {
      return LocalizeValue.of(namedElement.getName() + HTMLMasonUtil.getArgumentsListAsString((HTMLMasonParametrizedEntity)element));
    }
    else if (element instanceof HTMLMasonMethodDefinition) {
      if (location == UsageViewTypeLocation.INSTANCE) {
        return LocalizeValue.localizeTODO("HTML::Mason method");
      }
      return LocalizeValue.localizeTODO("HTML::Mason method for " + location.getClass().getSimpleName());
    }
    else if (element instanceof HTMLMasonSubcomponentDefitnition) {
      if (location == UsageViewTypeLocation.INSTANCE) {
        return LocalizeValue.localizeTODO("HTML::Mason subcomponent");
      }
      return LocalizeValue.localizeTODO("HTML::Mason subcomponent for " + location.getClass().getSimpleName());
    }
    return LocalizeValue.empty();
  }
}
