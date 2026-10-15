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

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.execution.coverage.SimpleCoverageAnnotator;
import consulo.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.io.File;

// consulo: upstream <projectService> registration in perl5.plugin.coverage.main.xml
@ServiceAPI(ComponentScope.PROJECT)
@ServiceImpl
@Singleton
public class PerlCoverageAnnotator extends SimpleCoverageAnnotator {
  @Inject
  public PerlCoverageAnnotator(Project project) {
    super(project);
  }

  public static @NotNull PerlCoverageAnnotator getInstance(@NotNull Project project) {
    return project.getService(PerlCoverageAnnotator.class);
  }

  // consulo: Consulo SimpleCoverageAnnotator#fillInfoForUncoveredFile accepts a File
  @Override
  protected @Nullable FileCoverageInfo fillInfoForUncoveredFile(@NotNull File file) {
    return new FileCoverageInfo();
  }

  @Override
  protected @Nullable String getLinesCoverageInformationString(@NotNull FileCoverageInfo info) {
    if (info.totalLineCount == 0) {
      return null;
    }
    if (info.coveredLineCount == 0) {
      return info instanceof DirCoverageInfo ? null : PerlCoverageBundle.message("no.lines.covered");
    }
    return PerlCoverageBundle.message("0.lines.covered", (int)((double)info.coveredLineCount * 100. / (double)info.totalLineCount));
  }

  @Override
  protected @Nullable String getFilesCoverageInformationString(@NotNull DirCoverageInfo info) {
    if (info.totalFilesCount == 0 || info.coveredFilesCount == 0) {
      return null;
    }
    return PerlCoverageBundle.message("0.of.1.files", info.coveredFilesCount, info.totalFilesCount);
  }
}
