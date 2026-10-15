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

package com.perl5.lang.perl.coverage;

import consulo.execution.coverage.CoverageHelper;
import consulo.process.ExecutionException;
import consulo.execution.coverage.CoverageEnabledConfiguration;
import consulo.execution.runner.ExecutionEnvironment;
import consulo.application.ApplicationManager;
import consulo.logging.Logger;
import consulo.content.bundle.Sdk;
import consulo.util.io.FileUtil;
import com.perl5.lang.perl.idea.run.GenericPerlRunConfiguration;
import com.perl5.lang.perl.idea.run.PerlRunProfileState;
import com.perl5.lang.perl.idea.sdk.host.PerlHostData;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class PerlCoverageProfileState extends PerlRunProfileState {
  private static final Logger LOG = Logger.getInstance(PerlCoverageProfileState.class);
  public PerlCoverageProfileState(ExecutionEnvironment environment) {
    super(environment);
  }

  @Override
  public @NotNull List<String> getAdditionalPerlArguments(@NotNull GenericPerlRunConfiguration perlRunConfiguration)
    throws ExecutionException {
    CoverageHelper.resetCoverageSuit(perlRunConfiguration);

    String coverageBasePath =
      CoverageEnabledConfiguration.getOrCreate((GenericPerlRunConfiguration)getEnvironment().getRunProfile()).getCoverageFilePath();

    if (coverageBasePath == null) {
      throw new ExecutionException(
        PerlCoverageBundle.message("dialog.message.unable.to.create.coverage.directory.for", perlRunConfiguration));
    }

    File coverageDir = new File(coverageBasePath);
    coverageDir.mkdirs();
    LOG.debug("Coverage directory created: ", coverageDir);

    Sdk effectiveSdk = perlRunConfiguration.getEffectiveSdk();
    PerlHostData<?, ?> hostData = PerlHostData.notNullFrom(effectiveSdk);
    var remoteDataPath = FileUtil.toSystemIndependentName(hostData.getRemotePath(coverageBasePath));
    if (ApplicationManager.getApplication().isUnitTestMode()) {
      return Collections.singletonList("-MDevel::Cover=-db," + remoteDataPath + ",-dir,.,-blib,0");
    }
    return Collections.singletonList("-MDevel::Cover=-silent,1,-db," + remoteDataPath + ",-dir,.,-blib,0");
  }

  @Override
  public boolean isParallelRunAllowed() {
    return false;
  }
}
