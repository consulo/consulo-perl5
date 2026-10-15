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

package com.perl5.lang.perl.idea.modules;

import consulo.content.ContentFolderTypeProvider;
import consulo.localize.LocalizeValue;
import consulo.module.Module;
import consulo.module.ModuleManager;
import consulo.perl5.core.module.PerlFrameworkContentFolderSupportPatcher;
import consulo.project.Project;
import com.intellij.openapi.projectRoots.impl.PerlModuleExtension;
import com.perl5.lang.perl.idea.configuration.settings.sdk.PerlSourceRootEditHandler;
import consulo.ui.color.ColorValue;
import consulo.ui.ex.awtUnsafe.TargetAWT;
import consulo.ui.image.Image;
import consulo.virtualFileSystem.VirtualFile;
import consulo.language.util.IncorrectOperationException;
import consulo.util.collection.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO consulo: upstream is a JPS {@code JpsModuleSourceRootType<JpsDummyElement>} with roots stored by {@link PerlModuleExtension}.
 *  In Consulo perl source root types are content folder types: roots are stored as module content folders and subclasses must be
 *  registered as {@code ContentFolderTypeProvider} extensions ({@code @ExtensionImpl}). The content folder id is the
 *  {@link #getSerializationKey() serialization key}, presentation is taken from the {@link PerlSourceRootEditHandler}.
 */
public abstract class PerlSourceRootType extends ContentFolderTypeProvider {
  protected PerlSourceRootType() {
    // real id is provided by #getId(), constructor is invoked before subclass is initialized
    super("PERL_SOURCE_ROOT");
  }

  protected abstract PerlSourceRootType getRootType();

  /**
   * @return key for xml file on serialization
   */
  public abstract @NotNull String getSerializationKey();

  /**
   * consulo: roots of a Perl framework (e.g. Template Toolkit templates roots) are collected only from the modules with the framework
   * module extension enabled, see {@link PerlFrameworkContentFolderSupportPatcher#isRootTypeEnabled(Module, PerlSourceRootType)}
   */
  public final @NotNull List<VirtualFile> getRoots(@NotNull Project project) {
    List<VirtualFile> result = new ArrayList<>();
    for (Module module : ModuleManager.getInstance(project).getModules()) {
      if (PerlFrameworkContentFolderSupportPatcher.isRootTypeEnabled(module, getRootType())) {
        result.addAll(getRoots(module));
      }
    }
    return ContainerUtil.filter(result, VirtualFile::isValid);
  }

  public final @NotNull List<VirtualFile> getRoots(@NotNull Module module) {
    return PerlModuleExtension.getInstance(module).getRootsByType(getRootType());
  }

  public final @NotNull PerlSourceRootEditHandler getEditHandler() {
    PerlSourceRootEditHandler handler = PerlSourceRootEditHandler.getEditHandler(getRootType());
    if (handler == null) {
      throw new IncorrectOperationException("Couldn't find handler for " + getRootType());
    }
    return handler;
  }

  @Override
  public @NotNull String getId() {
    return getSerializationKey();
  }

  @Override
  public @NotNull Image getIcon() {
    return getEditHandler().getRootIcon();
  }

  @Override
  public @NotNull Image getChildDirectoryIcon() {
    Image folderUnderRootIcon = getEditHandler().getFolderUnderRootIcon();
    return folderUnderRootIcon == null ? super.getChildDirectoryIcon() : folderUnderRootIcon;
  }

  @Override
  public @NotNull LocalizeValue getName() {
    return LocalizeValue.of(getEditHandler().getFullRootTypeName());
  }

  @Override
  public @NotNull ColorValue getGroupColor() {
    return TargetAWT.from(getEditHandler().getRootsGroupColor());
  }

  @Override
  public boolean equals(@Nullable Object o) {
    return this == o || o instanceof PerlSourceRootType rootType && getId().equals(rootType.getId());
  }

  @Override
  public int hashCode() {
    return getId().hashCode();
  }

  @Override
  public String toString() {
    return getId();
  }
}
