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

package com.perl5.lang.perl.cpanminus.action;

import consulo.ui.ex.action.AnActionEvent;
import consulo.logging.Logger;
import consulo.project.Project;
import consulo.content.bundle.Sdk;
import consulo.virtualFileSystem.VirtualFile;
import com.perl5.PerlIcons;
import com.perl5.lang.perl.cpanminus.PerlCpanminusBundle;
import com.perl5.lang.perl.cpanminus.adapter.CpanminusAdapter;
import com.perl5.lang.perl.cpanminus.cpanfile.PerlFileTypeCpanfile;
import com.perl5.lang.perl.idea.actions.PerlActionBase;
import com.perl5.lang.perl.idea.project.PerlProjectManager;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class PerlInstallDependenciesWithCpanMinusAction extends PerlActionBase {
  private static final Logger log = Logger.getInstance(PerlInstallDependenciesWithCpanMinusAction.class);

  public PerlInstallDependenciesWithCpanMinusAction() {
    super(PerlCpanminusBundle.message("install.dependencies.with.cpanminus"), PerlIcons.METACPAN);
  }

  @Override
  protected boolean isEnabled(@NotNull AnActionEvent event) {
    if (!super.isEnabled(event)) {
      return false;
    }

    VirtualFile virtualFile = getVirtualFile(event);
    return virtualFile != null && PerlFileTypeCpanfile.INSTANCE.equals(virtualFile.getFileType());
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent event) {
    VirtualFile eventFile = getVirtualFile(event);
    if (eventFile == null || !PerlFileTypeCpanfile.INSTANCE.equals(eventFile.getFileType())) {
      log.warn("No file or wrong file type " + eventFile);
      return;
    }

    // consulo: no AnAction#getEventProject, project is taken from the event data
    Project project = event.getData(Project.KEY);
    Sdk perlSdk = PerlProjectManager.getSdk(project);
    if (perlSdk == null) {
      log.warn("No perl sdk for the project");
      return;
    }

    CpanminusAdapter.installModules(perlSdk, project, List.of("--installdeps", Objects.requireNonNull(eventFile.getParent()).getPath()), null, true);
  }
}
