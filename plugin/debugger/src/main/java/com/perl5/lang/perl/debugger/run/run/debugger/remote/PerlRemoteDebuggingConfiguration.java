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

package com.perl5.lang.perl.debugger.run.run.debugger.remote;

import consulo.execution.executor.Executor;
import consulo.execution.configuration.ConfigurationFactory;
import consulo.execution.configuration.LegacyRunConfigurationConverter;
import consulo.execution.configuration.RunConfigurationBase;
import consulo.execution.debug.DefaultDebugExecutor;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.execution.configuration.RunConfigurationWithSuppressedDefaultRunAction;
import consulo.execution.configuration.RunConfigurationOptions;
import consulo.execution.configuration.fragment.RunConfigurationFragment;
import consulo.util.lang.StringUtil;
import consulo.project.Project;
import com.perl5.lang.perl.idea.run.debugger.PerlDebugOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


// consulo: Consulo RunConfigurationBase is not parameterized with RunConfigurationOptions
public class PerlRemoteDebuggingConfiguration extends RunConfigurationBase
  implements RunConfigurationWithSuppressedDefaultRunAction, PerlDebugOptions {
  private static final String DEFAULT_REMOTE_PROJECT_ROOT = "/home/";
  private static final String DEFAULT_SCRIPT_CHARSET = "utf8";
  private static final String DEFAULT_START_MODE = "RUN";

  // consulo: settings are kept in PerlRemoteDebuggingConfigurationOptions and edited with run configuration fragments

  public PerlRemoteDebuggingConfiguration(Project project, @NotNull ConfigurationFactory factory, String name) {
    super(project, factory, name);
  }

  @Override
  protected Class<? extends RunConfigurationOptions> getOptionsClass() {
    return PerlRemoteDebuggingConfigurationOptions.class;
  }

  @Override
  public PerlRemoteDebuggingConfigurationOptions getOptions() {
    return (PerlRemoteDebuggingConfigurationOptions)super.getOptions();
  }

  @Override
  protected void convertLegacyState(LegacyRunConfigurationConverter converter) {
    super.convertLegacyState(converter);

    PerlRemoteDebuggingConfigurationLegacyState old = converter.readXml(PerlRemoteDebuggingConfigurationLegacyState.class);
    PerlRemoteDebuggingConfigurationOptions options = new PerlRemoteDebuggingConfigurationOptions();
    options.setDebugHost(LOCALHOST.equals(old.debugHost) ? null : old.debugHost);
    options.setDebugPort(old.debugPort);
    options.setRemoteProjectRoot(DEFAULT_REMOTE_PROJECT_ROOT.equals(old.remoteProjectRoot) ? null : old.remoteProjectRoot);
    options.setLocalProjectRoot(old.localProjectRoot);
    options.setScriptCharset(DEFAULT_SCRIPT_CHARSET.equals(old.scriptCharset) ? null : old.scriptCharset);
    options.setPerlRole(ROLE_CLIENT.equals(old.perlRole) ? null : old.perlRole);
    options.setStartMode(DEFAULT_START_MODE.equals(old.startMode) ? null : old.startMode);
    options.setNonInteractiveModeEnabled(old.isNonInteractiveModeEnabled);
    options.setCompileTimeBreakpointsEnabled(old.isCompileTimeBreakpointsEnabled);
    options.setInitCode(old.initCode);
    options.setReconnect(old.reconnect);
    converter.setOptions(options);
  }

  @Override
  public List<RunConfigurationFragment<?>> createFragments() {
    return List.of(new PerlRemoteDebuggingFragment());
  }

  @Override
  public void checkConfiguration() {

  }



  @Override
  public @Nullable PerlRemoteDebuggingRunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment environment) {
    if (executor instanceof DefaultDebugExecutor) {
      return new PerlRemoteDebuggingRunProfileState(environment);
    }
    return null;
  }

  @Override
  public String getHostToBind() {
    return StringUtil.notNullize(getOptions().getDebugHost(), LOCALHOST);
  }

  public void setDebugHost(String debugHost) {
    getOptions().setDebugHost(LOCALHOST.equals(debugHost) ? null : debugHost);
  }

  @Override
  public int getDebugPort() {
    return getOptions().getDebugPort();
  }

  public void setDebugPort(int debugPort) {
    getOptions().setDebugPort(debugPort);
  }

  @Override
  public String getRemoteProjectRoot() {
    return StringUtil.notNullize(getOptions().getRemoteProjectRoot(), DEFAULT_REMOTE_PROJECT_ROOT);
  }

  public void setRemoteProjectRoot(String remoteWorkingDirectory) {
    getOptions().setRemoteProjectRoot(DEFAULT_REMOTE_PROJECT_ROOT.equals(remoteWorkingDirectory) ? null : remoteWorkingDirectory);
  }

  public @NotNull String getLocalProjectRoot() {
    return StringUtil.notNullize(getOptions().getLocalProjectRoot());
  }

  public void setLocalProjectRoot(@NotNull String localProjectRoot) {
    getOptions().setLocalProjectRoot(localProjectRoot);
  }

  @Override
  public String getScriptCharset() {
    return StringUtil.notNullize(getOptions().getScriptCharset(), DEFAULT_SCRIPT_CHARSET);
  }

  @Override
  public void setScriptCharset(String scriptCharset) {
    getOptions().setScriptCharset(DEFAULT_SCRIPT_CHARSET.equals(scriptCharset) ? null : scriptCharset);
  }

  @Override
  public String getStartMode() {
    return StringUtil.notNullize(getOptions().getStartMode(), DEFAULT_START_MODE);
  }

  @Override
  public void setStartMode(String startMode) {
    getOptions().setStartMode(DEFAULT_START_MODE.equals(startMode) ? null : startMode);
  }

  @Override
  public String getPerlRole() {
    return StringUtil.notNullize(getOptions().getPerlRole(), ROLE_CLIENT);
  }

  public void setPerlRole(String perlRole) {
    getOptions().setPerlRole(ROLE_CLIENT.equals(perlRole) ? null : perlRole);
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

  @Override
  public String getInitCode() {
    return StringUtil.notNullize(getOptions().getInitCode());
  }

  @Override
  public void setInitCode(String initCode) {
    getOptions().setInitCode(initCode);
  }

  @Override
  public boolean isReconnect() {
    return getOptions().isReconnect();
  }

  public void setReconnect(boolean reconnect) {
    getOptions().setReconnect(reconnect);
  }
}
