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

package com.perl5.lang.perl.idea.inspections;

import consulo.language.editor.inspection.ProblemsHolder;
import consulo.util.lang.StringUtil;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementVisitor;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.psi.PerlNamespaceDefinitionElement;
import com.perl5.lang.perl.psi.PerlNamespaceDefinitionHandler;
import com.perl5.lang.perl.psi.PerlNamespaceDefinitionWithIdentifier;
import com.perl5.lang.perl.psi.PerlVisitor;
import com.perl5.lang.perl.util.PerlPackageUtilCore;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.localize.LocalizeValue;
import com.perl5.lang.perl.PerlLanguage;
import org.jetbrains.annotations.Nullable;


@ExtensionImpl
public class PerlNamespaceRecursiveInheritanceInspection extends PerlInspection {
  @Override
  public @NotNull PsiElementVisitor buildVisitor(final @NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new PerlVisitor() {

      @Override
      protected boolean shouldVisitLightElements() {
        return true;
      }

      @Override
      public void visitPerlNamespaceDefinitionWithIdentifier(@NotNull PerlNamespaceDefinitionWithIdentifier o) {
        PsiElement nameIdentifier = o.getNameIdentifier();
        if (nameIdentifier == null) {
          return;
        }

        String packageName = o.getNamespaceName();

        if (StringUtil.isEmpty(packageName) || PerlPackageUtilCore.MAIN_NAMESPACE_NAME.equals(packageName)) {
          return;
        }

        if (hasRecursiveInheritance(o, new HashSet<>())) {
          registerError(holder, o.getContainingFile(),
                        PerlBundle.message("inspection.message.namespace.has.recursive.inheritance", packageName));
          registerError(holder, nameIdentifier, PerlBundle.message("inspection.message.namespace.has.recursive.inheritance", packageName));
        }
      }
    };
  }

  private static boolean hasRecursiveInheritance(@NotNull PerlNamespaceDefinitionElement definition,
                                                 @NotNull Set<? super String> passedWay) {
    passedWay.add(definition.getNamespaceName());
    for (PerlNamespaceDefinitionElement element : PerlNamespaceDefinitionHandler.instance(definition)
      .getParentNamespaceDefinitions(definition)) {
      if (passedWay.contains(element.getNamespaceName())) {
        return true;
      }
      if (hasRecursiveInheritance(element, new HashSet<>(passedWay))) {
        return true;
      }
    }
    return false;
  }

  // consulo: metadata from upstream <localInspection> registration in perl5.plugin.backend.main.xml
  @Override
  public @NotNull String getShortName() {
    return "NamespaceRecursiveInheritance";
  }

  @Override
  public @Nullable Language getLanguage() {
    return PerlLanguage.INSTANCE;
  }

  @Override
  public @NotNull LocalizeValue getDisplayName() {
    return LocalizeValue.localizeTODO(PerlBundle.message("inspection.namespace.recursive.inheritance.display.name"));
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
