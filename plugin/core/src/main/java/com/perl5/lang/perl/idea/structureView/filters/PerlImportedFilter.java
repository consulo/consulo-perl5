/*
 * Copyright 2015-2020 Alexandr Evstigneev
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

package com.perl5.lang.perl.idea.structureView.filters;

import com.perl5.lang.perl.idea.structureView.elements.PerlStructureViewElement;
import consulo.fileEditor.structureView.tree.ActionPresentation;
import consulo.fileEditor.structureView.tree.ActionPresentationData;
import consulo.platform.base.icon.PlatformIconGroup;
import org.jetbrains.annotations.NotNull;


public class PerlImportedFilter extends PerlFilter {
  public static final PerlImportedFilter INSTANCE = new PerlImportedFilter();
  private static final String ID = "SHOW_IMPORTED";

  @Override
  protected boolean isMyElement(@NotNull PerlStructureViewElement treeElement) {
    return treeElement.isImported();
  }

  @Override
  public @NotNull ActionPresentation getPresentation() {
    return new ActionPresentationData("Show imported", null, PlatformIconGroup.actionsShowimportstatements());
  }

  @Override
  public @NotNull String getName() {
    return ID;
  }
}
