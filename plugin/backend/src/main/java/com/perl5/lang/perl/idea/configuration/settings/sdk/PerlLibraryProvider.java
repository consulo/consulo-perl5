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
package com.perl5.lang.perl.idea.configuration.settings.sdk;

import consulo.annotation.component.ExtensionImpl;
import consulo.application.ReadAction;
import consulo.language.psi.stub.IndexableSetContributor;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import com.perl5.lang.perl.idea.project.PerlProjectManager;
import com.perl5.lang.perl.util.PerlRunUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * TODO consulo: upstream is an IntelliJ {@code AdditionalLibraryRootsProvider} (missing in Consulo), providing project interpreter and
 *  external library roots as synthetic libraries (indexed, part of the libraries scope, watched). In Consulo the closest API is
 *  {@link IndexableSetContributor}: the roots are indexed, but they are not part of the libraries/all scope. Interpreter roots are
 *  libraries only through the sdk order entry of the Perl module extension. {@link #getRootsToWatch(Project)} is not used.
 */
@ExtensionImpl
public final class PerlLibraryProvider extends IndexableSetContributor {
  public @NotNull Collection<PerlSdkLibrary> getAdditionalProjectLibraries(@NotNull Project project) {
    return PerlProjectManager.getInstance(project).getProjectLibraries();
  }

  public @NotNull List<VirtualFile> getRootsToWatch(@NotNull Project project) {
    List<VirtualFile> result = new ArrayList<>(PerlProjectManager.getInstance(project).getAllLibraryRoots());
    result.addAll(ReadAction.compute(() -> PerlRunUtil.getBinDirectories(
      project
    ).toList()));
    return result;
  }

  @Override
  public @NotNull Set<VirtualFile> getAdditionalProjectRootsToIndex(@NotNull Project project) {
    if (project.isDefault() || project.isDisposed()) {
      return Collections.emptySet();
    }
    Set<VirtualFile> result = new LinkedHashSet<>();
    for (PerlSdkLibrary library : getAdditionalProjectLibraries(project)) {
      for (VirtualFile root : library.getSourceRoots()) {
        if (root != null && root.isValid()) {
          result.add(root);
        }
      }
    }
    return result;
  }

  @Override
  public @NotNull Set<VirtualFile> getAdditionalRootsToIndex() {
    return Collections.emptySet();
  }
}
