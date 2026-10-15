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

package com.perl5.lang.mojolicious.idea.actions;

import consulo.process.ExecutionException;
import consulo.process.event.ProcessEvent;
import consulo.process.event.ProcessListener;
import consulo.ui.ex.action.AnActionEvent;
import consulo.language.editor.CommonDataKeys;
import consulo.logging.Logger;
import consulo.project.Project;
import consulo.module.content.ProjectFileIndex;
import consulo.ui.ex.InputValidator;
import consulo.ui.InputBoxBuilder;
import consulo.ui.InputProblem;
import consulo.ui.TextBox;
import consulo.localize.LocalizeValue;
import consulo.content.bundle.Sdk;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.application.util.Semaphore;
import consulo.ui.ex.awtUnsafe.TargetAWT;
import consulo.ui.image.Image;
import com.perl5.lang.mojolicious.MojoIcons;
import com.perl5.lang.mojolicious.MojoUtil;
import com.perl5.lang.perl.idea.execution.PerlCommandLine;
import com.perl5.lang.perl.idea.project.PerlProjectManager;
import com.perl5.lang.perl.idea.sdk.host.PerlHostData;
import com.perl5.lang.perl.util.PerlRunUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class MojoGenerateAction extends MojoScriptAction {
  private static final Logger LOG = Logger.getInstance(MojoGenerateAction.class);
  private static final String GENERATE_COMMAND = "generate";
  private Semaphore myCallbackTestSemaphore = null;

  public MojoGenerateAction(@Nls(capitalization = Nls.Capitalization.Title) @Nullable String text) {
    super(text);
  }

  public MojoGenerateAction(@Nls(capitalization = Nls.Capitalization.Title) @Nullable String text, @Nullable Image icon) {
    super(text, icon);
  }

  @Override
  protected boolean isEnabled(@NotNull AnActionEvent event) {
    if (!super.isEnabled(event)) {
      return false;
    }
    return getTargetDirectory(event) != null;
  }

  @Override
  public final void actionPerformed(@NotNull AnActionEvent e) {
    var project = e.getData(Project.KEY);
    var perlSdk = PerlProjectManager.getSdk(e);
    VirtualFile mojoScript = MojoUtil.getMojoScript(e);
    if (mojoScript == null) {
      LOG.warn("No mojo script; project: " + project + " sdk: " + perlSdk);
      return;
    }
    // consulo: the generation parameters are asked asynchronously, the generation continues when they arrive; the target
    // directory and the test semaphore are taken before asking
    VirtualFile targetDirectory = getTargetDirectory(e);
    var callbackTestSemaphore = myCallbackTestSemaphore;
    computeGenerationParameters(e).whenComplete((generationParameters, error) -> {
      if (generationParameters == null) {
        return;
      }
      generate(project, perlSdk, mojoScript, targetDirectory, callbackTestSemaphore, generationParameters);
    });
  }

  private static void generate(@Nullable Project project,
                               @Nullable Sdk perlSdk,
                               @NotNull VirtualFile mojoScript,
                               @Nullable VirtualFile targetDirectory,
                               @Nullable Semaphore callbackTestSemaphore,
                               @NotNull List<String> generationParameters) {
    var localScriptPath = mojoScript.getPath();
    if (perlSdk == null) {
      LOG.warn("No perl sdk for " + project);
      return;
    }
    PerlHostData<?, ?> hostData = PerlHostData.notNullFrom(perlSdk);
    var remoteScriptPath = hostData.getRemotePath(localScriptPath);
    if (targetDirectory == null) {
      LOG.debug("No virtual file in action event");
      return;
    }
    var targetDirectoryPath = targetDirectory.getPath();

    var processListener = new ProcessListener() {
      @Override
      public void processTerminated(@NotNull ProcessEvent event) {
        targetDirectory.refresh(true, false);
        try {
          hostData.fixPermissionsRecursively(targetDirectoryPath, project);
        }
        catch (ExecutionException ex) {
          LOG.warn("Error fixing permissions for " + targetDirectoryPath + ": " + ex.getMessage());
        }
        finally {
          if (callbackTestSemaphore != null) {
            callbackTestSemaphore.up();
          }
        }
      }
    };
    var perlCommandLine = new PerlCommandLine(remoteScriptPath)
      .withParameters(GENERATE_COMMAND)
      .withParameters(generationParameters)
      .withProject(project)
      // consulo: PerlCommandLine keeps a Swing icon for the console
      .withConsoleIcon(TargetAWT.to(MojoIcons.MOJO_LOGO))
      .withWorkDirectory(targetDirectoryPath)
      .withProcessListener(processListener);

    if (project != null) {
      PerlRunUtil.updatePerl5Opt(perlCommandLine.getEnvironment(), PerlRunUtil.getPerlRunIncludeArguments(hostData, project));
    }

    PerlRunUtil.runInConsole(perlCommandLine);
  }

  /**
   * Should return generation parameters or null if generation should not be performed
   * <p>
   * consulo: Messages are Swing dialogs (not available in web), the unified input box is asynchronous, so are the parameters
   */
  protected @NotNull CompletableFuture<@Nullable List<String>> computeGenerationParameters(@NotNull AnActionEvent e) {
    InputValidator nameValidator = getNameValidator();
    InputBoxBuilder<String, TextBox> inputBox = InputBoxBuilder.text()
      .title(LocalizeValue.of(getPromptTitle()))
      .text(LocalizeValue.of(getPromptMessage()))
      .icon(getPromptIcon())
      .value(getDefaultName());
    if (nameValidator != null) {
      // upstream checkInput disables the confirmation, canClose keeps the dialog open
      inputBox = inputBox.validator(
        value -> nameValidator.checkInput(value) && nameValidator.canClose(value) ? null : InputProblem.error(LocalizeValue.empty()));
    }
    return inputBox.showAsync(e.getData(Project.KEY))
      .handle((entityName, error) -> StringUtil.isEmpty(entityName) ? null : Arrays.asList(getGenerateCommand(), entityName));
  }

  protected abstract @NotNull String getDefaultName();

  protected abstract @Nullable InputValidator getNameValidator();

  /**
   * @return icon for name prompt dialog
   */
  protected abstract @NotNull Image getPromptIcon();

  /**
   * @return title for name prompt dialog
   */
  protected abstract @NotNull String getPromptTitle();

  /**
   * @return message for name prompt dialog
   */
  protected abstract @NotNull String getPromptMessage();

  /**
   * @return a command that should be used to generate entity
   */
  protected abstract @NotNull String getGenerateCommand();

  protected @Nullable VirtualFile getTargetDirectory(@NotNull AnActionEvent event) {
    Project project = event.getData(Project.KEY);
    VirtualFile virtualFile = event.getData(CommonDataKeys.VIRTUAL_FILE);
    return virtualFile != null && project != null &&
           virtualFile.isDirectory() &&
           ProjectFileIndex.getInstance(project).isInContent(virtualFile) ? virtualFile : null;
  }

  @TestOnly
  public Semaphore runWithTestSemaphore(@NotNull Runnable runnable) {
    LOG.assertTrue(myCallbackTestSemaphore == null);
    var semaphore = new Semaphore(1);
    myCallbackTestSemaphore = semaphore;
    try {
      runnable.run();
    }
    finally {
      myCallbackTestSemaphore = null;
    }
    return semaphore;
  }
}
