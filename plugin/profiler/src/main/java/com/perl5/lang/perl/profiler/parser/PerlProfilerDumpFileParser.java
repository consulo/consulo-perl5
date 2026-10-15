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

package com.perl5.lang.perl.profiler.parser;

import consulo.process.ExecutionException;
import consulo.process.BaseProcessHandler;
import consulo.process.event.ProcessEvent;
import consulo.process.event.ProcessListener;
import consulo.process.ProcessOutputType;
import consulo.logging.Logger;
import consulo.application.progress.ProgressIndicator;
import consulo.project.Project;
import consulo.util.dataholder.Key;
import consulo.util.lang.ref.Ref;

import consulo.execution.profiler.ui.NativeCallStackElementRenderer;
import com.perl5.lang.perl.idea.project.PerlProjectManager;
import com.perl5.lang.perl.idea.sdk.host.PerlHostData;
import com.perl5.lang.perl.profiler.PerlProfilerBundle;
import com.perl5.lang.perl.util.PerlPluginUtil;
import com.perl5.lang.perl.util.PerlRunUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import consulo.execution.profiler.Failure;
import consulo.execution.profiler.NewCallTreeOnlyProfilerData;
import consulo.execution.profiler.ProfilerDumpFileParser;
import consulo.execution.profiler.ProfilerDumpFileParsingResult;
import consulo.execution.profiler.Success;

/**
 * Parser for nytprof dump
 */
public class PerlProfilerDumpFileParser implements ProfilerDumpFileParser {
  private static final Logger LOG = Logger.getInstance(PerlProfilerDumpFileParser.class);
  private final @NotNull Project myProject;

  public PerlProfilerDumpFileParser(@NotNull Project project) {
    myProject = project;
  }

  @Override
  public @Nullable String getHelpId() {
    return null;
  }

  @Override
  public @NotNull ProfilerDumpFileParsingResult parse(@NotNull File file, @NotNull ProgressIndicator indicator) {
    var perlSdk = PerlProjectManager.getSdk(myProject);
    if (perlSdk == null) {
      return new Failure(PerlProfilerBundle.message("perl.sdk.is.not.set.for.the.project.0", myProject.getName()));
    }

    var nytprofUtilPath = PerlPluginUtil.getHelperPath("nytprofcalls.pl");
    var perlCommandLine = PerlRunUtil.getPerlCommandLine(myProject, nytprofUtilPath);
    if (perlCommandLine == null) {
      LOG.warn("Unable to create command line for parsing results in " + myProject);
      return new Failure(PerlProfilerBundle.message("failed.to.create.command.line.for.parsing.profiling.results"));
    }

    PerlHostData<?, ?> hostData = PerlHostData.notNullFrom(perlSdk);
    try {
      hostData.fixPermissionsRecursively(file.getAbsolutePath(), myProject);
    }
    catch (ExecutionException e) {
      LOG.warn("Failed to fix permissions for " + file, e);
      return new Failure(PerlProfilerBundle.message("failed.to.set.permissions.for.the.0", file));
    }

    var localPath = file.getAbsolutePath();
    var remotePath = hostData.getRemotePath(localPath);

    perlCommandLine.withParameters(remotePath);

    var dumpParser = new PerlCollapsedDumpParser();

    try {
      BaseProcessHandler processHandler = PerlHostData.createProcessHandler(perlCommandLine);
      Ref<String> errorRef = Ref.create();
      processHandler.addProcessListener(new ProcessListener() {
        @Override
        public void processTerminated(@NotNull ProcessEvent event) {
          if (event.getExitCode() != 0) {
            var message = "Error parsing NYTProf output. Exit code: " + event.getExitCode() + ". Message: " + event.getText();
            errorRef.set(message);
            LOG.warn(message);
          }
        }

        @Override
        public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
          if (ProcessOutputType.isStderr(outputType)) {
            LOG.warn("Error output: " + event.getText());
          }
          else if (ProcessOutputType.isStdout(outputType)) {
            dumpParser.consumeText(event.getText(), indicator);
          }
        }
      });
      processHandler.startNotify();
      while (!processHandler.waitFor(10)) {
        indicator.checkCanceled();
      }
      if (errorRef.isNull()) {
        return new Success(
          new NewCallTreeOnlyProfilerData(dumpParser.getCallTreeBuilder(), NativeCallStackElementRenderer.INSTANCE));
      }
      return new Failure(errorRef.get());
    }
    catch (ExecutionException e) {
      LOG.warn("Error parsing results: " + e.getMessage());
      return new Failure(e.getMessage());
    }
  }
}
