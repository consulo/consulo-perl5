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

package com.perl5.lang.perl.psi;

import com.perl5.lang.perl.extensions.PerlCodeGenerator;
import com.perl5.lang.perl.psi.impl.PerlBuiltInNamespaceDefinition;
import com.perl5.lang.perl.psi.impl.PerlImplicitNamespaceDefinition;
import com.perl5.lang.perl.psi.properties.PerlDieScope;
import com.perl5.lang.perl.psi.properties.PerlLabelScope;
import com.perl5.lang.perl.psi.properties.PerlLexicalScope;
import com.perl5.lang.perl.psi.references.PerlBuiltInNamespacesService;
import com.perl5.lang.perl.util.PerlPackageUtil;
import com.perl5.lang.pod.parser.psi.PodLinkTarget;
import consulo.application.util.CachedValueProvider;
import consulo.application.util.CachedValuesManager;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.util.LanguageCachedValueUtil;
import consulo.navigation.ItemPresentation;
import consulo.util.lang.StringUtil;
import org.jetbrains.annotations.Nullable;


public interface PerlFile
  extends PsiFile, PerlLexicalScope, PerlNamespaceDefinitionElement, PerlLabelScope, ItemPresentation, PodLinkTarget, PerlDieScope,
          PerlControlFlowOwner {
  /**
   * @return namespace definition psi element from built-in service or synthetic one
   */
  default @Nullable PerlNamespaceDefinitionElement getNamespaceDefinitionElement() {
    String packageName = getNamespaceName();
    if (StringUtil.isEmpty(packageName)) {
      return null;
    }
    PerlBuiltInNamespaceDefinition namespaceDefinition =
      PerlBuiltInNamespacesService.getInstance(getProject()).getNamespaceDefinition(packageName);
    if (namespaceDefinition != null) {
      return namespaceDefinition;
    }
    return LanguageCachedValueUtil.getCachedValue(this, () -> CachedValueProvider.Result.create(
      new PerlImplicitNamespaceDefinition(getManager(), PerlPackageUtil.getCanonicalNamespaceName(packageName), this),
      this));
  }

  /**
   * Returns generator for overriding elements
   *
   * @return override generator
   */
  PerlCodeGenerator getCodeGenerator();

  /**
   * Returns perl content with templating injections replaced with spaces
   *
   * @return bytes for external analysis/formatting
   */
  byte[] getPerlContentInBytes();

  /**
   * Overrides file context; if null - using default context resoving implementation
   *
   * @param fileContext new file context
   */
  void setFileContext(@Nullable PsiElement fileContext);
}
