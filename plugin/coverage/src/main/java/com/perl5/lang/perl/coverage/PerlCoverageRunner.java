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

package com.perl5.lang.perl.coverage;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import consulo.process.ExecutionException;
import consulo.process.util.ProcessOutput;
import consulo.project.ui.notification.Notification;
import consulo.project.ui.notification.NotificationType;
import consulo.project.ui.notification.Notifications;
import consulo.application.ApplicationManager;
import consulo.application.ReadAction;
import consulo.logging.Logger;
import consulo.application.progress.ProgressManager;
import consulo.project.Project;
import consulo.content.bundle.Sdk;
import consulo.util.lang.ref.Ref;
import consulo.util.io.FileUtil;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.annotation.component.ExtensionImpl;
import consulo.execution.coverage.data.CoverageLine;
import consulo.execution.coverage.data.CoverageLineImpl;
import consulo.execution.coverage.data.CoverageProjectData;
import consulo.execution.coverage.data.CoverageProjectDataImpl;
import consulo.execution.coverage.data.CoverageUnit;
import consulo.execution.coverage.data.LineStatus;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.idea.execution.PerlCommandLine;
import com.perl5.lang.perl.idea.sdk.host.PerlHostData;
import com.perl5.lang.perl.util.PerlPluginUtil;
import com.perl5.lang.perl.util.PerlRunUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import consulo.execution.coverage.CoverageEngine;
import consulo.execution.coverage.CoverageRunner;
import consulo.execution.coverage.CoverageSuite;

// consulo: upstream <coverageRunner> registration in perl5.plugin.coverage.main.xml
@ExtensionImpl
public class PerlCoverageRunner extends CoverageRunner {
  private static final String COVER = "cover";
  private static final String COVER_LIB = "Devel::Cover";
  private static final Logger LOG = Logger.getInstance(PerlCoverageRunner.class);

  // consulo: Consulo CoverageRunner#loadCoverageData accepts a File and returns the nullable coverage data instead of
  // CoverageLoadingResult; failures are logged and reported as null
  @Override
  public @Nullable CoverageProjectData loadCoverageData(@NotNull File sessionDataIoFile, @Nullable CoverageSuite baseCoverageSuite) {
    Path sessionDataFile = sessionDataIoFile.toPath();
    if (!(baseCoverageSuite instanceof PerlCoverageSuite perlCoverageSuite)) {
      LOG.warn(
        "Wrong type of coverage suite: " +
        (baseCoverageSuite == null ? "null" : baseCoverageSuite.getClass().getCanonicalName()));
      return null;
    }
    final Ref<CoverageProjectData> projectDataRef = new Ref<>();
    if (ApplicationManager.getApplication().isDispatchThread()) {

      ProgressManager.getInstance().runProcessWithProgressSynchronously(
        () -> projectDataRef.set(doLoadCoverageData(sessionDataFile, (PerlCoverageSuite)baseCoverageSuite)),
        PerlCoverageBundle.message("dialog.title.loading.coverage.data"), true, baseCoverageSuite.getProject());

    }
    else {
      projectDataRef.set(doLoadCoverageData(sessionDataFile, perlCoverageSuite));
    }
    return projectDataRef.get();
  }

  private static @Nullable CoverageProjectData doLoadCoverageData(@NotNull Path sessionDataFile, @NotNull PerlCoverageSuite perlCoverageSuite) {
    Project project = perlCoverageSuite.getProject();
    Sdk effectiveSdk;
    try {
      effectiveSdk = perlCoverageSuite.getConfiguration().getEffectiveSdk();
    }
    catch (ExecutionException e) {
      LOG.error(e);
      return null;
    }

    PerlHostData<?, ?> hostData = PerlHostData.from(effectiveSdk);
    if (hostData == null) {
      LOG.warn("No host data for " + effectiveSdk);
      return null;
    }
    try {
      hostData.fixPermissionsRecursively(sessionDataFile.toFile().getAbsolutePath(), project);
    }
    catch (ExecutionException e) {
      LOG.warn("Error fixing permissions for " + sessionDataFile);
    }

    PerlCommandLine perlCommandLine = ReadAction.compute(() -> {
      if (project.isDisposed()) {
        LOG.debug("Project disposed");
        return null;
      }

      VirtualFile coverFile = PerlRunUtil.findLibraryScriptWithNotification(effectiveSdk, project, COVER, COVER_LIB);
      if (coverFile == null) {
        LOG.warn("No `cover` script found in " + effectiveSdk);
        return null;
      }

      PerlCommandLine commandLine = PerlRunUtil.getPerlCommandLine(
        project, effectiveSdk, coverFile,
        Collections.singletonList(PerlRunUtil.PERL_I + hostData.getRemotePath(PerlPluginUtil.getHelpersLibPath())),
        Collections.emptyList());

      if (commandLine == null) {
        LOG.warn("Unable to create a command line: " +
                 " project: " + project +
                 "; sdk:" + effectiveSdk +
                 "; coverageFile: " + coverFile);
        return null;
      }

      String remotePath = hostData.getRemotePath(sessionDataFile.toFile().getAbsolutePath());
      if (StringUtil.isEmpty(remotePath)) {
        LOG.warn("Unable to map remote path for: " + sessionDataFile.toAbsolutePath() + " in " + hostData);
        return null;
      }

      commandLine
        .addParameters("--silent", "--nosummary", "-report", "camelcade", remotePath);
      commandLine.withSdk(effectiveSdk);
      commandLine.withProject(project);

      return commandLine;
    });

    if (perlCommandLine == null) {
      return null;
    }

    try {
      LOG.info("Loading coverage by: " + perlCommandLine.getCommandLineString());
      ProcessOutput output = PerlHostData.execAndGetOutput(perlCommandLine);
      if (output.getExitCode() != 0) {
        String errorMessage = output.getStderr();
        if (StringUtil.isEmpty(errorMessage)) {
          errorMessage = output.getStdout();
        }

        if (!StringUtil.isEmpty(errorMessage)) {
          showError(project, errorMessage);
        }
        return null;
      }
      String stdout = output.getStdout();
      if (StringUtil.isEmpty(stdout)) {
        return null;
      }

      try {
        PerlFileCoverageData[] filesData = new Gson().fromJson(stdout, PerlFileCoverageData[].class);
        if (filesData != null) {
          return parsePerlFileData(PerlHostData.notNullFrom(effectiveSdk), filesData);
        }
      }
      catch (JsonParseException e) {
        LOG.warn("Error parsing JSON", e);
        showError(project, e.getMessage());
      }
    }
    catch (ExecutionException e) {
      LOG.warn("Error loading coverage", e);
      showError(project, e.getMessage());
    }
    return null;
  }

  // consulo: Consulo coverage data model (CoverageProjectData/CoverageUnit/CoverageLine) instead of IntelliJ coverage rt
  // ProjectData/ClassData/LineData; line status is computed once and stored instead of overriding LineData#getStatus
  private static @NotNull CoverageProjectData parsePerlFileData(@NotNull PerlHostData<?, ?> hostData, @NotNull PerlFileCoverageData[] filesData) {
    CoverageProjectData projectData = new CoverageProjectDataImpl();
    for (PerlFileCoverageData perlFileCoverageData : filesData) {
      if (StringUtil.isEmpty(perlFileCoverageData.name) || perlFileCoverageData.lines == null) {
        LOG.warn("Name or lines is null in " + perlFileCoverageData);
        continue;
      }
      String localPath = hostData.getLocalPath(perlFileCoverageData.name);
      if (localPath == null) {
        continue;
      }
      CoverageUnit classData = projectData.getOrCreateUnit(FileUtil.toSystemIndependentName(localPath));
      Set<Map.Entry<Integer, PerlLineData>> linesEntries = perlFileCoverageData.lines.entrySet();
      Integer maxLineNumber = linesEntries.stream().map(Map.Entry::getKey).max(Integer::compare).orElse(0);
      CoverageLine[] linesData = new CoverageLine[maxLineNumber + 1];
      for (Map.Entry<Integer, PerlLineData> lineEntry : linesEntries) {
        PerlLineData perlLineData = lineEntry.getValue();
        final Integer lineNumber = lineEntry.getKey();
        CoverageLineImpl lineData = new CoverageLineImpl(lineNumber, null);
        if (perlLineData.cover == 0) {
          lineData.setStatus(LineStatus.NOT_COVERED);
        }
        else if (perlLineData.cover < perlLineData.data) {
          lineData.setStatus(LineStatus.PARTIALLY_COVERED);
        }
        else {
          lineData.setStatus(LineStatus.COVERED);
        }
        lineData.setHits(perlLineData.cover);
        linesData[lineNumber] = lineData;
      }

      classData.setLines(Arrays.asList(linesData));
    }

    return projectData;
  }

  private static void showError(@NotNull Project project, @NotNull String message) {
    ReadAction.run(() -> {
      if (!project.isDisposed()) {
        LOG.warn("Error loading coverage: " + message);
        Notifications.Bus.notify(
          // consulo: Consulo requires a registered NotificationGroup instead of the group id, see PerlCoverageNotificationGroupContributor
          new Notification(
            PerlCoverageNotificationGroupContributor.COVERAGE_LOADING_ERROR,
            PerlBundle.message("perl.coverage.loading.error"),
            message,
            NotificationType.ERROR
          ),
          project
        );
      }
    });
  }

  @Override
  public @NotNull String getPresentableName() {
    return PerlBundle.message("perl.perl5");
  }

  @Override
  public @NotNull String getId() {
    return "Perl5CoverageRunner";
  }

  @Override
  public @NotNull String getDataFileExtension() {
    return "json";
  }

  @Override
  public boolean acceptsCoverageEngine(@NotNull CoverageEngine engine) {
    return engine instanceof PerlCoverageEngine;
  }
}
