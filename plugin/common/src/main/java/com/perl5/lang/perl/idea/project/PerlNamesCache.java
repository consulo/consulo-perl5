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

package com.perl5.lang.perl.idea.project;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.disposer.Disposable;
import consulo.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.TestOnly;

import java.util.Set;

@ServiceAPI(ComponentScope.PROJECT)
public interface PerlNamesCache extends Disposable {
  @NotNull
  Set<String> getSubsNamesSet();

  @NotNull
  Set<String> getNamespacesNamesSet();

  // TODO consulo: upstream signature is `forceCacheUpdate(callback: () -> Unit = { })`, seen from Java as
  //  forceCacheUpdate(kotlin.jvm.functions.Function0<kotlin.Unit>); implementations and callers must switch to Runnable
  void forceCacheUpdate(@NotNull Runnable callback);

  boolean isDisposed();

  @TestOnly
  void cleanCache();

  static @NotNull PerlNamesCache getInstance(@NotNull Project project) {
    return project.getService(PerlNamesCache.class);
  }
}
