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

package com.perl5.lang.perl.cpanminus.runAnything;

import consulo.annotation.component.ExtensionImpl;
import consulo.application.Application;
import consulo.ide.runAnything.RunAnythingProvider;
import consulo.ide.runAnything.RunAnythingHelpGroup;
import consulo.ide.runAnything.RunAnythingItem;
import consulo.dataContext.DataContext;
import consulo.util.collection.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@ExtensionImpl
public class PerlCpanminusRunAnythingHelpGroup extends RunAnythingHelpGroup<PerlCpanminusRunAnythingProvider> {

  public PerlCpanminusRunAnythingHelpGroup() {
    super(Objects.requireNonNull(Application.get().getExtensionPoint(RunAnythingProvider.class).findExtension(PerlCpanminusRunAnythingProvider.class)).getHelpGroupTitle(),
          Collections.emptyList());
  }

  @Override
  protected int getMaxInitialItems() {
    return 10;
  }

  @Override
  public @NotNull Collection<RunAnythingItem> getGroupItems(@NotNull DataContext dataContext, @NotNull String pattern) {
    return ContainerUtil.filter(
      Objects.requireNonNull(Application.get().getExtensionPoint(RunAnythingProvider.class).findExtension(PerlCpanminusRunAnythingProvider.class)).getHelpItems(),
      Objects::nonNull);
  }
}
