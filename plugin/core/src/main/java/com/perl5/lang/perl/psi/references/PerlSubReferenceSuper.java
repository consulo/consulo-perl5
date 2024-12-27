/*
 * Copyright 2015-2022 Alexandr Evstigneev
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

package com.perl5.lang.perl.psi.references;

import com.perl5.lang.perl.util.PerlPackageUtil;
import consulo.language.psi.PsiElement;
import consulo.language.psi.ResolveResult;
import consulo.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.perl5.lang.perl.psi.mro.PerlMro.collectCallables;


public class PerlSubReferenceSuper extends PerlSubReferenceSimple {
  public PerlSubReferenceSuper(PsiElement psiElement) {
    super(psiElement);
  }


  @Override
  protected @NotNull ResolveResult[] resolveInner(boolean incompleteCode) {
    // fixme not dry with simple resolver, need some generics fix
    PsiElement element = getElement();

    String packageName = PerlPackageUtil.getContextNamespaceName(element);
    String subName = element.getNode().getText();
    Project project = element.getProject();

    List<ResolveResult> result = getResolveResults(collectCallables(
      project, element.getResolveScope(),
      packageName,
      subName,
      true
    ));

    return result.toArray(ResolveResult.EMPTY_ARRAY);
  }
}
