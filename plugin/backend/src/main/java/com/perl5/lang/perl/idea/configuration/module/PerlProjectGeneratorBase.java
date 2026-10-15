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

package com.perl5.lang.perl.idea.configuration.module;

import consulo.module.Module;
import consulo.project.Project;
import consulo.content.bundle.Sdk;
import consulo.virtualFileSystem.VirtualFile;
import com.perl5.lang.perl.idea.project.PerlProjectManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO consulo: Consulo has no DirectoryProjectGenerator API (IntelliJ "New Project" generators of small IDEs; the upstream
//  <directoryProjectGenerator> registration is commented out too), so this is a plain class; the methods are kept as is
public abstract class PerlProjectGeneratorBase<Settings extends PerlProjectGenerationSettings> {
  public final void generateProject(@Nullable Project project,
                                    @NotNull VirtualFile baseDir,
                                    @NotNull PerlProjectGenerationSettings settings,
                                    @NotNull Module module) {
    configureModule(module, settings);
  }

  public abstract @NotNull PerlProjectGeneratorPeerBase<Settings> createPeer();

  public void configureModule(@NotNull Module module, @NotNull PerlProjectGenerationSettings settings) {
    Sdk sdk = settings.getSdk();
    if (sdk != null) {
      PerlProjectManager.getInstance(module.getProject()).setProjectSdk(sdk);
    }
  }
}
