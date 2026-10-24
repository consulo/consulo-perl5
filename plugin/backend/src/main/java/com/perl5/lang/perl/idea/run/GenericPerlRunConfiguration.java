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

package com.perl5.lang.perl.idea.run;

import consulo.execution.CommonProgramRunConfigurationParameters;
import consulo.process.ExecutionException;
import consulo.execution.executor.Executor;

import consulo.process.ProcessHandler;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.execution.configuration.RunConfigurationWithSuppressedDefaultRunAction;
import consulo.execution.ui.console.ConsoleView;
import consulo.application.ApplicationManager;
import consulo.application.ReadAction;
import consulo.logging.Logger;
import consulo.project.Project;
import consulo.content.bundle.Sdk;
import com.intellij.openapi.projectRoots.impl.PerlSdkTable;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import consulo.virtualFileSystem.VirtualFile;
import java.util.function.Function;
import consulo.annotation.access.RequiredReadAction;
import consulo.util.collection.ContainerUtil;
import consulo.process.cmd.ParametersListUtil;
import consulo.util.io.NetUtil;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.idea.execution.PerlCommandLine;
import com.perl5.lang.perl.idea.execution.PerlTerminalExecutionConsole;
import com.perl5.lang.perl.idea.project.PerlProjectManager;
import com.perl5.lang.perl.idea.run.debugger.PerlDebugOptions;
import com.perl5.lang.perl.idea.sdk.host.PerlConsoleView;
import com.perl5.lang.perl.idea.sdk.host.PerlHostData;
import com.perl5.lang.perl.util.PerlFileUtil;
import com.perl5.lang.perl.util.PerlRunUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.*;
import java.util.stream.Collectors;

import static consulo.process.cmd.GeneralCommandLine.ParentEnvironmentType.CONSOLE;
import static consulo.process.cmd.GeneralCommandLine.ParentEnvironmentType.NONE;
import consulo.execution.configuration.ConfigurationFactory;
import consulo.execution.configuration.LegacyRunConfigurationConverter;
import consulo.execution.configuration.fragment.EnvironmentFragmentState;
import consulo.execution.configuration.fragment.ProgramParametersFragmentState;
import consulo.execution.configuration.fragment.RunConfigurationFragment;
import consulo.execution.configuration.fragment.WorkingDirectoryFragmentState;
import consulo.process.cmd.GeneralCommandLine;
import consulo.execution.configuration.LocatableConfigurationBase;
import consulo.execution.configuration.RunProfile;
import consulo.execution.RuntimeConfigurationException;

public abstract class GenericPerlRunConfiguration extends LocatableConfigurationBase
  implements CommonProgramRunConfigurationParameters,
             RunConfigurationWithSuppressedDefaultRunAction,
             PerlDebugOptions {
  public static final Function<String, List<String>> FILES_PARSER = text -> StringUtil.split(text.trim(), "||");
  public static final Function<List<String>, String> FILES_JOINER = strings ->
    StringUtil.join(ContainerUtil.filter(strings, StringUtil::isNotEmpty), "||");
  public static final Function<String, List<String>> PREREQUISITES_PARSER = text -> Arrays.stream(text.trim().split("\\s*,\\s*"))
    .filter( StringUtil::isNotEmpty).sorted().toList() ;
  @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
  public static final Function<List<String>, String> PREREQUISITES_JOINER = strings -> strings.stream()
    .filter(StringUtil::isNotEmpty).map(String::trim).sorted().collect(Collectors.joining(","));

  private static final Logger LOG = Logger.getInstance(GenericPerlRunConfiguration.class);

  private transient Integer myDebugPort;

  public GenericPerlRunConfiguration(@NotNull Project project, @NotNull ConfigurationFactory factory, @Nullable String name) {
    super(project, factory, name);
  }

  @Override
  public GenericPerlRunConfigurationOptions getOptions() {
    return (GenericPerlRunConfigurationOptions)super.getOptions();
  }

  /**
   * consulo: converts the settings an old configuration element wrote with the bean serializer
   */
  protected static void convertLegacyCommonState(@NotNull LegacyRunConfigurationConverter converter,
                                                 @NotNull GenericPerlRunConfigurationOptions options) {
    GenericPerlRunConfigurationLegacyState old = converter.readXml(GenericPerlRunConfigurationLegacyState.class);
    options.setScriptPath(old.scriptPath);
    options.setPerlArguments(old.perlArguments);
    options.setConsoleCharset(old.consoleCharset);
    options.setUseAlternativeSdk(old.useAlternativeSdk);
    options.setAlternativeSdkName(old.alternativeSdkName);
    options.setRequiredModules(old.requiredModules);
    options.setScriptCharset("utf8".equals(old.scriptCharset) ? null : old.scriptCharset);
    options.setStartMode("RUN".equals(old.startMode) ? null : old.startMode);
    options.setNonInteractiveModeEnabled(old.nonInteractiveModeEnabled);
    options.setCompileTimeBreakpointsEnabled(old.compileTimeBreakpointsEnabled);
    options.setInitCode(old.initCode);

    converter.addFragment(new ProgramParametersFragmentState(old.programParameters));
    converter.addFragment(new WorkingDirectoryFragmentState(old.workingDirectory));
    converter.addFragment(new EnvironmentFragmentState(old.envs, old.passParentEnvs));
  }

  /**
   * consulo: fragments shared by all perl configurations, after the script one of the subclass
   */
  protected @NotNull List<RunConfigurationFragment<?>> createCommonFragments() {
    return List.of(
      new PerlArgumentsFragment<>(),
      new PerlConsoleCharsetFragment<>(),
      new PerlRequiredModulesFragment<>(),
      new PerlAlternativeSdkFragment<>(),
      new PerlDebugOptionsFragment<GenericPerlRunConfiguration>()
    );
  }

  @Override
  public final @Nullable PerlRunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment)
    throws ExecutionException {
    var runner = executionEnvironment.getRunner();
    if (!(runner instanceof GenericPerlProgramRunner genericPerlProgramRunner)) {
      LOG.error("GenericPerlProgramRunner expected, got " + runner);
      throw new ExecutionException(
        PerlBundle.message("dialog.message.wrong.runner.used.to.run.perl.configuration.please.report.to.perl.plugin.developers"));
    }
    return genericPerlProgramRunner.createState(executionEnvironment);
  }

  /**
   * @return local path to the perl interpreter for this run configuration
   * @throws ExecutionException with human readable error message if interpreter path is empty
   */
  protected @NotNull String getEffectiveInterpreterPath() throws ExecutionException {
    Sdk effectiveSdk = getEffectiveSdk();
    String interpreterPath = PerlProjectManager.getInterpreterPath(effectiveSdk);
    if (StringUtil.isEmpty(interpreterPath)) {
      throw new ExecutionException(PerlBundle.message("perl.run.error.no.interpreter.path", effectiveSdk));
    }
    return interpreterPath;
  }

  public @NotNull Sdk getEffectiveSdk() throws ExecutionException {
    Sdk perlSdk;
    if (isUseAlternativeSdk()) {
      String alternativeSdkName = getAlternativeSdkName();
      if (StringUtil.isEmpty(alternativeSdkName)) {
        throw new ExecutionException(PerlBundle.message("perl.run.error.no.alternative.sdk.selected"));
      }
      perlSdk = PerlSdkTable.getInstance().findJdk(alternativeSdkName);
      if (perlSdk == null) {
        throw new ExecutionException(PerlBundle.message("perl.run.error.no.alternative.sdk", alternativeSdkName));
      }
      return perlSdk;
    }
    else {
      // consulo: interpreter of the script module (Perl module extension), the project one for scripts outside of modules
      List<VirtualFile> targetFiles = computeTargetFiles();
      perlSdk = PerlProjectManager.getSdk(getProject(), targetFiles.isEmpty() ? null : targetFiles.getFirst());
      if (perlSdk == null) {
        throw new ExecutionException(PerlBundle.message("perl.run.error.no.sdk", getProject().getName()));
      }
    }
    return perlSdk;
  }

  @Override
  public String suggestedName() {
    List<VirtualFile> targetFiles = computeTargetFiles();
    return targetFiles.isEmpty() ? null : targetFiles.getFirst().getName();
  }

  protected @NotNull List<VirtualFile> computeTargetFiles() {
    return computeVirtualFilesFromPaths(getScriptPath());
  }

  private @NotNull VirtualFile computeNonNullScriptFile() throws ExecutionException {
    List<VirtualFile> targetFiles = computeTargetFiles();
    if (targetFiles.isEmpty()) {
      throw new ExecutionException(PerlBundle.message("perl.run.error.script.missing", getScriptPath()));
    }
    return targetFiles.getFirst();
  }

  public @NotNull String getRequiredModules() {
    return StringUtil.notNullize(getOptions().getRequiredModules());
  }

  public @NotNull List<String> getRequiredModulesList() {
    return PREREQUISITES_PARSER.apply(getRequiredModules());
  }

  public void setRequiredModules(@NotNull String requiredModules) {
    getOptions().setRequiredModules(PREREQUISITES_JOINER.apply(PREREQUISITES_PARSER.apply(requiredModules)));
  }

  public String getConsoleCharset() {
    return getOptions().getConsoleCharset();
  }

  public void setConsoleCharset(String charset) {
    getOptions().setConsoleCharset(charset);
  }

  public String getScriptPath() {
    return getOptions().getScriptPath();
  }

  public void setScriptPath(String scriptPath) {
    getOptions().setScriptPath(scriptPath);
  }

  public String getAlternativeSdkName() {
    return getOptions().getAlternativeSdkName();
  }

  public void setAlternativeSdkName(String name) {
    getOptions().setAlternativeSdkName(name);
  }

  public boolean isUseAlternativeSdk() {
    return getOptions().isUseAlternativeSdk();
  }

  public void setUseAlternativeSdk(boolean value) {
    getOptions().setUseAlternativeSdk(value);
  }

  /**
   * @return virtual file for a working directory explicitly set by user
   */
  protected @Nullable VirtualFile computeExplicitWorkingDirectory() {
    String workingDirectory = getWorkingDirectory();
    if (StringUtil.isEmpty(workingDirectory)) {
      return null;
    }
    return VirtualFileUtil.findFileByIoFile(new File(workingDirectory), true);
  }

  public String getPerlArguments() {
    return StringUtil.notNullize(getOptions().getPerlArguments());
  }

  protected @NotNull List<String> getPerlArgumentsList() {
    String perlArguments = getPerlArguments();
    return StringUtil.isEmpty(perlArguments) ? Collections.emptyList() : ParametersListUtil.parse(perlArguments);
  }

  public void setPerlArguments(String perlArguments) {
    getOptions().setPerlArguments(perlArguments);
  }

  @Override
  public String getStartMode() {
    return StringUtil.notNullize(getOptions().getStartMode(), "RUN");
  }

  @Override
  public void setStartMode(String startMode) {
    getOptions().setStartMode("RUN".equals(startMode) ? null : startMode);
  }

  @Override
  public String getPerlRole() {
    return PerlDebugOptions.ROLE_SERVER;
  }

  @Override
  public String getHostToBind() {
    return LOCAL_DEBUG_HOST;
  }

  @Override
  public int getDebugPort() throws ExecutionException {
    if (myDebugPort == null) {
      myDebugPort = NetUtil.tryToFindAvailableSocketPort();
      if (myDebugPort == -1) {
        throw new ExecutionException(PerlBundle.message("dialog.message.no.free.port.to.work.on"));
      }
    }

    return myDebugPort;
  }

  @Override
  public String getRemoteProjectRoot() {
    return getProject().getBasePath();
  }

  @Override
  public String getScriptCharset() {
    return StringUtil.notNullize(getOptions().getScriptCharset(), "utf8");
  }

  @Override
  public void setScriptCharset(String scriptCharset) {
    getOptions().setScriptCharset("utf8".equals(scriptCharset) ? null : scriptCharset);
  }

  @Override
  public boolean isNonInteractiveModeEnabled() {
    return getOptions().isNonInteractiveModeEnabled();
  }

  @Override
  public void setNonInteractiveModeEnabled(boolean nonInteractiveModeEnabled) {
    getOptions().setNonInteractiveModeEnabled(nonInteractiveModeEnabled);
  }

  @Override
  public boolean isCompileTimeBreakpointsEnabled() {
    return getOptions().isCompileTimeBreakpointsEnabled();
  }

  @Override
  public void setCompileTimeBreakpointsEnabled(boolean compileTimeBreakpointsEnabled) {
    getOptions().setCompileTimeBreakpointsEnabled(compileTimeBreakpointsEnabled);
  }

  public @NotNull PerlCommandLine createCommandLine(@NotNull PerlRunProfileState perlRunProfileState) throws ExecutionException {
    PerlCommandLine commandLine = createBaseCommandLine(perlRunProfileState);
    commandLine.withParentEnvironmentType(isPassParentEnvs() ? CONSOLE : NONE);
    commandLine.withWorkDirectory(computeWorkingDirectory(perlRunProfileState.getEnvironment().getProject()));
    commandLine.withCharset(computeCharset());

    return commandLine.withPty(isUsePty());
  }

  protected boolean isUsePty() {
    return true;
  }

  protected @NotNull Charset computeCharset() throws ExecutionException {
    String charsetName = getConsoleCharset();
    if (!StringUtil.isEmpty(charsetName)) {
      try {
        return Charset.forName(charsetName);
      }
      catch (UnsupportedCharsetException e) {
        throw new ExecutionException(PerlBundle.message("perl.run.error.unknown.charset", charsetName));
      }
    }
    else {
      return computeNonNullScriptFile().getCharset();
    }
  }

  /**
   * @see PerlRemoteDebuggingRunProfileState#computeLocalProjectPath(PerlRemoteDebuggingConfiguration)
   */
  protected @Nullable String computeWorkingDirectory(@NotNull Project project) throws ExecutionException {
    String workDirectory = getWorkingDirectory();
    if (StringUtil.isNotEmpty(workDirectory)) {
      return workDirectory;
    }
    return ReadAction.compute(() -> computeWorkingDirectory(project, computeNonNullScriptFile()));
  }

  protected @NotNull PerlCommandLine createBaseCommandLine(@NotNull PerlRunProfileState perlRunProfileState) throws ExecutionException {
    ExecutionEnvironment executionEnvironment = perlRunProfileState.getEnvironment();
    Project project = executionEnvironment.getProject();
    List<String> additionalPerlArguments = perlRunProfileState.getAdditionalPerlArguments(this);
    Map<String, String> additionalEnvironmentVariables = perlRunProfileState.getAdditionalEnvironmentVariables();

    PerlCommandLine commandLine = PerlRunUtil.getPerlCommandLine(
      project, getEffectiveSdk(), computeNonNullScriptFile(), ContainerUtil.concat(getPerlArgumentsList(), additionalPerlArguments),
      getScriptArguments());

    if (commandLine == null) {
      throw new ExecutionException(PerlBundle.message("perl.run.error.sdk.corrupted", getEffectiveSdk()));
    }

    Map<String, String> environment = new HashMap<>(getEnvs());
    environment.putAll(additionalEnvironmentVariables);
    commandLine.withEnvironment(environment);
    return commandLine;
  }

  @RequiredReadAction
  protected static @Nullable String computeWorkingDirectory(@NotNull Project project, @NotNull VirtualFile virtualFile) {
    var fileContentRoot = PerlFileUtil.getContentRoot(project, virtualFile);
    if (fileContentRoot != null) {
      return fileContentRoot.getPath();
    }
    return project.getBasePath();
  }

  protected @NotNull List<String> getScriptArguments() {
    String programArguments = getProgramParameters();
    return StringUtil.isEmpty(programArguments) ? Collections.emptyList() : ParametersListUtil.parse(programArguments);
  }

  @Override
  public String getInitCode() {
    return StringUtil.notNullize(getOptions().getInitCode());
  }

  @Override
  public void setInitCode(String initCode) {
    getOptions().setInitCode(initCode);
  }

  /**
   * @return list of VirtualFiles pointed by  {@code paths} joined with pipe. Reverse of {@link #computePathsFromVirtualFiles(List)}
   */
  public static @NotNull List<VirtualFile> computeVirtualFilesFromPaths(@Nullable String paths) {
    if (StringUtil.isEmpty(paths)) {
      return Collections.emptyList();
    }
    List<String> pathNames = FILES_PARSER.apply(paths);
    if (pathNames.isEmpty()) {
      return Collections.emptyList();
    }
    List<VirtualFile> virtualFiles = new ArrayList<>(pathNames.size());
    for (String pathName : pathNames) {
      if (StringUtil.isEmpty(pathName)) {
        continue;
      }
      ContainerUtil.addIfNotNull(virtualFiles, VirtualFileUtil.findFileByIoFile(new File(pathName), false));
    }
    return virtualFiles;
  }

  /**
   * @return paths of {@code virtualFiles} joined with pipe, reverse of {@link #computeVirtualFilesFromPaths(String)}
   */
  public static @Unmodifiable @NotNull String computePathsFromVirtualFiles(@NotNull List<? extends VirtualFile> virtualFiles) {
    return FILES_JOINER.apply(ContainerUtil.map(virtualFiles, VirtualFile::getPath));
  }

  public @NotNull ConsoleView createConsole(@NotNull PerlRunProfileState runProfileState) throws ExecutionException {
    ExecutionEnvironment executionEnvironment = runProfileState.getEnvironment();
    PerlConsoleView console = ApplicationManager.getApplication().isUnitTestMode() ?
                              new PerlRunConsole(runProfileState.getEnvironment().getProject()) :
                              new PerlTerminalExecutionConsole(executionEnvironment.getProject());
    return console.withHostData(PerlHostData.from(getEffectiveSdk()));
  }

  /**
   * May patch {@code processHandler} created with {@code runProfileState} from current run configuration.
   *
   * @return patched process handler
   */
  protected @NotNull ProcessHandler doPatchProcessHandler(@NotNull ProcessHandler processHandler) {
    return processHandler;
  }

  /**
   * Method checks if specified script(s) path is ok.
   *
   * @throws RuntimeConfigurationException with human-readable error message
   */
  protected void checkConfigurationScriptPath() throws RuntimeConfigurationException {
    if (StringUtil.isEmptyOrSpaces(getScriptPath())) {
      throw new RuntimeConfigurationException(PerlBundle.message("perl.run.error.no.script.set"));
    }
    if (computeTargetFiles().isEmpty()) {
      throw new RuntimeConfigurationException(PerlBundle.message("perl.run.error.no.script.found", getScriptPath()));
    }
  }

  @Override
  public void checkConfiguration() throws RuntimeConfigurationException {
    checkConfigurationScriptPath();
    try {
      getEffectiveInterpreterPath();
    }
    catch (ExecutionException e) {
      throw new RuntimeConfigurationException(e.getMessage());
    }
  }

  /**
   * If profileState was build with {@link GenericPerlRunConfiguration}, patches {@code processHandler} using
   * {@link #doPatchProcessHandler(ProcessHandler)}
   *
   * @return patched process handler
   */
  @Contract("null,_->null; !null,_->!null")
  static @Nullable ProcessHandler patchProcessHandler(@Nullable ProcessHandler processHandler,
                                                      @NotNull PerlRunProfileState runProfileState) {
    if (processHandler == null) {
      return null;
    }
    RunProfile runConfiguration = runProfileState.getEnvironment().getRunProfile();
    return runConfiguration instanceof GenericPerlRunConfiguration perlRunConfiguration ?
      perlRunConfiguration.doPatchProcessHandler(processHandler) :
           processHandler;
  }
}
