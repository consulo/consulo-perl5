/*
 * Copyright 2015-2024 Alexandr Evstigneev
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

package com.perl5.lang.perl.idea.execution;

import com.perl5.lang.perl.idea.project.PerlProjectManager;
import com.perl5.lang.perl.idea.sdk.host.PerlHostData;
import com.perl5.lang.perl.idea.sdk.versionManager.PerlVersionManagerData;
import consulo.content.bundle.Sdk;
import consulo.logging.Logger;
import consulo.module.Module;
import consulo.process.ExecutionException;
import consulo.process.cmd.GeneralCommandLine;
import consulo.process.cmd.ParametersList;
import consulo.process.event.ProcessListener;
import consulo.project.Project;
import consulo.ui.image.Image;
import consulo.util.collection.ContainerUtil;
import consulo.util.lang.ObjectUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.nio.charset.Charset;
import java.util.*;

import static com.perl5.lang.perl.util.PerlUtil.mutableList;

public class PerlCommandLine extends GeneralCommandLine {
  private static final Logger LOG = Logger.getInstance(PerlCommandLine.class);
  private @Nullable Sdk mySdk;

  private @Nullable PerlHostData<?, ?> myHostData;

  private @Nullable PerlVersionManagerData<?, ?> myVersionManagerData;

  private boolean myUsePty = false;

  private @Nullable Project myProject;

  private @Nullable Module myModule;

  private @Nullable @Nls String myConsoleTitle;

  private @Nullable Image myConsoleIcon;

  private @NotNull List<ProcessListener> myProcessListeners = Collections.emptyList();

  private @NotNull Set<PortMapping> myPortMappings = Collections.emptySet();

  private boolean myWithMissingPackageListener = true;

  /**
   * Indicates that command line is user command line, not some internal one. E.g. command line from run configurations
   */
  private boolean myIsUserCommandLine = false;

  public PerlCommandLine() {
  }

  public PerlCommandLine(@NotNull String... command) {
    super(command);
  }

  public PerlCommandLine(@NotNull GeneralCommandLine original) {
    super(original);
    if (original instanceof PerlCommandLine perlCommandLine) {
      mySdk = perlCommandLine.mySdk;
      myHostData = perlCommandLine.myHostData;
      myVersionManagerData = perlCommandLine.myVersionManagerData;
      myUsePty = perlCommandLine.myUsePty;
      myConsoleTitle = perlCommandLine.myConsoleTitle;
      myConsoleIcon = perlCommandLine.myConsoleIcon;
      myProcessListeners = new ArrayList<>(perlCommandLine.myProcessListeners);
      myProject = perlCommandLine.myProject;
      myModule = perlCommandLine.myModule;
      myIsUserCommandLine = perlCommandLine.myIsUserCommandLine;
    }
  }

  /**
   * @return true iff we should add listener for CPAN error messages about missing packages. Enabled by default.
   */
  public boolean isWithMissingPackageListener() {
    return myWithMissingPackageListener;
  }

  /**
   * @see #isWithMissingPackageListener()
   */
  public @NotNull PerlCommandLine withMissingPackageListener(boolean withMissingPackageListener) {
    myWithMissingPackageListener = withMissingPackageListener;
    return this;
  }

  @Override
  public @NotNull PerlCommandLine withEnvironment(@Nullable Map<String, String> environment) {
    return (PerlCommandLine)super.withEnvironment(environment);
  }

  @Override
  public @NotNull PerlCommandLine withParentEnvironmentType(@NotNull ParentEnvironmentType type) {
    return (PerlCommandLine)super.withParentEnvironmentType(type);
  }

  @Override
  public @NotNull PerlCommandLine withEnvironment(@NotNull String key, @NotNull String value) {
    return (PerlCommandLine)super.withEnvironment(key, value);
  }

  @Override
  public @NotNull PerlCommandLine withParameters(String @NotNull ... parameters) {
    return (PerlCommandLine)super.withParameters(parameters);
  }

  public @NotNull PerlCommandLine withParameters(@NotNull Set<String> parameters) {
    return withParameters(new ArrayList<>(parameters));
  }

  @Override
  public @NotNull PerlCommandLine withWorkDirectory(@Nullable String path) {
    return (PerlCommandLine)super.withWorkDirectory(path);
  }

  @Override
  public @NotNull PerlCommandLine withWorkDirectory(@Nullable File workDirectory) {
    return (PerlCommandLine)super.withWorkDirectory(workDirectory);
  }

  @Override
  public @NotNull PerlCommandLine withParameters(@NotNull List<String> parameters) {
    return (PerlCommandLine)super.withParameters(parameters);
  }

  public @Nullable Sdk getEffectiveSdk() {
    return mySdk != null ? mySdk : PerlProjectManager.getSdk(getEffectiveProject());
  }

  public @NotNull PerlCommandLine withSdk(@Nullable Sdk sdk) {
    mySdk = sdk;
    return this;
  }

  /**
   * @return explicit data or data from sdk if any, null otherwise
   */
  public @Nullable PerlHostData<?, ?> getEffectiveHostData() {
    return myHostData == null ? PerlHostData.from(getEffectiveSdk()) : myHostData;
  }

  public @NotNull PerlCommandLine withHostData(@Nullable PerlHostData<?, ?> hostData) {
    myHostData = hostData;
    return this;
  }

  /**
   * @return explicit data or data from sdk if any, null otherwise
   */
  public @Nullable PerlVersionManagerData<?, ?> getEffectiveVersionManagerData() {
    return myVersionManagerData == null ? PerlVersionManagerData.from(getEffectiveSdk()) : myVersionManagerData;
  }

  public @NotNull PerlCommandLine withVersionManagerData(@Nullable PerlVersionManagerData<?, ?> versionManagerData) {
    myVersionManagerData = versionManagerData;
    return this;
  }

  public @Nullable @Nls String getConsoleTitle() {
    return myConsoleTitle;
  }

  public @NotNull PerlCommandLine withConsoleTitle(@Nullable @Nls String consoleTitle) {
    myConsoleTitle = consoleTitle;
    return this;
  }

  public @NotNull PerlCommandLine prependLineWith(@NotNull String... commands) {
    List<String> commandsList = mutableList(commands);
    commandsList.add(getExePath());
    setExePath(commandsList.removeFirst());
    if (!commandsList.isEmpty()) {
      ParametersList parametersList = getParametersList();
      ContainerUtil.reverse(commandsList).forEach(it -> parametersList.addAt(0, it));
    }
    return this;
  }

  public @NotNull List<ProcessListener> getProcessListeners() {
    return Collections.unmodifiableList(myProcessListeners);
  }

  public @NotNull PerlCommandLine withProcessListener(@NotNull ProcessListener... listeners) {
    myProcessListeners = new ArrayList<>(myProcessListeners);
    myProcessListeners.addAll(Arrays.asList(listeners));
    return this;
  }

  @Override
  public @NotNull PerlCommandLine withCharset(@NotNull Charset charset) {
    return (PerlCommandLine)super.withCharset(charset);
  }

  public boolean isUsePty() {
    return myUsePty;
  }

  public @NotNull PerlCommandLine withPty(boolean usePty) {
    myUsePty = usePty;
    return this;
  }

  public @Nullable Project getEffectiveProject() {
    return myProject != null ? myProject : ObjectUtil.doIfNotNull(getModule(), Module::getProject);
  }

  public @NotNull Project getNonNullEffectiveProject() {
    return Objects.requireNonNull(getEffectiveProject());
  }

  public @NotNull PerlCommandLine withProject(@Nullable Project project) {
    myProject = project;
    return this;
  }

  public @Nullable Module getModule() {
    return myModule;
  }

  public @NotNull PerlCommandLine withModule(@Nullable Module module) {
    myModule = module;
    return this;
  }

  public @Nullable Image getConsoleIcon() {
    return myConsoleIcon;
  }

  public @NotNull PerlCommandLine withConsoleIcon(@Nullable Image consoleIcon) {
    myConsoleIcon = consoleIcon;
    return this;
  }

  public @NotNull Set<PortMapping> getPortMappings() {
    return Collections.unmodifiableSet(myPortMappings);
  }

  /**
   * Appends mappings of ports
   */
  public @NotNull PerlCommandLine withPortMappings(PortMapping... mappings) {
    Set<PortMapping> newMappings = new HashSet<>(Set.of(mappings));
    newMappings.addAll(myPortMappings);
    myPortMappings = newMappings;
    return this;
  }

  /**
   * @see #myIsUserCommandLine
   */
  public boolean isUserCommandLine() {
    return myIsUserCommandLine;
  }

  /**
   * @see #myIsUserCommandLine
   */
  public PerlCommandLine withUserCommandLine(boolean userCommandLine) {
    myIsUserCommandLine = userCommandLine;
    return this;
  }

  @Override
  public @NotNull Process createProcess() throws ExecutionException {
    LOG.info("Executing: " + getCommandLineString());
    LOG.info("  environment: " + getEnvironment() + " (+" + getParentEnvironmentType() + ")");
    LOG.info("  working dir: " + getWorkDirectory());
    PerlHostData<?, ?> hostData = getEffectiveHostData();
    LOG.info("  host = " + hostData +
             "; vm = " + getEffectiveVersionManagerData() +
             "; pty = " + isUsePty() +
             "; charset: " + getCharset());
    if (!myUsePty) {
      return super.createProcess();
    }

    PtyCommandLine ptyCommandLine = new PtyCommandLine(this).withConsoleMode(false).withInitialColumns(256);
    if (hostData == null || !hostData.getOsHandler().isMsWindows()) {
      ptyCommandLine.withEnvironment("TERM", "xterm-256color");
    }

    return ptyCommandLine.createProcess();
  }
}
