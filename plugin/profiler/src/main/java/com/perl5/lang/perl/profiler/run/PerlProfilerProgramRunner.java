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

package com.perl5.lang.perl.profiler.run;

import consulo.process.ExecutionException;
import consulo.execution.configuration.RunProfile;
import consulo.execution.configuration.RunProfileState;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.execution.ui.RunContentDescriptor;
import consulo.container.boot.ContainerPathManager;
import consulo.logging.Logger;
import consulo.util.io.FileUtil;
import consulo.execution.profiler.DefaultProfilerExecutorGroup;
import consulo.execution.profiler.ProfilerExecutorSettings;
import consulo.execution.profiler.ProfilerToolWindowManager;
import com.perl5.lang.perl.idea.run.GenericPerlProgramRunner;
import com.perl5.lang.perl.idea.run.GenericPerlRunConfiguration;
import com.perl5.lang.perl.idea.run.PerlRunProfileState;
import com.perl5.lang.perl.profiler.PerlProfilerBundle;
import com.perl5.lang.perl.profiler.configuration.PerlProfilerConfigurationState;
import com.perl5.lang.perl.util.PerlPackageUtilCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.util.concurrent.AsyncPromise;

import java.util.Set;

@SuppressWarnings("UnstableApiUsage")
public class PerlProfilerProgramRunner extends GenericPerlProgramRunner {
  private static final Logger LOG = Logger.getInstance(PerlProfilerProgramRunner.class);

  @Override
  public @NotNull String getRunnerId() {
    return "Perl Profiler";
  }

  @Override
  public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
    if (!(profile instanceof GenericPerlRunConfiguration)) {
      return false;
    }
    var executorSettings = getExecutorSettings(executorId);
    return executorSettings != null && executorSettings.canRun(profile);
  }

  private static @Nullable ProfilerExecutorSettings getExecutorSettings(@NotNull String executorId) {
    var executorGroup = DefaultProfilerExecutorGroup.getInstance();
    if (executorGroup == null) {
      return null;
    }
    return executorGroup.getRegisteredSettings(executorId);
  }

  @Override
  protected @Nullable PerlRunProfileState createState(@NotNull ExecutionEnvironment executionEnvironment)
    throws ExecutionException {

    var executorSettings = getExecutorSettings(executionEnvironment.getExecutor().getId());
    if (executorSettings == null) {
      var message = PerlProfilerBundle.message("dialog.message.unable.to.find.executor.settings");
      LOG.error(message);
      throw new ExecutionException(message);
    }
    var runProfile = executionEnvironment.getRunProfile();
    if (!executorSettings.canRun(runProfile)) {
      var message = PerlProfilerBundle.message("dialog.message.unable.to.run.with", runProfile, executorSettings);
      LOG.error(message);
      throw new ExecutionException(message);
    }
    var perlProfilerConfigurationState = executorSettings.getState();
    if (!(perlProfilerConfigurationState instanceof PerlProfilerConfigurationState profilerConfigurationState)) {
      LOG.error("PerlProfilerConfigurationState expected, got: " + perlProfilerConfigurationState);
      throw new ExecutionException(
        PerlProfilerBundle.message("dialog.message.wrong.profiler.configuration.state", perlProfilerConfigurationState));
    }

    return new PerlProfilerRunProfileState(executionEnvironment, profilerConfigurationState);
  }

  @Override
  protected Set<String> getRequiredModules(@NotNull ExecutionEnvironment environment) {
    var modules = super.getRequiredModules(environment);
    modules.add(PerlPackageUtilCore.PROFILER_MODULE);
    return modules;
  }

  @Override
  protected void doExecute(@NotNull RunProfileState state,
                           @NotNull ExecutionEnvironment environment,
                           @NotNull AsyncPromise<? super RunContentDescriptor> result) throws ExecutionException {
    if (!(state instanceof PerlProfilerRunProfileState profilerRunProfileState)) {
      LOG.error("PerlProfilerRunProfileState expected, got " + state);
      throw new ExecutionException(PerlProfilerBundle.message("dialog.message.incorrect.run.configuration.state.see.logs.for.details"));
    }
    var profileResultsPath = profilerRunProfileState.getProfilingResultsPath();
    LOG.info("Profiling results saved in: " + profileResultsPath);
    if (FileUtil.isAncestor(ContainerPathManager.get().getSystemPath(), profileResultsPath.toString(), true)) {
      // fxime we probably should fix permissions here
      // TODO consulo: IntelliJ NioFiles.deleteRecursively is not available, FileUtil.delete only reports a failure
      //  (the IOException is logged by FileUtil and not attached to the ExecutionException)
      if (!FileUtil.delete(profileResultsPath)) {
        throw new ExecutionException(PerlProfilerBundle.message("dialog.message.error.removing.old.profiling.data.at", profileResultsPath));
      }
    }
    else {
      LOG.error("Wrong profiler results directory: " + profileResultsPath);
    }
    //noinspection ResultOfMethodCallIgnored
    profileResultsPath.toFile().mkdirs();

    result.then(descriptor -> {
      if (descriptor instanceof RunContentDescriptor runContentDescriptor) {
        var profilerProcess = new PerlProfilerProcess(environment, runContentDescriptor, profilerRunProfileState);
        ProfilerToolWindowManager.getInstance(environment.getProject()).addProfilerProcessTab(profilerProcess, false);
      }
      return descriptor;
    });
    createAndSetContentDescriptor(environment, state.execute(environment.getExecutor(), this), result);
  }
}
