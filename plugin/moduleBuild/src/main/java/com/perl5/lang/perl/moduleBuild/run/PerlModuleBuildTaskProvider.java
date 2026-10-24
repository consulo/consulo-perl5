/*
 * Copyright 2015-2023 Alexandr Evstigneev
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

package com.perl5.lang.perl.moduleBuild.run;

import consulo.annotation.component.ExtensionImpl;
import consulo.execution.BeforeRunTask;
import consulo.execution.BeforeRunFragment;
import consulo.execution.configuration.RunConfiguration;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.execution.ui.RunContentDescriptor;
import consulo.project.ui.notification.NotificationService;
import consulo.project.ui.notification.NotificationType;
import consulo.dataContext.DataContext;
import consulo.application.ApplicationManager;
import consulo.application.util.concurrent.AppExecutorUtil;
import com.dslplatform.json.CompiledJson;
import consulo.logging.Logger;
import consulo.project.Project;
import consulo.localize.LocalizeValue;
import consulo.ui.UIAccess;
import consulo.ui.image.Image;
import consulo.util.lang.ref.Ref;
import consulo.virtualFileSystem.VirtualFile;
import com.perl5.PerlIcons;
import com.perl5.lang.perl.moduleBuild.ModuleBuildBundle;
import com.perl5.lang.perl.moduleBuild.PerlModuleBuildUtil;
import com.perl5.lang.perl.util.PerlRunUtil;
import com.perl5.lang.perl.util.PerlUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

// consulo: before run tasks are run configuration fragments in Consulo
@ExtensionImpl
public class PerlModuleBuildTaskProvider implements BeforeRunFragment<PerlModuleBuildTaskProvider.PerlModuleBuildTask> {
  private static final Logger LOG = Logger.getInstance(PerlModuleBuildTaskProvider.class);
  private static final String ID = "Module::Builder Before Run Task";

  @Override
  public @NotNull String getId() {
    return ID;
  }

  @Override
  public @NotNull LocalizeValue getName() {
    return LocalizeValue.localizeTODO(ModuleBuildBundle.message("generate.build.file"));
  }

  @Override
  public @NotNull Class<PerlModuleBuildTask> getStateClass() {
    return PerlModuleBuildTask.class;
  }

  @Override
  public @Nullable PerlModuleBuildTask createTask(@NotNull RunConfiguration runConfiguration) {
    return runConfiguration instanceof PerlModuleBuildTestRunConfiguration ? new PerlModuleBuildTask() : null;
  }

  @Override
  public @Nullable Image getIcon(@NotNull RunConfiguration runConfiguration) {
    return PerlIcons.PERL_LANGUAGE_ICON;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }

  @Override
  public boolean canExecuteTask(@NotNull RunConfiguration configuration, @NotNull PerlModuleBuildTask task) {
    return configuration instanceof PerlModuleBuildTestRunConfiguration;
  }

  @Override
  public @NotNull LocalizeValue getDescription(PerlModuleBuildTask task) {
    return LocalizeValue.localizeTODO(ModuleBuildBundle.message("re.generates.build.file.by.running.build.pl.when.necessary"));
  }

  @Override
  public @NotNull CompletableFuture<?> executeAsync(@NotNull UIAccess uiAccess,
                                                    @NotNull DataContext context,
                                                    @NotNull RunConfiguration configuration,
                                                    @NotNull ExecutionEnvironment environment,
                                                    @NotNull PerlModuleBuildTask task) {
    return CompletableFuture.runAsync(() -> {
      if (!executeTask(configuration, environment)) {
        throw new CancellationException();
      }
    }, AppExecutorUtil.getAppExecutorService());
  }

  private static boolean executeTask(@NotNull RunConfiguration configuration, @NotNull ExecutionEnvironment environment) {
    if (!(configuration instanceof PerlModuleBuildTestRunConfiguration moduleBuildTestRunConfiguration)) {
      LOG.warn("Trying to run task in wrong configuration: " + configuration);
      return false;
    }
    var contentRoot = moduleBuildTestRunConfiguration.getEffectiveContentRootSafe();
    if (contentRoot == null) {
      showError(configuration.getProject(), ModuleBuildBundle.message("notification.content.no.effective.content.root.for", configuration));
      return false;
    }
    return updateBuildfile(environment.getProject(), contentRoot);
  }

  public static boolean updateBuildfile(@NotNull Project project, @NotNull VirtualFile contentRoot) {
    var buildPlFile = contentRoot.findChild(PerlModuleBuildUtil.BUILD_PL);
    var buildFile = contentRoot.findChild(PerlModuleBuildUtil.BUILD);

    if (buildPlFile == null) {
      showError(project, ModuleBuildBundle.message("notification.content.no.build.pl.file.in.content.root", contentRoot));
      return false;
    }

    if (buildFile != null && PerlUtil.getLastModifiedTime(buildFile) >= PerlUtil.getLastModifiedTime(buildPlFile)) {
      return true;
    }

    Ref<RunContentDescriptor> descriptorRef = Ref.create();
    var commandLine = PerlRunUtil.getPerlCommandLine(project, buildPlFile);

    if( commandLine == null){
      showError(project, ModuleBuildBundle.message("notification.content.unable.to.create.command.line.to.process", buildPlFile));
      return false;
    }
      commandLine.withWorkDirectory(contentRoot.getPath());
    ApplicationManager.getApplication().invokeAndWait(
      () -> descriptorRef.set(PerlRunUtil.runInConsole(commandLine)));

    var processHandler = descriptorRef.get().getProcessHandler();
    if( processHandler == null){
      return false;
    }
    if (processHandler.waitFor()) {
      var exitCode = processHandler.getExitCode();
      if (exitCode != null && exitCode == 0) {
        return true;
      }
      showError(project, ModuleBuildBundle.message("notification.content.process.didn.t.end.with.exit.code", String.valueOf(exitCode)));
    }
    else {
      showError(project, ModuleBuildBundle.message("notification.content.process.didn.t.end", processHandler));
    }
    return false;
  }

  private static void showError(@NotNull Project project, @NotNull String message){
    LOG.warn(message);
    NotificationService.getInstance()
      .newOfType(PerlModuleBuildNotificationGroups.BUILD_ERROR, NotificationType.ERROR)
      .title(LocalizeValue.of(ModuleBuildBundle.message("perl.module.build.notification.generating.error.title")))
      .content(LocalizeValue.of(message))
      .notify(project);
  }


  @CompiledJson
  public static final class PerlModuleBuildTask extends BeforeRunTask {
    public PerlModuleBuildTask() {
      // consulo: before run tasks are disabled by default in Consulo (enabled in IntelliJ)
      setEnabled(true);
    }
  }
}
