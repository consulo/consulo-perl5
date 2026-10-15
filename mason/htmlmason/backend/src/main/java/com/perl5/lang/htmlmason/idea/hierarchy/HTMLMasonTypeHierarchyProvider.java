/*
 * Copyright 2015-2023 Alexandr Evstigneev
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

package com.perl5.lang.htmlmason.idea.hierarchy;

import consulo.language.editor.hierarchy.HierarchyModel;
import consulo.language.Language;
import consulo.project.Project;
import consulo.language.psi.PsiElement;
import com.perl5.lang.htmlmason.HTMLMasonLanguage;
import com.perl5.lang.htmlmason.parser.psi.impl.HTMLMasonFileImpl;
import com.perl5.lang.perl.idea.hierarchy.namespace.PerlTypeHierarchyProvider;
import org.jetbrains.annotations.NotNull;


public class HTMLMasonTypeHierarchyProvider extends PerlTypeHierarchyProvider {
  @Override
  public Language getLanguage() {
    return HTMLMasonLanguage.INSTANCE;
  }

  @Override
  protected PsiElement adjustTargetElement(PsiElement element) {
    if (element != null && !(element instanceof HTMLMasonFileImpl)) {
      return element.getContainingFile();
    }

    return element;
  }

  // consulo: IntelliJ createHierarchyBrowser() -> Consulo createModel(), the platform builds the browser from the model
  @Override
  public @NotNull HierarchyModel<PsiElement> createModel(@NotNull Project project, @NotNull PsiElement target) {
    return new HTMLMasonHierarchyBrowser(target);
  }

  // consulo: IntelliJ browserActivated() switched the browser to the type hierarchy view, see HTMLMasonHierarchyBrowser#getDefaultViewType()
}
