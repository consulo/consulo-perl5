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

import consulo.annotation.component.ExtensionImpl;
import consulo.application.Application;
import consulo.execution.coverage.CoverageViewManager;
import consulo.execution.coverage.view.CoverageViewExtension;
import consulo.execution.coverage.view.DirectoryCoverageViewExtension;
import consulo.execution.configuration.RunConfigurationBase;
import consulo.execution.coverage.CoverageEnabledConfiguration;
import consulo.execution.test.AbstractTestProxy;
import consulo.module.Module;
import consulo.project.Project;
import consulo.util.io.FileUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import com.perl5.lang.perl.fileTypes.PurePerlFileType;
import com.perl5.lang.perl.idea.run.GenericPerlRunConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import consulo.execution.coverage.CoverageAnnotator;
import consulo.execution.coverage.CoverageEngine;
import consulo.execution.coverage.CoverageFileProvider;
import consulo.execution.coverage.CoverageRunner;
import consulo.execution.coverage.CoverageSuite;
import consulo.execution.coverage.CoverageSuitesBundle;

// consulo: upstream <coverageEngine> registration in perl5.plugin.coverage.main.xml
@ExtensionImpl
public class PerlCoverageEngine extends CoverageEngine {

  @Override
  public boolean isApplicableTo(@Nullable RunConfigurationBase conf) {
    return conf instanceof GenericPerlRunConfiguration;
  }

  @Override
  public boolean canHavePerTestCoverage(@Nullable RunConfigurationBase conf) {
    return false;
  }

  @Override
  public @NotNull CoverageEnabledConfiguration createCoverageEnabledConfiguration(@Nullable RunConfigurationBase conf) {
    return new PerlCoverageEnabledConfiguration(conf);
  }

  // consulo: not an override, Consulo CoverageEngine has no createCoverageSuite(CoverageEnabledConfiguration);
  // Consulo CoverageDataManager calls createCoverageSuite(runner, name, fileProvider, config), which delegates here
  public @Nullable CoverageSuite createCoverageSuite(@NotNull CoverageEnabledConfiguration config) {
    if (config instanceof PerlCoverageEnabledConfiguration perlConfig) {
      return new PerlCoverageSuite(perlConfig);
    }
    return null;
  }

  @SuppressWarnings("deprecation")
  @Override
  public @Nullable CoverageSuite createCoverageSuite(@NotNull CoverageRunner runner,
                                                     @NotNull String name,
                                                     @NotNull CoverageFileProvider fileProvider,
                                                     String @Nullable [] filters,
                                                     long lastCoverageTimeStamp,
                                                     @Nullable String suiteToMerge,
                                                     boolean coverageByTestEnabled,
                                                     boolean branchCoverage,
                                                     boolean trackTestFolders,
                                                     Project project) {
    throw new UnsupportedOperationException("Use createCoverageSuite(CoverageEnabledConfiguration)");
  }

  @SuppressWarnings("deprecation")
  @Override
  public @Nullable CoverageSuite createCoverageSuite(@NotNull CoverageRunner covRunner,
                                                     @NotNull String name,
                                                     @NotNull CoverageFileProvider coverageDataFileProvider,
                                                     @NotNull CoverageEnabledConfiguration config) {
    // consulo: this is the suite factory method used by Consulo CoverageDataManager (upstream throws UnsupportedOperationException
    // here and uses createCoverageSuite(CoverageEnabledConfiguration)); name and file provider are computed by the suite itself
    return createCoverageSuite(config);
  }

  @Override
  public @Nullable CoverageSuite createEmptyCoverageSuite(@NotNull CoverageRunner coverageRunner) {
    return new PerlCoverageSuite();
  }

  @Override
  public @NotNull CoverageAnnotator getCoverageAnnotator(Project project) {
    return PerlCoverageAnnotator.getInstance(project);
  }

  @Override
  public boolean coverageEditorHighlightingApplicableTo(@NotNull PsiFile psiFile) {
    return isOurFile(psiFile.getVirtualFile());
  }

  @Override
  public boolean coverageProjectViewStatisticsApplicableTo(VirtualFile fileOrDir) {
    return isOurFile(fileOrDir);
  }

  // consulo: Consulo passes the coverage view state to the view extension
  @Override
  public CoverageViewExtension createCoverageViewExtension(Project project,
                                                           CoverageSuitesBundle suiteBundle,
                                                           CoverageViewManager.StateBean stateBean) {
    return new DirectoryCoverageViewExtension(project, getCoverageAnnotator(project), suiteBundle, stateBean);
  }

  // consulo: IntelliJ CoverageEngine#acceptedByFilters default implementation, abstract in Consulo
  @Override
  public boolean acceptedByFilters(@NotNull PsiFile psiFile, @NotNull CoverageSuitesBundle suite) {
    return true;
  }

  @Override
  public boolean recompileProjectAndRerunAction(@NotNull Module module,
                                                @NotNull CoverageSuitesBundle suite,
                                                @NotNull Runnable chooseSuiteAction) {
    return false;
  }

  @Override
  public @NotNull Set<String> getQualifiedNames(@NotNull PsiFile sourceFile) {
    return Collections.singleton(buildQualifiedName(sourceFile));
  }

  // consulo: Consulo CoverageEngine#getQualifiedName is public and accepts a File
  @Override
  public @Nullable String getQualifiedName(@NotNull File outputFile, @NotNull PsiFile sourceFile) {
    return buildQualifiedName(sourceFile);
  }

  private static String buildQualifiedName(@NotNull PsiFile sourceFile) {
    return FileUtil.toSystemIndependentName(sourceFile.getVirtualFile().getPath());
  }

  @Override
  public List<PsiElement> findTestsByNames(String @NotNull [] testNames, @NotNull Project project) {
    return Collections.emptyList();
  }

  @Override
  public @Nullable String getTestMethodName(@NotNull PsiElement element, @NotNull AbstractTestProxy testProxy) {
    return null;
  }

  @Override
  public String getPresentableText() {
    return "PerlCoverage";
  }

  public static PerlCoverageEngine getInstance() {
    return Application.get().getExtensionPoint(CoverageEngine.class).findExtension(PerlCoverageEngine.class);
  }

  private static boolean isOurFile(@Nullable VirtualFile virtualFile) {
    return virtualFile != null && virtualFile.getFileType() instanceof PurePerlFileType;
  }
}
