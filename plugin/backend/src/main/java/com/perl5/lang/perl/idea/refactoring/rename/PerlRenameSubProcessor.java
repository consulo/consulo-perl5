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

package com.perl5.lang.perl.idea.refactoring.rename;

import consulo.codeEditor.Editor;
import consulo.ui.ex.awt.Messages;
import consulo.util.lang.StringUtil;
import consulo.language.psi.PsiElement;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.scope.PsiSearchScopeUtil;
import consulo.content.scope.SearchScope;
import consulo.language.editor.refactoring.rename.PsiElementRenameHandler;
import consulo.language.editor.refactoring.rename.RenamePsiElementProcessor;
import consulo.localize.LocalizeValue;
import consulo.ui.MessageBoxBuilder;
import consulo.ui.MessageButtonRole;
import consulo.ui.annotation.RequiredUIAccess;
import com.perl5.PerlBundle;
import com.perl5.PerlIcons;
import com.perl5.lang.perl.parser.Class.Accessor.psi.impl.PerlClassAccessorMethod;
import com.perl5.lang.perl.psi.PerlSubElement;
import com.perl5.lang.perl.util.PerlPackageUtil;
import com.perl5.lang.perl.util.PerlSubUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class PerlRenameSubProcessor extends RenamePsiElementProcessor {
  @Override
  public boolean canProcessElement(@NotNull PsiElement element) {
    return element instanceof PerlSubElement;
  }

  @Override
  public void prepareRenaming(@NotNull PsiElement element,
                              @NotNull String newName,
                              @NotNull Map<PsiElement, String> allRenames,
                              @NotNull SearchScope scope) {
    assert element instanceof PerlSubElement;
    PerlSubElement subElement = (PerlSubElement)element;
    for (PsiElement relatedItem : computeRelatedItems(subElement)) {
      if (PsiSearchScopeUtil.isInScope(scope, relatedItem)) {
        allRenames.put(relatedItem, newName);
      }
    }
  }

  @Override
  public @Nullable PsiElement substituteElementToRename(@NotNull PsiElement element, @Nullable Editor editor) {
    if (PsiElementRenameHandler.isVetoed(element)) {
      return null;
    }

    if (element instanceof PerlSubElement subElement && subElement.isMethod()) {
      return suggestSuperMethod(subElement);
    }

    return super.substituteElementToRename(element, editor);
  }

  /**
   * consulo: in-place rename takes the element through the callback, so the SUPER method question is asked with the unified
   * (asynchronous) message box there, Messages are Swing dialogs (not available in web). The synchronous variant above is
   * left for the dialog rename, whose rename dialog is Swing anyway.
   */
  @Override
  @RequiredUIAccess
  public void substituteElementToRename(@NotNull PsiElement element,
                                        @NotNull Editor editor,
                                        @NotNull Consumer<PsiElement> renameCallback) {
    if (PsiElementRenameHandler.isVetoed(element)) {
      return;
    }

    if (element instanceof PerlSubElement subElement && subElement.isMethod()) {
      suggestSuperMethodAsync(subElement).whenComplete((substituted, error) -> {
        if (substituted != null && substituted.isValid() &&
            PsiElementRenameHandler.canRename(substituted.getProject(), editor, substituted)) {
          renameCallback.accept(substituted);
        }
      });
      return;
    }

    super.substituteElementToRename(element, editor, renameCallback);
  }

  /**
   * consulo: asynchronous {@link #suggestSuperMethod(PerlSubElement)}
   */
  @RequiredUIAccess
  private static @NotNull CompletableFuture<PsiElement> suggestSuperMethodAsync(@NotNull PerlSubElement subBase) {
    PerlSubElement topLevelSuperMethod = PerlSubUtil.getTopmostSuperMethod(subBase);
    String canonicalName = topLevelSuperMethod.getCanonicalName();

    if (topLevelSuperMethod == subBase || canonicalName == null) {
      return CompletableFuture.completedFuture(subBase);
    }

    return MessageBoxBuilder.<PsiElement>create()
      .asQuestion()
      .title(LocalizeValue.of(PerlBundle.message("perl.sub.rename.dialog.title")))
      .text(LocalizeValue.of(PerlBundle.message("perl.sub.rename.dialog.message", canonicalName)))
      .icon(PerlIcons.PERL_LANGUAGE_ICON)
      .button(MessageButtonRole.OK, LocalizeValue.of(PerlBundle.message("perl.sub.rename.dialog.rename.super")), topLevelSuperMethod)
      .asDefaultButton()
      .button(MessageButtonRole.CANCEL, LocalizeValue.of(PerlBundle.message("perl.sub.rename.dialog.rename.current")), subBase)
      .asExitButton()
      .showAsync(subBase.getProject());
  }

  private @NotNull PsiElement suggestSuperMethod(@NotNull PerlSubElement subBase) {
    PerlSubElement topLevelSuperMethod = PerlSubUtil.getTopmostSuperMethod(subBase);
    String canonicalName = topLevelSuperMethod.getCanonicalName();

    if (topLevelSuperMethod == subBase || canonicalName == null) {
      return subBase;
    }

    int dialogResult = Messages.showOkCancelDialog(
      PerlBundle.message("perl.sub.rename.dialog.message", canonicalName),
      PerlBundle.message("perl.sub.rename.dialog.title"),
      PerlBundle.message("perl.sub.rename.dialog.rename.super"),
      PerlBundle.message("perl.sub.rename.dialog.rename.current"),
      PerlIcons.PERL_LANGUAGE_ICON);

    return dialogResult == Messages.OK ? topLevelSuperMethod : subBase;
  }

  /**
   * @return related items for sub declaration or definition. All synonymous definitions, declarations and typeglobs from
   * the {@link GlobalSearchScope#allScope(consulo.project.Project) all scope}
   */
  public static @NotNull Set<PsiElement> computeRelatedItems(@NotNull PerlSubElement subElement) {
    String canonicalName = subElement.getCanonicalName();
    if (StringUtil.isEmpty(canonicalName)) {
      return Collections.emptySet();
    }
    Set<PsiElement> relatedItems = new HashSet<>();
    PerlPackageUtil
      .processCallables(subElement.getProject(), GlobalSearchScope.allScope(subElement.getProject()), canonicalName, relatedItems::add);

    for (PsiElement relatedItem : new ArrayList<>(relatedItems)) {
      if (relatedItem instanceof PerlSubElement perlSubElement && perlSubElement.isMethod()) {
        relatedItems.addAll(PerlSubUtil.collectOverridingSubs(perlSubElement));
      }
      // following is the hack until #1730 is fixed
      if (!(relatedItem instanceof PerlClassAccessorMethod classAccessorMethod) ||
          !relatedItems.contains(classAccessorMethod.getPairedMethod())) {
        relatedItems.add(relatedItem);
      }
    }
    return relatedItems;
  }
}
