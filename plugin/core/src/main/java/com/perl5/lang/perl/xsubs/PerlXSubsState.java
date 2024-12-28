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

package com.perl5.lang.perl.xsubs;

import com.perl5.PerlBundle;
import com.perl5.lang.perl.idea.PerlPathMacros;
import com.perl5.lang.perl.idea.actions.PerlActionBase;
import com.perl5.lang.perl.idea.execution.PerlCommandLine;
import com.perl5.lang.perl.idea.project.PerlProjectManager;
import com.perl5.lang.perl.idea.sdk.host.PerlHostData;
import com.perl5.lang.perl.idea.sdk.host.os.PerlOsHandler;
import com.perl5.lang.perl.util.PerlPluginUtil;
import com.perl5.lang.perl.util.PerlRunUtil;
import com.perl5.lang.perl.util.PerlUtil;
import consulo.application.Application;
import consulo.application.ApplicationManager;
import consulo.application.ReadAction;
import consulo.application.WriteAction;
import consulo.application.progress.ProgressIndicator;
import consulo.application.progress.Task;
import consulo.component.ProcessCanceledException;
import consulo.component.persist.PersistentStateComponent;
import consulo.component.persist.State;
import consulo.component.persist.Storage;
import consulo.content.bundle.Sdk;
import consulo.document.util.FileContentUtilCore;
import consulo.language.file.light.LightVirtualFile;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.scope.GlobalSearchScopesCore;
import consulo.language.psi.search.FilenameIndex;
import consulo.logging.Logger;
import consulo.module.content.ProjectRootManager;
import consulo.process.util.ProcessOutput;
import consulo.project.Project;
import consulo.project.ui.notification.NotificationType;
import consulo.project.ui.notification.Notifications;
import consulo.project.util.ProjectUtil;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.awt.Messages;
import consulo.util.collection.ContainerUtil;
import consulo.util.lang.StringUtil;
import consulo.util.xml.serializer.XmlSerializerUtil;
import consulo.util.xml.serializer.annotation.Transient;
import consulo.virtualFileSystem.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@State(
  name = "Perl5XSubsState",
  storages = @Storage(PerlPathMacros.PERL5_PROJECT_SETTINGS_FILE)
)
public class PerlXSubsState implements PersistentStateComponent<PerlXSubsState> {
  private static final Logger LOG = Logger.getInstance(PerlXSubsState.class);
  @Transient
  public static final String DEPARSED_FILE_NAME = "_Deparsed_XSubs.pm";
  public boolean isActual = true;
  public Map<String, Long> myFilesMap = Collections.emptyMap();
  @Transient
  private Task.Backgroundable myParserTask = null;
  @Transient
  private final @Nullable Project myProject;

  @SuppressWarnings("unused")
  public PerlXSubsState() {
    myProject = null;
  }

  @SuppressWarnings("unused")
  public PerlXSubsState(@NotNull Project project) {
    myProject = project;
  }

  @Override
  public @Nullable PerlXSubsState getState() {
    return this;
  }

  private @NotNull Project getProject() {
    return Objects.requireNonNull(myProject);
  }

  @Override
  public void loadState(@NotNull PerlXSubsState state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  private Set<VirtualFile> getAllXSFiles(@NotNull Project project) {
    PerlProjectManager perlProjectManager = PerlProjectManager.getInstance(project);
    List<VirtualFile> classesRoots = perlProjectManager.getAllLibraryRoots();
    if (classesRoots.isEmpty()) {
      return Collections.emptySet();
    }
    Sdk projectSdk = perlProjectManager.getProjectSdk();
    if (projectSdk == null) {
      return Collections.emptySet();
    }
    PerlOsHandler osHandler = PerlOsHandler.notNullFrom(projectSdk);

    GlobalSearchScope classRootsScope =
      GlobalSearchScopesCore.directoriesScope(getProject(), true, classesRoots.toArray(VirtualFile.EMPTY_ARRAY));

    Set<VirtualFile> result = new HashSet<>();
    for (VirtualFile virtualFile : FilenameIndex.getAllFilesByExt(project, osHandler.getXSBinaryExtension(), classRootsScope)) {
      if (virtualFile.isValid() && !virtualFile.isDirectory() && !(virtualFile instanceof LightVirtualFile)) {
        String path = virtualFile.getCanonicalPath();
        if (path != null && StringUtil.contains(path, "/auto/")) {
          result.add(virtualFile);
        }
      }
    }
    return result;
  }

  public void rescanFiles(@Nullable Runnable callback) {
    ProgressIndicatorUtils.scheduleWithWriteActionPriority(new ReadTask() {
      @Override
      public void computeInReadAction(@NotNull ProgressIndicator indicator) throws ProcessCanceledException {
        if (!PerlProjectManager.isPerlEnabled(myProject)) {
          return;
        }
        int filesCounter = 0;
        indicator.setIndeterminate(false);
        indicator.setText(PerlBundle.message("perl.scanning.xs.changes"));
        if (isActual) {
          Set<VirtualFile> allXSFiles = getAllXSFiles(myProject);
          for (VirtualFile virtualFile : allXSFiles) {
            if (indicator.isCanceled()) {
              return;
            }

            if (!virtualFile.isValid()) {
              continue;
            }

            indicator.setFraction((double)filesCounter / allXSFiles.size());

            if (!isFileUpToDate(virtualFile)) {
              isActual = false;
              break;
            }
            else {
              filesCounter++;
            }
          }
        }

        isActual = isActual && (filesCounter == 0 || getDeparsedSubsFile() != null);

        if (!isActual) {
          showNotification(
            PerlBundle.message("perl.deparsing.change.detected.title"),
            PerlBundle.message("perl.deparsing.change.detected.message"),
            NotificationType.INFORMATION,
            notification -> Collections.singletonList(new PerlActionBase(PerlBundle.message("perl.deparsing.action")) {
              @Override
              public void actionPerformed(@NotNull AnActionEvent e) {
                notification.expire();
                reparseXSubs();
              }
            })
          );
        }
        if (callback != null) {
          callback.run();
        }
      }

      @Override
      public void onCanceled(@NotNull ProgressIndicator indicator) {
        rescanFiles(null);
      }
    });
  }

  @VisibleForTesting
  public @Nullable VirtualFile getDeparsedSubsFile() {
    for (VirtualFile possibleLocation : getPossibleFileLocations()) {
      var deparsedFile = possibleLocation.findFileByRelativePath(DEPARSED_FILE_NAME);
      if (deparsedFile != null && deparsedFile.isValid()) {
        return deparsedFile;
      }
    }

    return null;
  }

  private @NotNull Iterable<VirtualFile> getPossibleFileLocations() {
    var contentRoots = PerlUtil.mutableList(ProjectRootManager.getInstance(getProject()).getContentRoots());
    contentRoots.add(ProjectUtil.guessProjectDir(getProject()));
    return ContainerUtil.filter(contentRoots, it -> it != null && it.isValid() && it.exists() && it.isDirectory());
  }

  private @NotNull VirtualFile getOrCreateDeparsedSubsFile() throws IOException {
    var deparsedSubsFile = getDeparsedSubsFile();
    if (deparsedSubsFile != null) {
      return deparsedSubsFile;
    }

    for (VirtualFile possibleLocation : getPossibleFileLocations()) {
      try {
        return possibleLocation.findOrCreateChildData(this, DEPARSED_FILE_NAME);
      }
      catch (IOException e) {
        LOG.warn("Unable to create " + DEPARSED_FILE_NAME + " in the content root " + possibleLocation +
                 "cause: " + e.getMessage());
      }
    }

    throw new IOException("Could not find suitable location for creating a file: " + DEPARSED_FILE_NAME);
  }

  private boolean isFileUpToDate(@NotNull VirtualFile virtualFile) {
    String path = virtualFile.getCanonicalPath();

    if (path != null) {
      Long modificationStamp = myFilesMap.get(path);
      return modificationStamp != null && modificationStamp == VfsUtilCore.virtualToIoFile(virtualFile).lastModified();
    }
    return false;
  }

  public void reparseXSubs() {
    if (!PerlProjectManager.isPerlEnabled(myProject)) {
      return;
    }

    if (myParserTask != null) {
      Messages.showErrorDialog(
        myProject,
        PerlBundle.message("perl.deparsing.in.progress.message"),
        PerlBundle.message("perl.deparsing.in.progress.title")
      );
      return;
    }

    PerlCommandLine commandLine = PerlRunUtil.getPerlCommandLine(myProject, PerlPluginUtil.getHelperPath("xs_parser_simple.pl"));

    if (commandLine == null) {
      LOG.warn("Unable to create deparser command line");
      return;
    }
    commandLine.withCharset(StandardCharsets.UTF_8).withMissingPackageListener(false);

    LOG.info("Deparsing: " + commandLine.getCommandLineString());

    myParserTask = new Task.Backgroundable(myProject, PerlBundle.message("perl.deparsing.xsubs"), false) {
      @Override
      public void run(@NotNull ProgressIndicator indicator) {
        indicator.setIndeterminate(true);
        var project = PerlXSubsState.this.myProject;
        Map<String, Long> newFilesMap = ReadAction.compute(() -> {
          if (project.isDisposed()) {
            return null;
          }
          final Map<String, Long> result = new HashMap<>();
          for (VirtualFile virtualFile : getAllXSFiles(project)) {
            if (virtualFile.isValid()) {
              String filePath = virtualFile.getCanonicalPath();
              if (filePath != null) {
                result.put(filePath, VfsUtilCore.virtualToIoFile(virtualFile)
                  .lastModified());
              }
            }
          }
          return result;
        });
        if (newFilesMap == null) {
          myParserTask = null;
          return;
        }

        ProcessOutput processOutput;
        try {
          processOutput = PerlHostData.execAndGetOutput(commandLine);
        }
        catch (ExecutionException e) {
          LOG.warn("Error deparsing", e);

          showNotification(
            PerlBundle.message("perl.deparsing.error.execution"),
            e.getMessage(),
            NotificationType.ERROR
          );
          myParserTask = null;
          return;
        }
        final String stdout = processOutput.getStdout();
        String stderr = processOutput.getStderr();
        int exitCode = processOutput.getExitCode();
        LOG.info("Deparsing finished with exit code: " + exitCode +
                 (StringUtil.isEmpty(stderr) ? "" : ". STDERR:\n" + stderr));

        if (exitCode != 0) {
          showNotification(
            PerlBundle.message("perl.deparsing.error.execution"),
            stderr,
            NotificationType.ERROR
          );
        }
        else if (!stdout.isEmpty()) {
          Application application = ApplicationManager.getApplication();
          application.invokeAndWait(
            () -> WriteAction.run(() -> {
              if (project.isDisposed()) {
                return;
              }
              try {
                VirtualFile deparsedFile = getOrCreateDeparsedSubsFile();
                deparsedFile.setWritable(true);
                OutputStream outputStream = deparsedFile.getOutputStream(null);
                outputStream.write(stdout.getBytes());
                outputStream.close();
                deparsedFile.setWritable(false);
                FileContentUtilCore.reparseFiles(deparsedFile);

                myFilesMap = newFilesMap;
                isActual = true;

                showNotification(
                  PerlBundle.message("perl.deparsing.finished"),
                  "",
                  NotificationType.INFORMATION
                );
              }
              catch (IOException e) {
                LOG.warn("Error creating deparsed file", e);
                showNotification(
                  PerlBundle.message("perl.deparsing.error.creating.file"),
                  e.getMessage(),
                  NotificationType.ERROR
                );
              }
              // fixme fix modality state
            }));
        }
        myParserTask = null;
      }
    };
    myParserTask.queue();
  }

  private void showNotification(@NotNull @NotificationTitle String title,
                                @NotNull @NotificationContent String message,
                                @NotNull NotificationType type) {
    showNotification(title, message, type, null);
  }

  private void showNotification(@NotNull @NotificationTitle String title,
                                @NotNull @NotificationContent String message,
                                @NotNull NotificationType type,
                                @Nullable Function<? super Notification, ? extends List<AnAction>> actionsProvider) {
    Notification notification = new Notification(PerlBundle.message("perl.deparsing.notification"), title, message, type);

    if (actionsProvider != null) {
      List<AnAction> actions = actionsProvider.fun(notification);
      if (actions != null) {
        actions.forEach(notification::addAction);
      }
    }
    Notifications.Bus.notify(notification, myProject);
  }

  public static PerlXSubsState getInstance(@NotNull Project project) {
    return project.getService(PerlXSubsState.class);
  }
}
