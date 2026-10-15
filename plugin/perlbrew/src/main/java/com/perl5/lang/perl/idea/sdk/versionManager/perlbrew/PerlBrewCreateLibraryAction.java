/*
 * Copyright 2015-2022 Alexandr Evstigneev
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

package com.perl5.lang.perl.idea.sdk.versionManager.perlbrew;

import consulo.process.util.ProcessOutput;
import consulo.ui.ex.action.AnActionEvent;
import consulo.application.ApplicationManager;
import consulo.application.progress.ProgressIndicator;
import consulo.application.progress.Task;
import consulo.application.dumb.DumbAware;
import consulo.content.bundle.Sdk;
import consulo.project.Project;
import consulo.ui.InputBoxBuilder;
import consulo.ui.MessageBoxes;
import consulo.localize.LocalizeValue;
import consulo.util.lang.StringUtil;
import consulo.util.lang.ObjectUtil;
import com.perl5.PerlBundle;
import com.perl5.PerlIcons;
import com.perl5.lang.perl.idea.project.PerlProjectManager;
import com.perl5.lang.perl.idea.sdk.host.PerlHostData;
import com.perl5.lang.perl.idea.sdk.versionManager.PerlVersionManagerData;
import consulo.perl5.core.ui.PerlDialogUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PerlBrewCreateLibraryAction extends PerlBrewActionBase implements DumbAware {

  public PerlBrewCreateLibraryAction() {
    super(PerlBundle.message("perl.vm.perlbrew.create.lib"));
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    Sdk perlSdk = PerlProjectManager.getSdk(e);
    PerlBrewData perlBrewData = ObjectUtil.tryCast(PerlVersionManagerData.from(perlSdk), PerlBrewData.class);
    if (perlBrewData == null) {
      return;
    }

    PerlHostData<?, ?> hostData = PerlHostData.from(perlSdk);
    if (hostData == null) {
      return;
    }

    final String perlVersionString = perlBrewData.getPerlVersionString();
    // consulo: no AnAction#getEventProject/AnActionEvent#getProject, project is taken from the event data
    Project project = e.getData(Project.KEY);
    // consulo: Messages are Swing dialogs (not available in web), the unified input box is asynchronous, the library is
    // created when the name arrives
    InputBoxBuilder.text()
      .title(LocalizeValue.of(PerlBundle.message("perl.vm.perlbrew.create.lib.title", perlVersionString)))
      .text(LocalizeValue.of(PerlBundle.message("perl.vm.perlbrew.create.lib.message")))
      .showAsync(project)
      .whenComplete((libraryName, error) -> createLibrary(project, perlBrewData, hostData, perlVersionString, libraryName));
  }

  private static void createLibrary(@Nullable Project project,
                                    @NotNull PerlBrewData perlBrewData,
                                    @NotNull PerlHostData<?, ?> hostData,
                                    @NotNull String perlVersionString,
                                    @Nullable String libraryName) {
    if (StringUtil.isEmpty(libraryName)) {
      return;
    }
    new Task.Backgroundable(project, PerlBundle.message("perl.vm.perlbrew.create.lib.progress", libraryName,
                                                               perlVersionString)) {
      @Override
      public void run(@NotNull ProgressIndicator indicator) {
        PerlBrewAdapter perlBrewAdapter = new PerlBrewAdapter(perlBrewData.getVersionManagerPath(), hostData);
        ProcessOutput processOutput = perlBrewAdapter.createLibrary(perlVersionString, libraryName);
        if (processOutput == null) {
          return;
        }

        String distirbutionId = perlVersionString + "@" + libraryName;
        if (processOutput.getExitCode() == 0) {
          doCreateSdk(perlBrewAdapter, distirbutionId);
        }

        if (StringUtil.contains(processOutput.getStderr(), "is already there")) {
          // consulo: the unified message box is asynchronous, this background task waits for the answer
          PerlDialogUtil.showAndThen(
            () -> MessageBoxes.yesNo()
              .title(LocalizeValue.of(PerlBundle.message("perl.vm.perlbrew.title")))
              .text(LocalizeValue.of(PerlBundle.message("perl.vm.perlbrew.library.exists", distirbutionId)))
              .icon(PerlIcons.PERLBREW_ICON)
              .showAsync(),
            useExisting -> {
              if (Boolean.TRUE.equals(useExisting)) {
                doCreateSdk(perlBrewAdapter, distirbutionId);
              }
            });
        }
      }

      // fixme we could avoid creating duplicated sdks here
      // consulo: Task#myProject is a ComponentManager, the captured project is used instead
      private void doCreateSdk(@NotNull PerlBrewAdapter perlBrewAdapter, @NotNull String distirbutionId) {
        PerlBrewHandler.getInstance().createInterpreter(
          distirbutionId, perlBrewAdapter, sdk -> ApplicationManager.getApplication().invokeLater(
            () -> {
              PerlBrewData perlBrewData = PerlBrewData.from(sdk);
              if (perlBrewData != null && project != null) {
                // consulo: the unified message box is asynchronous
                MessageBoxes.yesNo()
                  .title(LocalizeValue.of(PerlBundle.message("perl.vm.perlbrew.create.lib.title", perlBrewData.getPerlVersionString())))
                  .text(LocalizeValue.of(PerlBundle.message("perl.vm.perlbrew.create.lib.select", perlBrewData.getDistributionId())))
                  .showAsync(project)
                  .whenComplete((select, error) -> {
                    if (Boolean.TRUE.equals(select)) {
                      PerlProjectManager.getInstance(project).setProjectSdk(sdk);
                    }
                  });
              }
            }), project);
      }
    }.queue();
  }
}
