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

package com.perl5.lang.perl.parser.moose.idea.inspections;

import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementVisitor;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.idea.inspections.PerlInspection;
import com.perl5.lang.perl.parser.moose.psi.impl.PerlAttributeDefinition;
import com.perl5.lang.perl.parser.moose.psi.impl.PerlMooseAttributeHandler;
import com.perl5.lang.perl.psi.PerlVisitor;
import com.perl5.lang.perl.psi.PsiPerlSubCall;
import com.perl5.lang.perl.psi.impl.PerlSubCallElement;
import com.perl5.lang.perl.psi.light.PerlDelegatingLightNamedElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.localize.LocalizeValue;
import com.perl5.lang.perl.PerlLanguage;
import org.jetbrains.annotations.Nullable;

@ExtensionImpl
public class MooseMultiAttributeAccessorInspection extends PerlInspection {
  @Override
  public @NotNull PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new PerlVisitor() {
      @Override
      public void visitSubCall(@NotNull PsiPerlSubCall o) {
        if (!PerlMooseAttributeHandler.isMooseAttributeWrapper(o)) {
          return;
        }

        List<? extends PerlDelegatingLightNamedElement<?>> lightElements = ((PerlSubCallElement)o).getLightElements();
        if (lightElements.size() < 2) {
          return;
        }
        List<PerlDelegatingLightNamedElement<?>> attributes = new ArrayList<>(lightElements.size());
        List<PerlDelegatingLightNamedElement<?>> accessors = new ArrayList<>(lightElements.size() - 1);
        lightElements.forEach(it -> {
          if (it instanceof PerlAttributeDefinition) {
            attributes.add(it);
          }
          else {
            accessors.add(it);
          }
        });

        if (attributes.size() < 2 || accessors.isEmpty()) {
          return;
        }
        accessors.forEach(it -> registerProblem(holder, it.getNameIdentifier(), PerlBundle.message("perl.inspection.multiattr.accessor")));
        List<PsiElement> attributesIdentifiers = attributes.stream()
          .map(PerlDelegatingLightNamedElement::getNameIdentifier)
          .distinct().toList();
        if (attributesIdentifiers.size() == 1) {
          // same identifier looks like an accessor
          registerProblem(holder, attributesIdentifiers.getFirst(), PerlBundle.message("perl.inspection.multiattr.accessor"));
        }
      }
    };
  }

  // consulo: metadata from upstream <localInspection> registration in perl5.plugin.backend.main.xml
  @Override
  public @NotNull String getShortName() {
    return "MooseAttributeAccessors";
  }

  @Override
  public @Nullable Language getLanguage() {
    return PerlLanguage.INSTANCE;
  }

  @Override
  public @NotNull LocalizeValue getDisplayName() {
    return LocalizeValue.localizeTODO(PerlBundle.message("inspection.moose.attribute.accessors.display.name"));
  }

  @Override
  public @NotNull LocalizeValue getGroupDisplayName() {
    return LocalizeValue.localizeTODO(PerlBundle.message("inspection.group.perl"));
  }

  @Override
  public @NotNull HighlightDisplayLevel getDefaultLevel() {
    return HighlightDisplayLevel.ERROR;
  }
}
