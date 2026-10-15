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
package com.perl5.lang.perl.cpanminus.cpanfile;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.psi.PsiFile;
import com.perl5.lang.perl.extensions.imports.PerlImportsProvider;
import com.perl5.lang.perl.extensions.packageprocessor.PerlExportDescriptor;
import com.perl5.lang.perl.psi.PerlNamespaceDefinitionElement;
import consulo.util.lang.lazy.LazyValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

@ExtensionImpl
public class PerlCpanfileImportProvider implements PerlImportsProvider {
  private static final String NAMESPACE = "Module::CPANfile::Environment";

  private final Supplier<List<PerlExportDescriptor>> myDescriptors = LazyValue.atomicNotNull(
    () -> List.of(
        "on",
        "requires",
        "recommends",
        "suggests",
        "conflicts",
        "feature",
        "osname",
        "mirror",
        "configure_requires",
        "build_requires",
        "test_requires",
        "author_requires"
      ).stream()
      .map(name -> PerlExportDescriptor.create(NAMESPACE, name))
      .toList()
  );

  @Override
  public @NotNull List<PerlExportDescriptor> getExportDescriptors(@NotNull PerlNamespaceDefinitionElement namespaceElement) {
    return myDescriptors.get();
  }

  @Override
  public boolean isApplicable(@Nullable PerlNamespaceDefinitionElement namespaceDefinitionElement) {
    return namespaceDefinitionElement instanceof PsiFile psiFile &&
           psiFile.getOriginalFile().getFileType() == PerlFileTypeCpanfile.INSTANCE;
  }
}
