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

package com.perl5.lang.perl.idea.gotosearch;

import consulo.application.util.function.Processor;
import consulo.content.scope.SearchScope;
import consulo.ide.navigation.ChooseByNameContributorEx;
import consulo.language.psi.search.FindSymbolParameters;
import consulo.language.psi.stub.IdFilter;
import consulo.navigation.NavigationItem;
import consulo.project.Project;
import consulo.project.content.scope.ProjectAwareSearchScope;
import consulo.util.collection.ArrayUtil;
import consulo.util.lang.function.Condition;
import consulo.util.collection.ContainerUtil;
import com.perl5.lang.perl.psi.impl.PerlImplicitElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

// consulo: Consulo goto contributors (GotoClassOrTypeContributor, GotoSymbolContributor) are ChooseByNameContributorEx
abstract class PerlChooseByNameContributor implements ChooseByNameContributorEx {
  private static final Condition<NavigationItem> FILTER = it -> !(it instanceof PerlImplicitElement);

  @Override
  public final String @NotNull [] getNames(Project project, boolean includeNonProjectItems) {
    return ArrayUtil.toStringArray(getNamesCollection(project, includeNonProjectItems));
  }

  // consulo: adapter to the upstream getNames(Project, boolean)
  @Override
  public void processNames(@NotNull Processor<String> processor, @NotNull SearchScope scope, @Nullable IdFilter filter) {
    ProjectAwareSearchScope projectAwareScope = (ProjectAwareSearchScope)scope;
    for (String name : getNames(projectAwareScope.getProject(), projectAwareScope.isSearchInLibraries())) {
      if (!processor.process(name)) {
        return;
      }
    }
  }

  // consulo: adapter to the upstream getItemsByName(String, String, Project, boolean)
  @Override
  public void processElementsWithName(@NotNull String name,
                                      @NotNull Processor<NavigationItem> processor,
                                      @NotNull FindSymbolParameters parameters) {
    for (NavigationItem item : getItemsByName(name, parameters.getLocalPatternName(), parameters.getProject(),
                                              parameters.isSearchInLibraries())) {
      if (!processor.process(item)) {
        return;
      }
    }
  }

  protected abstract @NotNull Collection<String> getNamesCollection(Project project,
                                                                    @SuppressWarnings("unused") boolean includeNonProjectItems);

  @Override
  public final NavigationItem @NotNull [] getItemsByName(String name, String pattern, Project project, boolean includeNonProjectItems) {
    return ContainerUtil.filter(getItemsCollectionByName(name, project, includeNonProjectItems), FILTER)
      .toArray(NavigationItem.EMPTY_NAVIGATION_ITEM_ARRAY);
  }

  protected abstract @NotNull Collection<? extends NavigationItem> getItemsCollectionByName(String name,
                                                                                            Project project,
                                                                                            boolean includeNonProjectItems);
}
