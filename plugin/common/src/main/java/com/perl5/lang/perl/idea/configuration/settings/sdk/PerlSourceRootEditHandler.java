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

package com.perl5.lang.perl.idea.configuration.settings.sdk;

import com.perl5.lang.perl.idea.modules.PerlSourceRootType;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.application.Application;
import consulo.ui.ex.action.CustomShortcutSet;
import consulo.ui.ex.JBColor;
import consulo.ui.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * TODO consulo: upstream extends IntelliJ {@code ModuleSourceRootEditHandler<JpsDummyElement>} registered as
 *  {@code projectStructure.sourceRootEditHandler}. Consulo has no such API (presentation of content folders is provided by
 *  {@code ContentFolderTypeProvider}), so the used part of the base class is inlined here and handlers are Consulo extensions.
 *  {@link PerlSourceRootType} delegates its content folder presentation to the handler.
 */
@ExtensionAPI(ComponentScope.APPLICATION)
public abstract class PerlSourceRootEditHandler {
  private final @NotNull PerlSourceRootType myRootType;

  public PerlSourceRootEditHandler(@NotNull PerlSourceRootType rootType) {
    myRootType = rootType;
  }

  public static @Nullable PerlSourceRootEditHandler getEditHandler(@NotNull PerlSourceRootType rootType) {
    return Application.get().getExtensionPoint(PerlSourceRootEditHandler.class).findFirstSafe(it -> rootType.equals(it.getRootType()));
  }

  public final @NotNull PerlSourceRootType getRootType() {
    return myRootType;
  }

  public abstract @NotNull String getRootTypeName();

  public @NotNull String getFullRootTypeName() {
    // upstream: ProjectBundle.message("module.paths.root.node", getRootTypeName())
    return getRootTypeName() + " Root";
  }

  public abstract @NotNull Image getRootIcon();

  public @Nullable Image getFolderUnderRootIcon() {
    return null;
  }

  public @Nullable CustomShortcutSet getMarkRootShortcutSet() {
    return null;
  }

  public abstract @NotNull String getRootsGroupTitle();

  public @NotNull Color getRootsGroupColor() {
    return new JBColor(new Color(76, 94, 133), new Color(76, 94, 133));
  }

  public @NotNull String getMarkRootButtonText() {
    return getRootTypeName();
  }

  public abstract @NotNull String getUnmarkRootButtonText();
}
