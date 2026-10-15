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

package com.perl5.lang.perl.idea.sdk.versionManager;

import consulo.process.event.ProcessEvent;
import consulo.process.event.ProcessListener;
import consulo.application.ApplicationManager;
import consulo.application.progress.ProgressIndicator;
import consulo.application.progress.Task;
import consulo.project.Project;
import consulo.ui.image.Image;
import consulo.util.lang.VersionComparatorUtil;
import com.perl5.PerlBundle;
import com.perl5.PerlIcons;
import com.perl5.lang.perl.idea.project.PerlProjectManager;
import com.perl5.lang.perl.idea.sdk.host.PerlHostData;
import com.perl5.lang.perl.util.PerlRunUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class InstallPerlHandler {
  private final @NotNull String myVersionManagerPath;
  private final @NotNull PerlRealVersionManagerHandler<?, ?> myVersionManageHandler;

  public InstallPerlHandler(@NotNull String versionManagerPath,
                            @NotNull PerlRealVersionManagerHandler<?, ?> versionManageHandler) {
    myVersionManagerPath = versionManagerPath;
    myVersionManageHandler = versionManageHandler;
  }

  void install(@NotNull PerlHostData<?, ?> hostData, @Nullable Project project) {
    // consulo: a background task, the dialog is asynchronous and a modal task would block the web UI thread
    new Task.Backgroundable(project, PerlBundle.message("perl.vm.installing.perl"), false) {
      @Override
      public void run(@NotNull ProgressIndicator indicator) {
        indicator.setIndeterminate(true);
        doInstall(hostData, project);
      }
    }.queue();
  }

  void doInstall(@NotNull PerlHostData<?, ?> hostData, @Nullable Project project) {
    if (ApplicationManager.getApplication().isDispatchThread()) {
      throw new RuntimeException("Should not be invoked on EDT");
    }
    PerlVersionManagerAdapter vmAdapter = createAdapter(myVersionManagerPath, hostData);
    PerlRunUtil.setProgressText(PerlBundle.message("perl.vm.fetching.available.perls"));
    List<String> distributionsList = vmAdapter.getInstallableDistributionsList();
    if (distributionsList == null) {
      return;
    }
    ApplicationManager.getApplication().invokeLater(() -> {
      PerlInstallFormOptions optionsForm = createOptionsForm();
      // consulo: unified dialog instead of the Swing MyDialog (not available in web), it is answered asynchronously
      PerlUnifiedInstallForm.show(this, project, optionsForm, new ArrayList<>(distributionsList), installForm -> {
        boolean createInterpreter = installForm.isAddInstalledPerl();
        boolean chooseInterpreter = installForm.isChooseInstalledPerl();

        String distributionId = installForm.getSelectedDistributionId();
        String targetName = optionsForm.getTargetName(distributionId);

        vmAdapter.installPerl(project, distributionId, optionsForm.buildParametersList(), new ProcessListener() {
          @Override
          public void processTerminated(@NotNull ProcessEvent event) {
            if (!createInterpreter || event.getExitCode() != 0) {
              return;
            }
            myVersionManageHandler.createInterpreter(
              targetName, vmAdapter, sdk -> {
                if (chooseInterpreter && project != null) {
                  ApplicationManager.getApplication().invokeLater(
                    () -> PerlProjectManager.getInstance(project).setProjectSdk(sdk));
                }
              }, project);
          }
        });
      });
    });
  }

  public @NotNull PerlRealVersionManagerHandler<?, ?> getVersionManageHandler() {
    return myVersionManageHandler;
  }

  protected abstract @NotNull String doCleanDistributionItem(@NotNull String rawItem);

  protected abstract boolean doIsInstalled(@NotNull String rawItem);

  protected abstract @NotNull PerlVersionManagerAdapter createAdapter(@NotNull String vmPath, @NotNull PerlHostData<?, ?> hostData);

  protected abstract @NotNull PerlInstallFormOptions createOptionsForm();

  protected int doCompareVersions(String a, String b) {
    return VersionComparatorUtil.compare(b, a);
  }

  // consulo: unified Image instead of Swing Icon, it is rendered by the unified install dialog
  public @Nullable Image doGetIcon(@NotNull String distribution) {
    return PerlIcons.PERL_LANGUAGE_ICON;
  }
}
