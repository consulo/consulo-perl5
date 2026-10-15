/*
 * Copyright 2015-2021 Alexandr Evstigneev
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

import consulo.language.editor.hierarchy.HierarchyKind;
import consulo.language.editor.hierarchy.HierarchyModel;
import consulo.language.editor.hierarchy.HierarchyProvider;
import consulo.language.editor.hierarchy.StandardHierarchyKinds;
import consulo.project.Project;
import consulo.language.Language;
import consulo.language.editor.CommonDataKeys;
import consulo.dataContext.DataContext;
import consulo.codeEditor.Editor;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.util.PsiTreeUtil;
import com.perl5.lang.perl.PerlLanguage;
import com.perl5.lang.perl.psi.PerlNamespaceDefinitionElement;
import com.perl5.lang.perl.psi.PerlNamespaceDefinitionWithIdentifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class PerlTypeHierarchyProvider implements HierarchyProvider<PsiElement> {
  @Override
  public HierarchyKind getKind() {
    return StandardHierarchyKinds.TYPE;
  }

  @Override
  public @Nullable PsiElement getTarget(@NotNull DataContext dataContext) {
    PsiElement element = dataContext.getData(CommonDataKeys.PSI_ELEMENT);
    if (element == null) {
      final Editor editor = dataContext.getData(CommonDataKeys.EDITOR);
      final PsiFile file = dataContext.getData(CommonDataKeys.PSI_FILE);

      if (editor != null && file != null) {
        element = file.getViewProvider().findElementAt(editor.getCaretModel().getOffset(), getLanguage());
      }
    }

    return adjustTargetElement(element);
  }

  protected PsiElement adjustTargetElement(PsiElement element) {
    if (element != null && !(element instanceof PerlNamespaceDefinitionWithIdentifier)) {
      return PsiTreeUtil.getParentOfType(element, PerlNamespaceDefinitionElement.class);
    }

    return element;
  }

  // consulo: IntelliJ createHierarchyBrowser() -> Consulo createModel(), the platform builds the browser from the model
  @Override
  public @NotNull HierarchyModel<PsiElement> createModel(@NotNull Project project, @NotNull PsiElement target) {
    return new PerlHierarchyBrowser(target);
  }

  // consulo: IntelliJ browserActivated() switched the browser to the supertypes view, see PerlHierarchyBrowser#getDefaultViewType()

  @Override
  public Language getLanguage() {
    return PerlLanguage.INSTANCE;
  }
}
