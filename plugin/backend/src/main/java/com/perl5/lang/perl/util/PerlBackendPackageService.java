/*
 * Copyright 2015-2026 Alexandr Evstigneev
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

package com.perl5.lang.perl.util;

import consulo.annotation.component.ServiceImpl;
import jakarta.inject.Singleton;
import consulo.project.Project;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.application.util.function.Processor;
import com.perl5.lang.perl.fileTypes.PerlFileTypePackage;
import com.perl5.lang.perl.psi.PerlFile;
import com.perl5.lang.perl.psi.PerlNamespaceDefinitionElement;
import com.perl5.lang.perl.psi.impl.PerlUseStatementElement;
import com.perl5.lang.pod.parser.psi.PodFile;
import com.perl5.lang.pod.parser.psi.util.PodFileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

import static com.perl5.lang.perl.util.PerlCorePackages.CORE_PACKAGES_DEPRECATED;


@ServiceImpl
@Singleton
public final class PerlBackendPackageService implements PerlPackageService {
  @Override
  public @Nullable String getFilePackageName(@NotNull PerlFile perlFile) {
    VirtualFile containingFile = perlFile.getVirtualFile();
    if (containingFile == null) {
      return null;
    }
    if (containingFile.getFileType() != PerlFileTypePackage.INSTANCE) {
      return null;
    }
    var innermostSourceRoot = PerlPackageUtil.getClosestIncRoot(perlFile.getProject(), containingFile);
    if (innermostSourceRoot == null) {
      return null;
    }
    return PerlPackageUtilCore.getPackageNameByPath(VirtualFileUtil.getRelativePath(containingFile, innermostSourceRoot));
  }

  @Override
  public void addExports(
    @NotNull PerlUseStatementElement useStatement,
    @NotNull Set<? super String> export,
    @NotNull Set<? super String> exportOk
  ) {
    var packageName = useStatement.getPackageName();
    if (packageName == null) {
      return;
    }

    if (StringUtil.isEmpty(packageName)) {
      return;
    }

    // fixme handle tags
    var scope = GlobalSearchScope.allScope(useStatement.getProject());
    for (PerlNamespaceDefinitionElement namespaceDefinition : PerlNamespaceUtil
      .getNamespaceDefinitions(useStatement.getProject(), scope, packageName)) {
      var defaultExports = namespaceDefinition.getEXPORT();
      export.addAll(defaultExports);
      exportOk.addAll(defaultExports);
      exportOk.addAll(namespaceDefinition.getEXPORT_OK());
    }
  }

  @Override
  public @Nullable String getFilePackageName(@NotNull PodFile podFile) {
    return PodFileUtil.getPackageName(podFile);
  }

  @Override
  public boolean isDeprecated(@NotNull Project project, @NotNull GlobalSearchScope searchScope, @NotNull String packageCanonicalName) {
    return CORE_PACKAGES_DEPRECATED.contains(packageCanonicalName) ||
           !PerlNamespaceUtil.processNamespaces(
             packageCanonicalName,
             project,
             searchScope,
             (Processor<PerlNamespaceDefinitionElement>)it -> !it.isDeprecated());
  }
}
