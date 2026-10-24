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

package com.perl5.lang.perl.idea.run.prove;

import com.google.common.annotations.VisibleForTesting;
import consulo.process.ExecutionException;
import consulo.execution.configuration.ConfigurationFactory;
import consulo.execution.configuration.LegacyRunConfigurationConverter;
import consulo.execution.configuration.RunConfigurationOptions;
import consulo.execution.configuration.fragment.RunConfigurationFragment;
import consulo.execution.test.sm.SMTestRunnerConnectionUtil;
import consulo.execution.test.sm.ui.SMTRunnerConsoleView;
import consulo.execution.ui.console.ConsoleView;
import consulo.application.ApplicationManager;
import consulo.logging.Logger;
import consulo.project.Project;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.util.collection.ContainerUtil;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.idea.execution.PerlCommandLine;
import com.perl5.lang.perl.idea.execution.PerlTerminalExecutionConsole;
import com.perl5.lang.perl.idea.run.GenericPerlRunConfiguration;
import com.perl5.lang.perl.idea.run.PerlRunProfileState;
import com.perl5.lang.perl.idea.sdk.host.PerlHostData;
import com.perl5.lang.perl.util.PerlPackageUtilCore;
import com.perl5.lang.perl.util.PerlRunUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static consulo.process.cmd.GeneralCommandLine.ParentEnvironmentType.CONSOLE;
import static consulo.process.cmd.GeneralCommandLine.ParentEnvironmentType.NONE;
import static com.perl5.lang.perl.util.PerlRunUtil.PERL5OPT;
import static com.perl5.lang.perl.util.PerlRunUtil.getPerlRunIncludeArguments;

public abstract class PerlAbstractTestRunConfiguration extends GenericPerlRunConfiguration {
  private static final Logger LOG = Logger.getInstance(PerlAbstractTestRunConfiguration.class);
  static final int DEFAULT_JOBS_NUMBER = 1;
  private static final String HARNESS_OPTIONS = "HARNESS_OPTIONS";
  private static final String HARNESS_OPTIONS_SEPARATOR = ":";
  private static final String HARNESS_OPTIONS_JOBS = "j";
  private static final String HARNESS_OPTIONS_FORMATTER = "f";
  private static final String HARNESS_OPTIONS_FORMATTER_CLASS = "TAP-Formatter-Camelcade";
  private static final String HARNESS_PERL_SWITCHES = "HARNESS_PERL_SWITCHES";

  public PerlAbstractTestRunConfiguration(@NotNull Project project,
                                          @NotNull ConfigurationFactory factory,
                                          @Nullable String name) {
    super(project, factory, name);
  }

  // consulo: settings are kept in PerlAbstractTestRunConfigurationOptions and edited with run configuration fragments
  @Override
  protected Class<? extends RunConfigurationOptions> getOptionsClass() {
    return PerlAbstractTestRunConfigurationOptions.class;
  }

  @Override
  public PerlAbstractTestRunConfigurationOptions getOptions() {
    return (PerlAbstractTestRunConfigurationOptions)super.getOptions();
  }

  @Override
  protected void convertLegacyState(LegacyRunConfigurationConverter converter) {
    super.convertLegacyState(converter);

    PerlAbstractTestRunConfigurationOptions options = new PerlAbstractTestRunConfigurationOptions();
    convertLegacyCommonState(converter, options);

    PerlAbstractTestRunConfigurationLegacyState old = converter.readXml(PerlAbstractTestRunConfigurationLegacyState.class);
    options.setJobsNumber(old.jobsNumber);
    options.setTestScriptParameters(old.testScriptParameters);
    converter.setOptions(options);
  }

  @Override
  public List<RunConfigurationFragment<?>> createFragments() {
    List<RunConfigurationFragment<?>> fragments = new ArrayList<>();
    fragments.add(new PerlTestScriptFragment(getRunConfigurationProducer()));
    fragments.add(new PerlTestJobsFragment());
    fragments.add(new PerlTestScriptArgumentsFragment());
    fragments.addAll(createCommonFragments());
    return fragments;
  }

  int getJobsNumber() {
    return getOptions().getJobsNumber();
  }

  @VisibleForTesting
  public void setJobsNumber(int jobsNumber) {
    getOptions().setJobsNumber(jobsNumber);
  }

  @Nullable
  String getTestScriptParameters() {
    return getOptions().getTestScriptParameters();
  }

  void setTestScriptParameters(@Nullable String testScriptParameters) {
    getOptions().setTestScriptParameters(testScriptParameters);
  }

  @NotNull
  List<String> getTestScriptParametersList() {
    String testScriptParameters = getTestScriptParameters();
    return StringUtil.isEmpty(testScriptParameters) ? Collections.emptyList() : StringUtil.split(testScriptParameters, " ");
  }

  protected abstract @NotNull PerlAbstractTestRunConfigurationProducer<? extends PerlAbstractTestRunConfiguration> getRunConfigurationProducer();

  @Override
  public String suggestedName() {
    List<VirtualFile> testsVirtualFiles = computeTargetFiles();
    if (testsVirtualFiles.size() > 1) {
      VirtualFile firstTest = testsVirtualFiles.removeFirst();
      return PerlBundle.message("perl.run.prove.configuration.name.multi", firstTest.getName(), testsVirtualFiles.size());
    }
    else if (testsVirtualFiles.size() == 1) {
      return PerlBundle.message("perl.run.prove.configuration.name.single", testsVirtualFiles.getFirst().getName());
    }
    return super.suggestedName();
  }

  @Override
  public boolean isReconnect() {
    return true;
  }

  @Override
  protected boolean isUsePty() {
    return false;
  }

  protected abstract @NotNull String getFrameworkName();

  @Override
  public @NotNull ConsoleView createConsole(@NotNull PerlRunProfileState runProfileState) throws ExecutionException {
    PerlSMTRunnerConsoleProperties consoleProperties =
      new PerlSMTRunnerConsoleProperties(this, getFrameworkName(), runProfileState.getEnvironment().getExecutor());
    String splitterPropertyName = SMTestRunnerConnectionUtil.getSplitterPropertyName(getFrameworkName());
    var project = getProject();
    SMTRunnerConsoleView consoleView = new PerlSMTRunnerConsoleView(consoleProperties, splitterPropertyName)
      .withHostData(getEffectiveHostData());
    ApplicationManager.getApplication().invokeAndWait(() -> SMTestRunnerConnectionUtil.initConsoleView(consoleView, getFrameworkName()));
    PerlTerminalExecutionConsole.updatePredefinedFiltersLater(project, consoleView);
    return consoleView;
  }

  protected abstract @NotNull List<String> getDefaultTestRunnerArguments();

  @Override
  protected @NotNull PerlCommandLine createBaseCommandLine(@NotNull PerlRunProfileState perlRunProfileState) throws ExecutionException {
    return new PerlCommandLine(getEffectiveInterpreterPath())
      .withParameters(getPerlRunIncludeArguments(getEffectiveHostData(), getProject()))
      .withParameters(getTestRunnerPath())
      .withParameters(getTestRunnerArguments())
      .withParameters(getTestsArguments())
      .withProject(getProject())
      .withSdk(getEffectiveSdk())
      .withEnvironment(computeEnvironment(perlRunProfileState))
      .withParentEnvironmentType(isPassParentEnvs() ? CONSOLE : NONE)      ;
  }

  protected @NotNull Map<String, String> computeEnvironment(@NotNull PerlRunProfileState perlRunProfileState)
    throws ExecutionException {

    // environment
    List<String> perlArgumentsList = new ArrayList<>(getPerlArgumentsList());
    perlArgumentsList.addAll(perlRunProfileState.getAdditionalPerlArguments(this));
    perlArgumentsList.addAll(getPerlRunIncludeArguments(getEffectiveHostData(), getProject()));

    Map<String, String> environment = new HashMap<>(getEnvs());
    environment.putAll(perlRunProfileState.getAdditionalEnvironmentVariables());
    PerlRunUtil.updatePerl5Opt(environment, perlArgumentsList);

    List<String> harnessOptions = new ArrayList<>();
    harnessOptions.add(HARNESS_OPTIONS_JOBS + (perlRunProfileState.isParallelRunAllowed() ? getJobsNumber() : 1));
    harnessOptions.add(HARNESS_OPTIONS_FORMATTER + HARNESS_OPTIONS_FORMATTER_CLASS);

    environment.put(HARNESS_OPTIONS, String.join(HARNESS_OPTIONS_SEPARATOR, harnessOptions));
    var perl5Opt = environment.remove(PERL5OPT);
    if (StringUtil.isNotEmpty(perl5Opt)) {
      environment.put(HARNESS_PERL_SWITCHES, perl5Opt);
    }
    return environment;
  }

  protected @NotNull PerlHostData<?, ?> getEffectiveHostData() throws ExecutionException {
    return PerlHostData.notNullFrom(getEffectiveSdk());
  }

  protected abstract @NotNull String getTestRunnerLocalPath() throws ExecutionException;

  protected @NotNull String getTestRunnerPath() throws ExecutionException {
    PerlHostData<?, ?> perlHostData = getEffectiveHostData();
    String remoteTestRunnerPath = perlHostData.getRemotePath(getTestRunnerLocalPath());
    if (StringUtil.isEmpty(remoteTestRunnerPath)) {
      throw new ExecutionException(PerlBundle.message("dialog.message.unable.to.map.remote.path.for", remoteTestRunnerPath, perlHostData));
    }
    return remoteTestRunnerPath;
  }

  private @NotNull List<String> getTestRunnerArguments() throws ExecutionException {
    List<String> testRunnerArguments = new ArrayList<>(getDefaultTestRunnerArguments());
    testRunnerArguments.addAll(getScriptArguments());
    testRunnerArguments.addAll(getTestsToRunArguments());
    return testRunnerArguments;
  }

  protected @NotNull List<String> getTestsToRunArguments() throws ExecutionException {
    var workingDirectory = computeExplicitWorkingDirectory();
    var perlHostData = getEffectiveHostData();
    List<String> testsPaths = new ArrayList<>();
    for (VirtualFile testVirtualFile : computeTargetFiles()) {
      if (testVirtualFile == null) {
        continue;
      }
      String virtualFilePath = testVirtualFile.getPath();
      String effectivePath;
      if (workingDirectory != null && VirtualFileUtil.isAncestor(workingDirectory, testVirtualFile, true)) {
        effectivePath = VirtualFileUtil.getRelativePath(testVirtualFile, workingDirectory);
      }
      else {
        effectivePath = perlHostData.getRemotePath(virtualFilePath);
      }
      if (effectivePath != null) {
        addTestPathArguments(testsPaths, effectivePath, testVirtualFile);
      }
      else {
        LOG.warn("Unable to compute the effective path");
      }
    }
    return testsPaths;
  }

  @Override
  public @NotNull List<String> getRequiredModulesList() {
    return ContainerUtil.concat(super.getRequiredModulesList(), List.of(PerlPackageUtilCore.TEST_HARNESS_MODULE,
                                PerlPackageUtilCore.TAP_FORMATTER_MODULE));
  }

  protected void addTestPathArguments(@NotNull List<? super String> arguments, @NotNull String testFilePath, @NotNull VirtualFile testVirtualFile) {
    arguments.add(testFilePath);
  }

  protected abstract @NotNull List<String> getTestsArguments();
}
