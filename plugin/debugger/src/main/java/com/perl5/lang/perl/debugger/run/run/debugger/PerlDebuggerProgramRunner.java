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

package com.perl5.lang.perl.debugger.run.run.debugger;

import consulo.process.ExecutionException;
import consulo.execution.configuration.RunProfile;
import consulo.execution.configuration.RunProfileState;
import consulo.execution.debug.DefaultDebugExecutor;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.execution.ui.RunContentDescriptor;
import consulo.application.ApplicationManager;
import consulo.logging.Logger;
import consulo.execution.debug.XDebugProcess;
import consulo.execution.debug.XDebugProcessStarter;
import consulo.execution.debug.XDebugSession;
import consulo.execution.debug.XDebuggerManager;
import com.perl5.lang.perl.debugger.PerlDebugProcess;
import com.perl5.lang.perl.debugger.PerlDebuggerBundle;
import com.perl5.lang.perl.idea.run.GenericPerlProgramRunner;
import com.perl5.lang.perl.idea.run.PerlRunProfileState;
import com.perl5.lang.perl.idea.run.debugger.PerlDebugOptions;
import com.perl5.lang.perl.util.PerlPackageUtilCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.util.concurrent.AsyncPromise;

import java.util.Set;


public class PerlDebuggerProgramRunner extends GenericPerlProgramRunner {
  private static final Logger LOG = Logger.getInstance(PerlDebuggerProgramRunner.class);
  @Override
  public @NotNull String getRunnerId() {
    return "Perl Debugger";
  }

  @Override
  public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
    return executorId.equals(DefaultDebugExecutor.EXECUTOR_ID) && profile instanceof PerlDebugOptions;
  }

  @Override
  protected @Nullable PerlRunProfileState createState(@NotNull ExecutionEnvironment executionEnvironment) {
    return new PerlDebugProfileState(executionEnvironment);
  }

  @Override
  protected Set<String> getRequiredModules(@NotNull ExecutionEnvironment environment) {
    var modules = super.getRequiredModules(environment);
    modules.add(PerlPackageUtilCore.DEBUGGER_MODULE);
    return modules;
  }

  @Override
  protected void doExecute(@NotNull RunProfileState state,
                           @NotNull ExecutionEnvironment env,
                           @NotNull AsyncPromise<? super RunContentDescriptor> result) throws ExecutionException {
    if (!(state instanceof PerlDebugProfileStateBase perlDebugProfileStateBase)) {
      LOG.error("PerlDebugProfileStateBase expected, got " + state + " for " + env);
      throw new ExecutionException(PerlDebuggerBundle.message("dialog.message.incorrect.run.state"));
    }
    var executionResult = perlDebugProfileStateBase.execute(env.getExecutor(), this);
    ApplicationManager.getApplication().invokeLater(() -> {
      try {
        // consulo: Consulo XDebuggerManager has no session builder, startSession(environment, starter) is used
        var sessionStartedResult = XDebuggerManager.getInstance(env.getProject()).startSession(env, new XDebugProcessStarter() {
          @Override
          public @NotNull XDebugProcess start(@NotNull XDebugSession session) {
            return new PerlDebugProcess(session, perlDebugProfileStateBase, executionResult);
          }
        });

        result.setResult(sessionStartedResult.getRunContentDescriptor());
      }
      catch (ExecutionException e) {
        LOG.error(e);
        result.setError(e);
      }
    });
  }
}
