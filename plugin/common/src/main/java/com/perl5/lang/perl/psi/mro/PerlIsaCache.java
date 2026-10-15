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

package com.perl5.lang.perl.psi.mro;

import consulo.disposer.Disposable;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import jakarta.inject.Singleton;
import jakarta.inject.Inject;
import consulo.project.Project;
import consulo.language.psi.PsiModificationTrackerListener;
import consulo.util.collection.ContainerUtil;
import consulo.util.interner.Interner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@ServiceAPI(ComponentScope.PROJECT)
@ServiceImpl
@Singleton
public final class PerlIsaCache implements PsiModificationTrackerListener, Disposable {
  private final Map<String, List<String>> myCache = ContainerUtil.createConcurrentWeakMap();
  private static final Interner<List<String>> INTERNER = Interner.createWeakInterner();

  @Inject
  public PerlIsaCache(@NotNull Project project) {
    project.getMessageBus().connect(this).subscribe(PsiModificationTrackerListener.class, this);
  }

  public @Nullable List<String> get(@Nullable String namespaceName) {
    var cachedValue = myCache.get(namespaceName);
    return cachedValue == null ? null : List.copyOf(cachedValue);
  }

  public @NotNull List<String> put(@Nullable String namespaceName, @NotNull List<String> linearIsa) {
    var internedValue = INTERNER.intern(linearIsa);
    myCache.put(namespaceName, internedValue);
    return List.copyOf(internedValue);
  }

  @Override
  public void modificationCountChanged() {
    myCache.clear();
  }

  @Override
  public void dispose() {
  }

  public static @NotNull PerlIsaCache getInstance(@NotNull Project project) {
    return project.getService(PerlIsaCache.class);
  }
}
