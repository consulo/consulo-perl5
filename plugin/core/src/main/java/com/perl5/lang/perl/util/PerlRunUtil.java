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

package com.perl5.lang.perl.util;

import com.perl5.PerlBundle;
import com.perl5.PerlIcons;
import com.perl5.lang.perl.adapters.PackageManagerAdapter;
import com.perl5.lang.perl.idea.actions.PerlDumbAwareAction;
import com.perl5.lang.perl.idea.execution.PerlCommandLine;
import com.perl5.lang.perl.idea.execution.PerlTerminalExecutionConsole;
import com.perl5.lang.perl.idea.project.PerlProjectManager;
import com.perl5.lang.perl.idea.run.PerlRunConsole;
import com.perl5.lang.perl.idea.sdk.PerlSdkType;
import com.perl5.lang.perl.idea.sdk.host.PerlConsoleView;
import com.perl5.lang.perl.idea.sdk.host.PerlHostData;
import com.perl5.lang.perl.idea.sdk.host.os.PerlOsHandler;
import com.perl5.lang.perl.idea.sdk.versionManager.PerlVersionManagerData;
import consulo.application.ApplicationManager;
import consulo.application.ReadAction;
import consulo.application.WriteAction;
import consulo.application.progress.ProgressIndicator;
import consulo.application.progress.ProgressManager;
import consulo.application.progress.Task;
import consulo.application.util.Semaphore;
import consulo.content.bundle.Sdk;
import consulo.disposer.Disposable;
import consulo.disposer.Disposer;
import consulo.execution.executor.DefaultRunExecutor;
import consulo.execution.executor.Executor;
import consulo.execution.ui.RunContentDescriptor;
import consulo.execution.ui.RunContentManager;
import consulo.execution.ui.console.ConsoleViewContentType;
import consulo.language.editor.DaemonCodeAnalyzer;
import consulo.logging.Logger;
import consulo.module.content.internal.ProjectRootManagerEx;
import consulo.process.ExecutionException;
import consulo.process.ProcessHandler;
import consulo.process.event.ProcessAdapter;
import consulo.process.event.ProcessEvent;
import consulo.process.event.ProcessListener;
import consulo.project.Project;
import consulo.project.ui.notification.Notification;
import consulo.project.ui.notification.NotificationType;
import consulo.project.ui.notification.Notifications;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;
import consulo.util.collection.ContainerUtil;
import consulo.util.dataholder.Key;
import consulo.util.lang.EmptyRunnable;
import consulo.util.lang.ObjectUtil;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import org.jetbrains.annotations.*;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;


public final class PerlRunUtil {
  public static final String PERL_I = "-I";
  public static final String PERL_LE = "-le";
  public static final String PERL_CTRL_X = "print eval chr(0x24).q{^X}";
  public static final String PERL5OPT = "PERL5OPT";
  private static final Logger LOG = Logger.getInstance(PerlRunUtil.class);
  private static final String MISSING_MODULE_PREFIX = "(you may need to install the ";
  private static final String MISSING_MODULE_SUFFIX = " module)";
  private static final String LEGACY_MODULE_PREFIX = "Can't locate ";
  private static final String LEGACY_MODULE_SUFFIX = " in @INC";
  public static final String BUNDLED_MODULE_NAME = "Bundle::Camelcade";
  private static final List<RunContentDescriptor> TEST_CONSOLE_DESCRIPTORS = new ArrayList<>();
  private static Semaphore ourTestSdkRefreshSemaphore;
  private static Disposable ourTestDisposable;
  // should be synchronized with https://github.com/Camelcade/Bundle-Camelcade/blob/master/dist.ini
  private static final Set<String> BUNDLED_MODULE_PARTS = Collections.unmodifiableSet(Set.of(
    "App::cpanminus",
    "App::Prove::Plugin::PassEnv",
    "B::Deparse",
    "Config",
    PerlPackageUtil.COVERAGE_MODULE,
    PerlPackageUtil.DEBUGGER_MODULE,
    PerlPackageUtil.PROFILER_MODULE,
    "File::Find",
    PerlPackageUtil.JSON_MODULE,
    "Perl::Critic",
    "Perl::Tidy",
    PerlPackageUtil.TAP_FORMATTER_MODULE,
    PerlPackageUtil.TEST_HARNESS_MODULE
  ));

  private PerlRunUtil() {
  }

  /**
   * Builds non-patched perl command line for {@code project}'s sdk (without patching by version manager)
   *
   * @return command line if perl support for project or scriptFile is enabled
   */
  public static @Nullable PerlCommandLine getPerlCommandLine(@NotNull Project project,
                                                             @Nullable VirtualFile scriptFile,
                                                             String... perlParameters) {
    return getPerlCommandLine(
      project, PerlProjectManager.getSdk(project, scriptFile), scriptFile, Arrays.asList(perlParameters), Collections.emptyList());
  }

  public static @Nullable PerlCommandLine getPerlCommandLine(@NotNull Project project,
                                                             @Nullable Sdk perlSdk,
                                                             @Nullable VirtualFile scriptFile,
                                                             @NotNull List<String> perlParameters,
                                                             @NotNull List<String> scriptParameters) {
    if (perlSdk == null) {
      perlSdk = PerlProjectManager.getSdk(project, scriptFile);
    }
    return getPerlCommandLine(project, perlSdk, ObjectUtil.doIfNotNull(scriptFile, VirtualFile::getPath), perlParameters,
                              scriptParameters);
  }

  public static PerlCommandLine getPerlCommandLine(@NotNull Project project,
                                                   @Nullable String localScriptPath) {
    return getPerlCommandLine(project, null, localScriptPath, Collections.emptyList(), Collections.emptyList());
  }

  /**
   * Builds non-patched perl command line (without patching by version manager)
   *
   * @return new perl command line or null if sdk is missing or corrupted
   */
  public static @Nullable PerlCommandLine getPerlCommandLine(@NotNull Project project,
                                                             @Nullable Sdk perlSdk,
                                                             @Nullable String localScriptPath,
                                                             @NotNull List<String> perlParameters,
                                                             @NotNull List<String> scriptParameters) {
    if (perlSdk == null) {
      perlSdk = PerlProjectManager.getSdk(project);
    }
    if (perlSdk == null) {
      LOG.error("No sdk provided or available in project " + project);
      return null;
    }
    String interpreterPath = PerlProjectManager.getInterpreterPath(perlSdk);
    if (StringUtil.isEmpty(interpreterPath)) {
      LOG.warn("Empty interpreter path in " + perlSdk + " while building command line for " + localScriptPath);
      return null;
    }
    PerlCommandLine commandLine = new PerlCommandLine(interpreterPath).withSdk(perlSdk).withProject(project);
    PerlHostData<?, ?> hostData = PerlHostData.notNullFrom(perlSdk);
    commandLine.addParameters(getPerlRunIncludeArguments(hostData, project));

    commandLine.addParameters(perlParameters);

    if (StringUtil.isNotEmpty(localScriptPath)) {
      String remoteScriptPath = hostData.getRemotePath(localScriptPath);
      if (remoteScriptPath != null) {
        commandLine.addParameter(remoteScriptPath);
      }
    }

    commandLine.addParameters(scriptParameters);

    return commandLine;
  }

  /**
   * @return a list with {@code -I} arguments we need to pass to the Perl to include all in-project library directories and external
   * libraries configured by user
   */
  public static @NotNull List<String> getPerlRunIncludeArguments(@NotNull PerlHostData<?, ?> hostData, @NotNull Project project) {
    var result = new ArrayList<String>();
    var perlProjectManager = PerlProjectManager.getInstance(project);
    for (VirtualFile libRoot : perlProjectManager.getModulesLibraryRoots()) {
      result.add(PERL_I + hostData.getRemotePath(libRoot.getCanonicalPath()));
    }
    for (VirtualFile libRoot : perlProjectManager.getExternalLibraryRoots()) {
      result.add(PERL_I + hostData.getRemotePath(libRoot.getCanonicalPath()));
    }
    return result;
  }

  /**
   * Attempts to find a script in project's perl sdk and shows notification to user with suggestion to install a library if
   * script was not found
   *
   * @param project     to get sdk from
   * @param scriptName  script name
   * @param libraryName library to suggest if script was not found; notification won't be shown if lib is null/empty
   * @return script's virtual file if any
   */
  public static @Nullable VirtualFile findLibraryScriptWithNotification(@NotNull Project project,
                                                                        @NotNull String scriptName,
                                                                        @Nullable String libraryName) {
    return ObjectUtil.doIfNotNull(
      PerlProjectManager.getSdkWithNotification(project),
      it -> findLibraryScriptWithNotification(it, project, scriptName, libraryName));
  }


  /**
   * Attempts to find a script in project's perl sdk and shows notification to user with suggestion to install a library if
   * script was not found
   *
   * @param sdk         to find script in
   * @param scriptName  script name
   * @param libraryName library to suggest if script was not found, notification won't be shown if lib is null/empty
   * @return script's virtual file if any
   */
  public static @Nullable VirtualFile findLibraryScriptWithNotification(@NotNull Sdk sdk,
                                                                        @Nullable Project project,
                                                                        @NotNull String scriptName,
                                                                        @Nullable String libraryName) {
    VirtualFile scriptFile = findScript(project, scriptName);
    if (scriptFile != null) {
      return scriptFile;
    }

    if (StringUtil.isEmpty(libraryName)) {
      return null;
    }

    showMissingLibraryNotification(project, sdk, libraryName);

    return null;
  }

  public static void showMissingLibraryNotification(@NotNull Project project, @NotNull Sdk sdk, @NotNull Collection<String> packageNames) {
    if (packageNames.isEmpty()) {
      return;
    }
    if (packageNames.size() == 1) {
      showMissingLibraryNotification(project, sdk, packageNames.iterator().next());
      return;
    }

    Notification notification = new Notification(
      PerlBundle.message("perl.missing.library.notification"),
      PerlBundle.message("perl.missing.library.notification.title", packageNames.size()),
      StringUtil.join(ContainerUtil.sorted(packageNames), ", "),
      NotificationType.ERROR
    );
    addInstallActionsAndShow(project, sdk, packageNames, notification);
  }

  /**
   * Adds installation actions to the {@code notification} and shows it in the context of the {@code project}
   */
  public static void addInstallActionsAndShow(@Nullable Project project,
                                              @NotNull Sdk sdk,
                                              @NotNull Collection<String> packagesToInstall,
                                              @NotNull Notification notification) {
    List<AnAction> actions = new ArrayList<>();
    actions.add(PackageManagerAdapter.createInstallAction(sdk, project, packagesToInstall, notification::expire));

    if (ContainerUtil.intersects(BUNDLED_MODULE_PARTS, packagesToInstall)) {
      actions.add(new PerlDumbAwareAction(PerlBundle.message("perl.quickfix.install.module", BUNDLED_MODULE_NAME)) {
        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
          notification.expire();
          var extendedModulesList = new ArrayList<String>();
          extendedModulesList.add(BUNDLED_MODULE_NAME);
          extendedModulesList.addAll(ContainerUtil.subtract(packagesToInstall, BUNDLED_MODULE_PARTS));
          PackageManagerAdapter.installModules(sdk, project, extendedModulesList, notification::expire, true);
        }
      });
    }

    actions.forEach(notification::addAction);
    Notifications.Bus.notify(notification, project);
  }


  private static void showMissingLibraryNotification(@Nullable Project project, @NotNull Sdk sdk, @NotNull String libraryName) {
    Notification notification = new Notification(
      PerlBundle.message("perl.missing.library.notification"),
      PerlBundle.message("perl.missing.library.notification.title", libraryName),
      PerlBundle.message("perl.missing.library.notification.message", libraryName),
      NotificationType.ERROR
    );

    addInstallActionsAndShow(project, sdk, Collections.singletonList(libraryName), notification);
  }

  /**
   * Attempts to find an executable script by name in perl's libraries path
   *
   * @return script's virtual file if available
   * @apiNote returns virtual file of local file, not remote. It finds not a perl script, but executable script. E.g. for windows, it may
   * be a bat script
   **/
  @Contract("null,_->null;_,null->null")
  public static @Nullable VirtualFile findScript(@Nullable Project project, @Nullable String scriptName) {
    return ReadAction.nonBlocking(() -> {
      var sdk = PerlProjectManager.getSdk(project);
      if (sdk == null || StringUtil.isEmpty(scriptName)) {
        return null;
      }
      PerlOsHandler osHandler = PerlOsHandler.notNullFrom(sdk);
      return getBinDirectories(project)
        .map(root -> {
          VirtualFile scriptFile = null;
          if (osHandler.isMsWindows()) {
            scriptFile = root.findChild(scriptName + ".bat");
          }
          return scriptFile != null ? scriptFile : root.findChild(scriptName);
        })
        .filter(Objects::nonNull)
        .findFirst().orElse(null);
    }).executeSynchronously();
  }


  /**
   * @return list of perl bin directories where script from library may be located
   **/
  public static @NotNull Stream<VirtualFile> getBinDirectories(@NotNull Project project) {
    ApplicationManager.getApplication().assertReadAccessAllowed();
    var libraryRoots = PerlProjectManager.getInstance(project).getAllLibraryRoots();
    var files = new ArrayList<>(ContainerUtil.map(libraryRoots, PerlRunUtil::findLibsBin));

    var sdk = PerlProjectManager.getSdk(project);
    if (sdk != null) {
      PerlHostData<?, ?> hostData = PerlHostData.notNullFrom(sdk);
      File localSdkBinDir = hostData.getLocalPath(new File(
        StringUtil.notNullize(PerlProjectManager.getInterpreterPath(sdk))).getParentFile());
      if (localSdkBinDir != null) {
        files.add(VirtualFileUtil.findFileByIoFile(localSdkBinDir, false));
      }
      PerlVersionManagerData.notNullFrom(sdk).getBinDirsPath().forEach(
        it -> ObjectUtil.doIfNotNull(hostData.getLocalPath(it), localPath -> files.add(VirtualFileUtil.findFileByIoFile(localPath, false))));
    }
    return files.stream().filter(Objects::nonNull).distinct();
  }

  /**
   * Finds a bin dir for a library root
   *
   * @return bin root or null if not available
   * @implSpec for now we are traversing tree up to lib dir and resolving {@code ../bin}
   */
  private static @Nullable VirtualFile findLibsBin(@Nullable VirtualFile libraryRoot) {
    if (libraryRoot == null || !libraryRoot.isValid()) {
      return null;
    }
    File binPath = findLibsBin(new File(libraryRoot.getPath()));
    return binPath == null ? null : VfsUtil.findFileByIoFile(binPath, false);
  }

  /**
   * Finds a bin dir for a library root path
   *
   * @return bin root path or null if not found
   * @implSpec for now we are traversing tree up to {@code lib} dir and resolving {@code ../bin}
   */
  public static @Nullable File findLibsBin(@Nullable File libraryRoot) {
    if (libraryRoot == null) {
      return null;
    }
    String fileName = libraryRoot.getName();
    if ("lib".equals(fileName)) {
      return new File(libraryRoot.getParentFile(), "bin");
    }
    return findLibsBin(libraryRoot.getParentFile());
  }

  /**
   * Gets stdout from executing a perl command with a given parameters, command represented by {@code parameters}.
   */
  public static @NotNull List<String> getOutputFromPerl(@NotNull Sdk perlSdk, @NotNull String... parameters) {
    String interpreterPath = PerlProjectManager.getInterpreterPath(perlSdk);
    if (StringUtil.isEmpty(interpreterPath)) {
      LOG.warn("Empty interpreter path from " + perlSdk);
      return Collections.emptyList();
    }
    return getOutputFromProgram(new PerlCommandLine(
      interpreterPath).withParameters(parameters).withSdk(perlSdk));
  }


  /**
   * Gets stdout from executing a command represented by {@code commands} on the host represented by {@code hostData}
   * Commands are going to be patched with version manager, represented by {@code versionManagerData}
   */
  public static @NotNull List<String> getOutputFromProgram(@NotNull PerlHostData<?, ?> hostData,
                                                           @NotNull PerlVersionManagerData<?, ?> versionManagerData,
                                                           @NotNull String... commands) {
    return getOutputFromProgram(new PerlCommandLine(commands).withHostData(hostData).withVersionManagerData(versionManagerData));
  }

  /**
   * Gets stdout from a {@code commandLine} at host represented by {@code hostData}
   */
  private static @NotNull List<String> getOutputFromProgram(@NotNull PerlCommandLine commandLine) {
    try {
      var commandOutput = PerlHostData.execAndGetOutput(commandLine);
      if (commandOutput.getExitCode() != 0) {
        LOG.warn("Non-zero exit code from " + commandLine + "; " + commandOutput);
      }
      return commandOutput.getStdoutLines();
    }
    catch (Exception e) {
      LOG.warn("Error executing " + commandLine, e);
      return Collections.emptyList();
    }
  }

  public static @NotNull RunContentDescriptor runInConsole(@NotNull PerlCommandLine perlCommandLine) {
    ApplicationManager.getApplication().assertIsDispatchThread();
    Executor runExecutor = DefaultRunExecutor.getRunExecutorInstance();
    Project project = perlCommandLine.getNonNullEffectiveProject();
    boolean isUnitTestMode = ApplicationManager.getApplication().isUnitTestMode();
    PerlConsoleView consoleView = isUnitTestMode ? new PerlRunConsole(project) : new PerlTerminalExecutionConsole(project);
    consoleView.withHostData(perlCommandLine.getEffectiveHostData());
    ProcessHandler processHandler = null;
    try {
      processHandler = PerlHostData.createConsoleProcessHandler(perlCommandLine.withPty(!isUnitTestMode));
      if (isUnitTestMode) {
        processHandler.addProcessListener(new ProcessAdapter() {
          @Override
          public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
            LOG.info(outputType + ": " + event.getText());
          }
        });
      }
    }
    catch (ExecutionException e) {
      consoleView.print(e.getMessage(), ConsoleViewContentType.ERROR_OUTPUT);
      LOG.warn(e);
    }

    RunContentDescriptor runContentDescriptor = new RunContentDescriptor(
      consoleView,
      processHandler,
      consoleView.getComponent(),
      ObjectUtil.notNull(perlCommandLine.getConsoleTitle(), perlCommandLine.getCommandLineString()),
      ObjectUtil.notNull(perlCommandLine.getConsoleIcon(), PerlIcons.PERL_LANGUAGE_ICON)
    );

    project.getInstance(RunContentManager.class).showRunContent(runExecutor, runContentDescriptor);
    if (processHandler != null) {
      consoleView.attachToProcess(processHandler);
      processHandler.startNotify();
      if (ApplicationManager.getApplication().isUnitTestMode()) {
        LOG.assertTrue(ourTestDisposable != null);
        TEST_CONSOLE_DESCRIPTORS.add(runContentDescriptor);
        Disposer.register(ourTestDisposable, runContentDescriptor.getExecutionConsole());
      }
    }
    return runContentDescriptor;
  }

  public static void addMissingPackageListener(@NotNull ProcessHandler handler,
                                               @NotNull PerlCommandLine commandLine) {
    if (!commandLine.isWithMissingPackageListener()) {
      return;
    }
    ProcessListener listener = createMissingPackageListener(commandLine.getEffectiveProject(), commandLine.getEffectiveSdk());
    if (listener != null) {
      handler.addProcessListener(listener);
    }
  }

  /**
   * Creates a listener watching process output and showing notifications about missing libraries
   */
  private static @Nullable ProcessListener createMissingPackageListener(@Nullable Project project, @Nullable Sdk sdk) {
    if (project == null) {
      return null;
    }

    if (sdk == null) {
      sdk = PerlProjectManager.getSdk(project);
      if (sdk == null) {
        return null;
      }
    }

    Sdk finalSdk = sdk;
    Set<String> missingPackages = new HashSet<>();

    return new ProcessAdapter() {
      @Override
      public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
        String text = event.getText();
        if (StringUtil.isEmpty(text)) {
          return;
        }
        int keyOffset = text.indexOf(MISSING_MODULE_PREFIX);
        if (keyOffset == -1) {
          checkLegacyPrefix(text);
          return;
        }
        int startOffset = keyOffset + MISSING_MODULE_PREFIX.length();
        int endOffset = text.indexOf(MISSING_MODULE_SUFFIX, startOffset);
        if (endOffset == -1) {
          return;
        }
        processPackage(text.substring(startOffset, endOffset));
      }

      private void checkLegacyPrefix(@NotNull String text) {
        int keyOffset = text.indexOf(LEGACY_MODULE_PREFIX);
        if (keyOffset == -1) {
          return;
        }
        int startOffset = keyOffset + LEGACY_MODULE_PREFIX.length();
        int endOffset = text.indexOf(LEGACY_MODULE_SUFFIX, startOffset);
        if (endOffset == -1) {
          return;
        }
        processPackage(PerlPackageUtil.getPackageNameByPath(text.substring(startOffset, endOffset)));
      }

      private void processPackage(@Nullable String packageName) {
        if (StringUtil.isNotEmpty(packageName) && missingPackages.add(packageName)) {
          showMissingLibraryNotification(project, finalSdk, missingPackages);
        }
      }
    };
  }


  /**
   * Sets {@code newText} to the progress indicator if available.
   *
   * @return old indicator text
   */
  public static @Nullable String setProgressText(@Nullable @Nls String newText) {
    ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
    if (indicator != null) {
      String oldText = indicator.getText();
      indicator.setText(newText);
      return oldText;
    }
    return null;
  }

  public static void refreshSdkDirs(@Nullable Project project) {
    refreshSdkDirs(PerlProjectManager.getSdk(project), project);
  }

  /**
   * Asynchronously refreshes directories of sdk. Need to be invoked after installations
   */
  public static void refreshSdkDirs(@Nullable Sdk sdk, @Nullable Project project, @Nullable Runnable callback) {
    if (sdk == null) {
      return;
    }
    LOG.debug("Starting to refresh ", sdk, " on ", Thread.currentThread().getName());
    if (ourTestSdkRefreshSemaphore != null) {
      ourTestSdkRefreshSemaphore.down();
    }
    new Task.Backgroundable(project, PerlBundle.message("perl.progress.refreshing.interpreter.information", sdk.getName()), false) {
      @Override
      public void run(@NotNull ProgressIndicator indicator) {
        PerlSdkType.INSTANCE.setupSdkPaths(sdk);
        if (project != null) {
          WriteAction.runAndWait(() -> {
            if (!project.isDisposed()) {
              ProjectRootManagerEx.getInstanceEx(project)
                .makeRootsChange(EmptyRunnable.getInstance(), RootsChangeRescanningInfo.TOTAL_RESCAN);
              DaemonCodeAnalyzer.getInstance(project).restart();
            }
          });
        }
        if (callback != null) {
          callback.run();
        }
        if (ourTestSdkRefreshSemaphore != null) {
          ourTestSdkRefreshSemaphore.up();
        }
        LOG.debug("Finished to refresh ", sdk, " on ", Thread.currentThread().getName());
      }
    }.queue();
  }

  /**
   * Asynchronously refreshes directories of the provided sdk.
   * This method sets up sdk paths and rescans the roots of the project to ensure that all changes are accounted for.
   *
   * @param sdk      The sdk to refresh.
   * @param project  The project associated with the sdk.
   * @param callback A callback function to execute after the sdk is refreshed (optional).
   */
  public static void refreshSdkDirs(@Nullable Sdk sdk, @Nullable Project project) {
    refreshSdkDirs(sdk, project, null);
  }

  @TestOnly
  public static @NotNull List<RunContentDescriptor> getTestConsoleDescriptors() {
    return TEST_CONSOLE_DESCRIPTORS;
  }

  @TestOnly
  public static @NotNull Semaphore getSdkRefreshSemaphore() {
    return ourTestSdkRefreshSemaphore;
  }

  @TestOnly
  public static void setUpForTests(@NotNull Disposable testDisposable) {
    ourTestDisposable = testDisposable;
    ourTestSdkRefreshSemaphore = new Semaphore();
    Disposer.register(testDisposable, () -> {
      TEST_CONSOLE_DESCRIPTORS.clear();
      ourTestSdkRefreshSemaphore = null;
      ourTestDisposable = null;
    });
  }

  /**
   * Updates {@code environment} with {@code perlArguments} packed to {@code PERL5OPT} environment variable to pass it transparently.
   */
  public static void updatePerl5Opt(@NotNull Map<? super String, String> environment, @NotNull List<String> perlArguments) {
    if (perlArguments.isEmpty()) {
      return;
    }
    var perlParameters = StringUtil.join(perlArguments, " ");

    String currentOpt = environment.get(PERL5OPT);
    if (StringUtil.isNotEmpty(currentOpt)) {
      perlParameters = String.join(" ", currentOpt, perlParameters);
    }
    environment.put(PERL5OPT, perlParameters);
  }
}
