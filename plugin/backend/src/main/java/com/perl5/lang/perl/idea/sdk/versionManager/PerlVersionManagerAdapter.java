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

import consulo.process.ExecutionException;
import consulo.process.event.ProcessEvent;
import consulo.process.event.ProcessListener;
import consulo.process.util.ProcessOutput;
import consulo.project.ui.notification.Notification;
import consulo.project.ui.notification.NotificationType;
import consulo.project.ui.notification.Notifications;
import consulo.application.ApplicationManager;
import consulo.ui.ModalityState;
import consulo.project.ui.notification.NotificationGroup;
import com.perl5.lang.perl.idea.sdk.PerlSdkNotificationGroups;
import consulo.logging.Logger;
import consulo.project.Project;
import consulo.ui.MessageBoxes;
import consulo.localize.LocalizeValue;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.idea.execution.PerlCommandLine;
import com.perl5.lang.perl.idea.sdk.host.PerlHostData;
import com.perl5.lang.perl.util.PerlRunUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.annotations.Unmodifiable;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Base class for CLI adapter of version manager
 */
public abstract class PerlVersionManagerAdapter {
  private static final Logger LOG = Logger.getInstance(PerlVersionManagerAdapter.class);

  private final @NotNull String myVersionManagerPath;
  private final @NotNull PerlHostData<?, ?> myHostData;

  public PerlVersionManagerAdapter(@NotNull String versionManagerPath, @NotNull PerlHostData<?, ?> hostData) {
    myVersionManagerPath = versionManagerPath;
    myHostData = hostData;
  }

  public @NotNull String getVersionManagerPath() {
    return myVersionManagerPath;
  }

  public @NotNull PerlHostData<?, ?> getHostData() {
    return myHostData;
  }

  protected abstract @Nullable Icon getIcon();

  protected void runInstallInConsole(@NotNull PerlCommandLine commandLine, @NotNull String entityName) {
    PerlRunUtil.runInConsole(
      commandLine
        .withHostData(myHostData)
        .withConsoleTitle(PerlBundle.message("perl.vm.installing", entityName))
        .withConsoleIcon(getIcon())
        .withVersionManagerData(PerlVersionManagerData.getDefault())
    );
  }

  /**
   * Used for installing packages using version manager, e.g. {@code install-cpanm}
   */
  public void runInstallInConsole(@NotNull Project project, @NotNull String packageName, @NotNull String command) {
    runInstallInConsole(createInstallCommandLine(project, command), packageName);
  }

  protected @NotNull PerlCommandLine createInstallCommandLine(@NotNull Project project,
                                                              @NotNull String command) {
    return new PerlCommandLine(getVersionManagerPath(), command)
      .withProject(project)
      .withHostData(myHostData)
      .withProcessListener(new ProcessListener() {
        @Override
        public void processTerminated(@NotNull ProcessEvent event) {
          PerlRunUtil.refreshSdkDirs(project);
        }
      });
  }

  /**
   * Executes commands in the context of {@code distributionId}
   */
  protected abstract @Nullable List<String> execWith(@NotNull String distributionId,
                                                     @SuppressWarnings("SameParameterValue") @NotNull String... commands);

  /**
   * Installs perl to the version manager
   *
   * @param project         context project
   * @param distributionId  perl to install
   * @param params          additional params
   * @param processListener process listener
   */
  public abstract void installPerl(@Nullable Project project,
                                   @NotNull String distributionId,
                                   @NotNull List<String> params,
                                   @NotNull ProcessListener processListener);

  /**
   * @return list of installed perl distributions
   * @apiNote returned items format is version-manager specific, for perlbrew it may be a {@code version@library}
   */
  protected abstract @Unmodifiable @Nullable List<String> getInstalledDistributionsList();

  /**
   * @return list of perls available for installation
   */
  protected abstract @Unmodifiable @Nullable List<String> getInstallableDistributionsList();

  protected @Nullable List<String> getOutput(@NotNull List<String> parameters) {
    return getOutput(it -> it.withParameters(parameters));
  }

  protected @Nullable List<String> getOutput(@NotNull Function<? super PerlCommandLine, ? extends PerlCommandLine> commandLineProcessor) {
    return getOutput(commandLineProcessor.apply(new PerlCommandLine(getVersionManagerPath())));
  }

  /**
   * @return output of perlbrew command or null if error happened
   */
  protected @Nullable List<String> getOutput(@NotNull PerlCommandLine commandLine) {
    ProcessOutput processOutput = getProcessOutput(commandLine.withHostData(getHostData()));
    if (processOutput == null) {
      LOG.debug("Process output for " + commandLine + " is null");
      return null;
    }
    if (processOutput.getExitCode() == 0) {
      LOG.debug("Process output for " + commandLine + " is\n" + processOutput);
      return processOutput.getStdoutLines(true);
    }
    LOG.warn("Process exited with non-zero code. Command line: " + commandLine +
             "; exit code: " + processOutput.getExitCode() +
             "; stdout: " + processOutput.getStdout() +
             "; stderr: " + processOutput.getStderr());
    notifyUser(PerlBundle.message("perl.vm.adapter.notification.message.exitcode"));
    return null;
  }

  protected @Nullable ProcessOutput getProcessOutput(@NotNull PerlCommandLine commandLine) {
    try {
      return PerlHostData.execAndGetOutput(commandLine.withHostData(getHostData()));
    }
    catch (ExecutionException e) {
      LOG.warn("Error running " + commandLine, e);
      notifyUser(PerlBundle.message("perl.vm.adapter.notification.message.exception", e.getMessage()));
    }
    return null;
  }

  protected @Nullable List<String> getOutput(@NotNull String... parameters) {
    return getOutput(Arrays.asList(parameters));
  }

  protected @NotNull NotificationGroup getNotificationGroup() {
    return PerlSdkNotificationGroups.VERSION_MANAGERS;
  }

  protected abstract @NotNull String getErrorNotificationTitle();

  private void notifyUser(@NotNull String message) {
    ApplicationManager.getApplication().invokeLater(() -> {
      //noinspection DialogTitleCapitalization
      var dialogTitle = getErrorNotificationTitle();
      if (ApplicationManager.getApplication().getCurrentModalityState() != ModalityState.nonModal()) {
        // consulo: Messages are Swing dialogs (not available in web), the unified message box is shown asynchronously
        MessageBoxes.okError(LocalizeValue.of(message)).title(LocalizeValue.of(dialogTitle)).showAsync();
      }
      else {
        Notifications.Bus.notify(new Notification(
          getNotificationGroup(),
          dialogTitle,
          message,
          NotificationType.ERROR
        ));
      }
    });
  }

  @TestOnly
  public final @Nullable List<String> getInstalledDistributionsListInTests() {
    return getInstalledDistributionsList();
  }

  @TestOnly
  public final @Unmodifiable @Nullable List<String> getInstallableDistributionsListInTests() {
    return getInstallableDistributionsList();
  }
}
