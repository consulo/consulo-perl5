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

package com.perl5.lang.perl.idea.run;

import consulo.process.ExecutionException;
import consulo.execution.ExecutionResult;
import consulo.execution.configuration.RunProfileState;
import consulo.execution.configuration.RunnerSettings;
import consulo.execution.runner.AsyncProgramRunner;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.execution.runner.RunContentBuilder;
import consulo.execution.ui.RunContentDescriptor;
import consulo.application.ApplicationManager;
import consulo.ui.ModalityState;
import consulo.logging.Logger;
import consulo.document.FileDocumentManager;
import consulo.ui.MessageBoxes;
import consulo.localize.LocalizeValue;
import consulo.content.bundle.Sdk;
import consulo.application.util.concurrent.AppExecutorUtil;
import consulo.util.collection.ContainerUtil;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.adapters.PackageManagerAdapterFactory;
import com.perl5.lang.perl.idea.project.PerlProjectManager;
import com.perl5.lang.perl.util.PerlPackageUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.util.concurrent.AsyncPromise;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public abstract class GenericPerlProgramRunner extends AsyncProgramRunner<RunnerSettings> {
  private static final Logger LOG = Logger.getInstance(GenericPerlProgramRunner.class);

  protected abstract @Nullable PerlRunProfileState createState(@NotNull ExecutionEnvironment executionEnvironment) throws
                                                                                                                   ExecutionException;

  @Override
  protected @NotNull CompletableFuture<RunContentDescriptor> executeImpl(@NotNull RunProfileState state, @NotNull ExecutionEnvironment environment) {
    FileDocumentManager.getInstance().saveAllDocuments();
    AsyncPromise<RunContentDescriptor> result = new AsyncPromise<>();
    var missingModules = getMissingModules(environment);
    var project = environment.getProject();
    var sdk = missingModules.isEmpty() ? null : PerlProjectManager.getSdk(project);
    if (sdk != null) {
      // consulo: Messages are Swing dialogs (not available in web), the unified message box is asynchronous
      MessageBoxes.yesNo()
        .title(LocalizeValue.of(PerlBundle.message("dialog.title.missing.modules")))
        .text(LocalizeValue.of(PerlBundle.message("dialog.message.are.missing.in.would.you.like.to.install",
                                                  missingModules.stream().sorted().collect(Collectors.joining(", ")))))
        .showAsync(project)
        .whenComplete((install, error) -> {
          if (Boolean.TRUE.equals(install)) {
            installMissingModules(environment, sdk, missingModules);
            result.setResult(null);
          }
          else {
            executeInBackground(state, environment, result);
          }
        });
    }
    else {
      executeInBackground(state, environment, result);
    }
    // Consulo AsyncProgramRunner works with CompletableFuture
    CompletableFuture<RunContentDescriptor> future = new CompletableFuture<>();
    result.onSuccess(future::complete).onError(future::completeExceptionally);
    return future;
  }

  /**
   * Handles missing modules by prompting the user to install them, and then installing them
   * using the package manager adapter. Executes the environment after the installation is complete.
   *
   * @param environment    the execution environment
   * @param missingModules a list of missing modules
   * @return true iff invoker should not call run configuration itself, because it's going to be handled by the callback
   */
  private void executeInBackground(@NotNull RunProfileState state,
                                   @NotNull ExecutionEnvironment environment,
                                   @NotNull AsyncPromise<RunContentDescriptor> result) {
    AppExecutorUtil.getAppExecutorService().execute(() -> {
      try {
        doExecute(state, environment, result);
      }
      catch (ExecutionException e) {
        result.setError(e);
      }
    });
  }

  /**
   * Installs missing modules using the package manager adapter. Executes the environment after the installation is complete.
   * consulo: upstream handleMissingModules, the confirmation is asked asynchronously by the caller
   */
  private void installMissingModules(@NotNull ExecutionEnvironment environment, @NotNull Sdk sdk, List<String> missingModules) {
    var project = environment.getProject();
    AppExecutorUtil.getAppExecutorService().execute(() -> {
      var packageManagerAdapter = PackageManagerAdapterFactory.create(sdk, project);
      ApplicationManager.getApplication().invokeLater(() -> packageManagerAdapter.install(missingModules, () -> {
        if (project.isDisposed()) {
          return;
        }
        ApplicationManager.getApplication().invokeLater(() -> {
          try {
            execute(environment);
          }
          catch (ExecutionException e) {
            LOG.warn("Error running environment after installation: " + environment + "; " + e.getMessage());
          }
        });
      }, true));
    });
  }

  private List<String> getMissingModules(@NotNull ExecutionEnvironment environment) {
    var project = environment.getProject();
    return ContainerUtil.filter(getRequiredModules(environment),
                                it -> PerlPackageUtil.getPackageVirtualFileByPackageName(project, it) == null);
  }

  /**
   * Returns a modifiable set of required modules for the given execution environment.
   * By default, the set is obtained from the "Required modules" field of the run configuration.
   *
   * @param environment the execution environment
   * @return a modifiable set of required modules
   */
  protected Set<String> getRequiredModules(@NotNull ExecutionEnvironment environment) {
    var runProfile = environment.getRunProfile();
    return runProfile instanceof GenericPerlRunConfiguration perlRunConfiguration
           ? new HashSet<>(perlRunConfiguration.getRequiredModulesList())
           : new HashSet<>();
  }

  protected static void createAndSetContentDescriptor(@NotNull ExecutionEnvironment environment,
                                                      @Nullable ExecutionResult executionResult,
                                                      @NotNull AsyncPromise<? super RunContentDescriptor> result) {
    if (executionResult == null) {
      result.setResult(null);
    }
    else {
      ApplicationManager.getApplication().invokeLater(
        () -> result.setResult(new RunContentBuilder(executionResult, environment).showRunContent(environment.getContentToReuse())),
        ModalityState.any());
    }
  }

  protected abstract void doExecute(@NotNull RunProfileState state,
                                    @NotNull ExecutionEnvironment environment,
                                    @NotNull AsyncPromise<? super RunContentDescriptor> result) throws ExecutionException;
}
