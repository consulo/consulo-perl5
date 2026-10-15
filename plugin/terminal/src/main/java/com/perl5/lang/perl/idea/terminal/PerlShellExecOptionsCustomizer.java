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

package com.perl5.lang.perl.idea.terminal;

import consulo.logging.Logger;
import consulo.project.Project;
import consulo.content.bundle.Sdk;
import com.perl5.lang.perl.idea.project.PerlProjectManager;
import com.perl5.lang.perl.idea.sdk.host.PerlHostHandler;
import com.perl5.lang.perl.idea.sdk.versionManager.PerlVersionManagerData;
import com.perl5.lang.perl.util.PerlPluginUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

// TODO consulo: upstream implements org.jetbrains.plugins.terminal.startup.ShellExecOptionsCustomizer (registered as
//  org.jetbrains.plugins.terminal.shellExecOptionsCustomizer EP). Neither the Consulo platform terminal
//  (consulo.execution.terminal.TerminalSessionFactory, LocalTerminalDirectRunner) nor consulo-terminal plugin has an extension point
//  to customize the shell environment, so the class is not registered and MutableShellExecOptions is replaced with the mutable
//  environment map of the shell process
public class PerlShellExecOptionsCustomizer {
  private static final Logger LOG = Logger.getInstance(PerlShellExecOptionsCustomizer.class);

  public void customizeExecOptions(@NotNull Project project,
                                   @NotNull Map<String, String> shellExecOptions) {
    Sdk perlSdk = PerlProjectManager.getSdk(project);
    if (perlSdk == null) {
      LOG.debug("No perl sdk set for the project ", project.getName());
      return;
    }

    PerlHostHandler<?, ?> hostHandler = PerlHostHandler.from(perlSdk);
    if (hostHandler == null || !hostHandler.isLocal()) {
      LOG.debug("Non-local or missing host handler ", hostHandler, " in ", perlSdk);
      return;
    }

    PerlVersionManagerData<?, ?> versionManagerData = PerlVersionManagerData.from(perlSdk);
    if (versionManagerData == null) {
      LOG.debug("Missing version manager data in ", perlSdk);
      return;
    }

    String customizerScriptName = versionManagerData.getTerminalCustomizerScriptName();
    if (customizerScriptName == null) {
      LOG.debug("Version manager does not support terminal customization: ", perlSdk);
      return;
    }

    String customizerScriptPath = PerlPluginUtil.getHelperPath(customizerScriptName);

    setEnvironmentVariable(shellExecOptions, "JEDITERM_SOURCE", customizerScriptPath);
    setEnvironmentVariable(shellExecOptions, "PERL_VERSION_MANAGER_PATH", versionManagerData.getVersionManagerPath());
    setEnvironmentVariable(shellExecOptions, "PERL_VERSION_MANAGER_DISTRIBUTION_ID", versionManagerData.getDistributionId());
  }

  /**
   * consulo: replacement for {@code MutableShellExecOptions#setEnvironmentVariable}, {@code null} value removes the variable
   */
  private static void setEnvironmentVariable(@NotNull Map<String, String> envs, @NotNull String name, @Nullable String value) {
    if (value == null) {
      envs.remove(name);
    }
    else {
      envs.put(name, value);
    }
  }
}
