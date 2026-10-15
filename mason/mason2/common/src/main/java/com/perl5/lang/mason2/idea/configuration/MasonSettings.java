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

package com.perl5.lang.mason2.idea.configuration;

import com.perl5.lang.htmlmason.idea.configuration.AbstractMasonSettings;
import consulo.annotation.access.RequiredReadAction;
import consulo.language.file.light.LightVirtualFile;
import consulo.language.psi.PsiElement;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.perl5.mason.module.Mason2ModuleExtension;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Mason2 settings of a module.
 * <p>
 * TODO consulo: upstream is a project-level persistent service. In Consulo the settings are the state of the Mason2 module extension
 *  ({@link Mason2ModuleExtension}), see {@link AbstractMasonSettings}. The settings of a file are the settings of its module if the
 *  extension is enabled there, {@link #DEFAULT} otherwise. Changes are handled by {@code Mason2SettingsChangeListener}.
 */
public final class MasonSettings extends AbstractMasonSettings {
  public static final MasonSettings DEFAULT =
    new MasonSettings(List.of("Base.mp", "Base.mc"), List.of(new VariableDescription("$m", "Mason::Request")));

  private final @NotNull List<String> myAutobaseNames;

  /**
   * @param autobaseNames   autobase components names, order is important: later components may be inherited from early ones
   * @param globalVariables global variables of components, they are copied
   */
  public MasonSettings(@NotNull List<String> autobaseNames, @NotNull List<VariableDescription> globalVariables) {
    super(globalVariables);
    myAutobaseNames = List.copyOf(autobaseNames);
  }

  public @NotNull List<String> getAutobaseNames() {
    return myAutobaseNames;
  }

  @Override
  public boolean equals(Object o) {
    return this == o || super.equals(o) && o instanceof MasonSettings settings && myAutobaseNames.equals(settings.myAutobaseNames);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), myAutobaseNames);
  }

  /**
   * @return settings of the {@code module} if the Mason2 module extension is enabled for it, {@link #DEFAULT} otherwise
   */
  public static @NotNull MasonSettings forModule(@Nullable Module module) {
    Mason2ModuleExtension extension = module == null || module.isDisposed() ? null : module.getExtension(Mason2ModuleExtension.class);
    return extension == null ? DEFAULT : extension.getSettings();
  }

  /**
   * @return settings of the module of the {@code file}, {@link #DEFAULT} if the file is not in a module with the Mason2 module extension
   * enabled
   */
  public static @NotNull MasonSettings forFile(@Nullable Project project, @Nullable VirtualFile file) {
    if (file instanceof LightVirtualFile lightVirtualFile && lightVirtualFile.getOriginalFile() != null) {
      return forFile(project, lightVirtualFile.getOriginalFile());
    }
    if (project == null || project.isDisposed() || file == null) {
      return DEFAULT;
    }
    return forModule(ModuleUtilCore.findModuleForFile(file, project));
  }

  /**
   * @return settings of the module of the {@code element}, {@link #DEFAULT} if it is not in a module with the Mason2 module extension
   * enabled
   */
  @RequiredReadAction
  public static @NotNull MasonSettings forPsiElement(@Nullable PsiElement element) {
    return element == null || !element.isValid() ? DEFAULT : forModule(element.getModule());
  }
}
