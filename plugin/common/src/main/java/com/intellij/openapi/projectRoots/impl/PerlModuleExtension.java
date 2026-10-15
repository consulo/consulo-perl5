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

package com.intellij.openapi.projectRoots.impl;

import consulo.application.WriteAction;
import consulo.content.ContentFolderTypeProvider;
import consulo.content.bundle.SdkType;
import consulo.logging.Logger;
import consulo.module.Module;
import consulo.module.content.layer.ContentEntry;
import consulo.module.content.layer.ContentFolder;
import consulo.module.content.layer.ModifiableRootModel;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.content.layer.extension.ModuleExtensionWithSdkBase;
import consulo.module.content.ModuleRootManager;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import com.perl5.lang.perl.idea.modules.PerlSourceRootType;
import com.perl5.lang.perl.idea.sdk.PerlSdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * TODO consulo: upstream is an IntelliJ {@code ModuleExtension} (available for every module) which persists perl source roots in its own
 *  state ({@code perl5/path} elements, serialized with JPS {@code JpsModuleSourceRootPropertiesSerializer}s).
 *  In Consulo this is the Perl module extension ({@link PerlModuleExtension} + {@code PerlMutableModuleExtension}, registered by
 *  {@code PerlModuleExtensionProvider}) bound to the {@link PerlSdkType perl interpreter}. Perl source roots are stored by the platform
 *  as module content folders of {@link PerlSourceRootType} content folder types, so they are available regardless of the extension
 *  being enabled. Roots are allowed only inside module content entries. Writability is asserted by the platform content entries,
 *  {@code getModifiableModel(boolean)}/{@code commit()} of the IntelliJ API are replaced with the platform modifiable root model.
 */
public class PerlModuleExtension extends ModuleExtensionWithSdkBase<PerlModuleExtension> {
  private static final Logger LOG = Logger.getInstance(PerlModuleExtension.class);

  public PerlModuleExtension(@NotNull String id, @NotNull ModuleRootLayer rootLayer) {
    super(id, rootLayer);
  }

  @Override
  public @NotNull Class<? extends SdkType> getSdkTypeClass() {
    return PerlSdkType.class;
  }

  /**
   * @return true iff perl roots of this model differ from the committed ones
   */
  public boolean isChanged() {
    PerlModuleExtension original = getInstance(getModule());
    return original != this && !getRoots().equals(original.getRoots());
  }

  public synchronized void setRoot(@NotNull VirtualFile root, @NotNull PerlSourceRootType type) {
    ContentEntry contentEntry = findContentEntry(root);
    if (contentEntry == null) {
      LOG.warn("Unable to set root " + root + ", it's not under content roots of " + getModule());
      return;
    }
    for (ContentFolder folder : contentEntry.getFolders(it -> true)) {
      if (root.equals(folder.getFile())) {
        // TODO consulo: in IntelliJ perl roots are stored separately, here a directory can have a single content folder type
        contentEntry.removeFolder(folder);
      }
    }
    ContentFolderTypeProvider registeredType = ContentFolderTypeProvider.byId(type.getId());
    contentEntry.addFolder(root, registeredType instanceof PerlSourceRootType ? registeredType : type);
  }

  public synchronized void removeRoot(@NotNull VirtualFile root) {
    for (ContentEntry contentEntry : getModuleRootLayer().getContentEntries()) {
      for (ContentFolder folder : contentEntry.getFolders(PerlModuleExtension::isPerlRootType)) {
        if (root.equals(folder.getFile())) {
          contentEntry.removeFolder(folder);
        }
      }
    }
  }

  public Map<VirtualFile, PerlSourceRootType> getRoots() {
    Map<VirtualFile, PerlSourceRootType> roots = new LinkedHashMap<>();
    for (ContentEntry contentEntry : getModuleRootLayer().getContentEntries()) {
      for (ContentFolder folder : contentEntry.getFolders(PerlModuleExtension::isPerlRootType)) {
        VirtualFile file = folder.getFile();
        if (file != null && file.isValid() && file.isDirectory()) {
          roots.put(file, (PerlSourceRootType)folder.getType());
        }
      }
    }
    return Collections.unmodifiableMap(roots);
  }

  public synchronized List<VirtualFile> getRootsByType(@NotNull PerlSourceRootType type) {
    return getRoots().entrySet().stream()
      .filter(entry -> type.equals(entry.getValue()))
      .map(Map.Entry::getKey)
      .collect(Collectors.toList());
  }

  public @Nullable PerlSourceRootType getRootType(@NotNull VirtualFile virtualFile) {
    return getRoots().get(virtualFile);
  }

  private @Nullable ContentEntry findContentEntry(@NotNull VirtualFile root) {
    for (ContentEntry contentEntry : getModuleRootLayer().getContentEntries()) {
      VirtualFile contentRoot = contentEntry.getFile();
      if (contentRoot != null && VirtualFileUtil.isAncestor(contentRoot, root, false)) {
        return contentEntry;
      }
    }
    return null;
  }

  private static boolean isPerlRootType(@NotNull ContentFolderTypeProvider typeProvider) {
    return typeProvider instanceof PerlSourceRootType;
  }

  /**
   * @return perl module extension of the module, the extension is returned even if it's not enabled for the module
   */
  public static PerlModuleExtension getInstance(@NotNull Module module) {
    return Objects.requireNonNull(ModuleRootManager.getInstance(module).getExtensionWithoutCheck(PerlModuleExtension.class));
  }

  /**
   * Modifies {@link PerlModuleExtension} for the {@code module} using {@code mutator}. Incapsulates proper logic of working with modifiable
   * root model.
   */
  public static void modify(@NotNull Module module, @NotNull Consumer<? super PerlModuleExtension> mutator) {
    ModifiableRootModel modifiableModel = ModuleRootManager.getInstance(module).getModifiableModel();
    PerlModuleExtension moduleExtensionModifiableModel =
      Objects.requireNonNull(modifiableModel.getExtensionWithoutCheck(PerlModuleExtension.class));
    try {
      mutator.accept(moduleExtensionModifiableModel);
    }
    catch (Throwable e) {
      LOG.error(e);
      modifiableModel.dispose();
    }
    finally {
      if (!modifiableModel.isDisposed() && moduleExtensionModifiableModel.isChanged()) {
        // consulo: commit of a changed root model fires the roots change itself (the extension is a part of the root model)
        WriteAction.run(
          () -> modifiableModel.commit()
        );
      }
      else if (!modifiableModel.isDisposed()) {
        modifiableModel.dispose();
      }
    }
  }
}
