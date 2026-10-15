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

package com.perl5.lang.perl.idea.sdk.host;

import consulo.project.ui.notification.Notification;
import consulo.project.ui.notification.NotificationType;
import consulo.project.ui.notification.Notifications;
import consulo.ui.ex.action.AnActionEvent;
import consulo.application.ApplicationManager;
import consulo.application.WriteAction;
import consulo.logging.Logger;
import consulo.application.progress.ProgressIndicator;
import consulo.application.progress.Task;
import consulo.project.Project;
import consulo.content.bundle.Sdk;
import com.intellij.openapi.projectRoots.impl.PerlSdkTable;
import consulo.annotation.component.ExtensionImpl;
import consulo.project.startup.BackgroundStartupActivity;
import consulo.ui.UIAccess;
import consulo.util.io.FileUtil;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import consulo.virtualFileSystem.VirtualFile;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.idea.sdk.PerlSdkNotificationGroups;
import com.perl5.lang.perl.idea.actions.PerlDumbAwareAction;
import com.perl5.lang.perl.util.PerlPluginUtil;
import com.perl5.lang.perl.util.PerlRunUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@ExtensionImpl
public final class PerlHostCacheCleaner implements BackgroundStartupActivity {
  private static final Logger log = Logger.getInstance(PerlHostCacheCleaner.class);

  // TODO consulo: upstream implements Kotlin `suspend fun execute(project: Project)` of ProjectActivity (coroutine API), mapped to BackgroundStartupActivity
  @Override
  public void runActivity(@NotNull Project project, @NotNull UIAccess uiAccess) {
    var application = ApplicationManager.getApplication();
    if (!application.isUnitTestMode() && application.isDispatchThread()) {
      log.error("This supposed to be invoked from pooled thread");
      return;
    }
    var cacheRoot = VirtualFileUtil.findFileByIoFile(new File(PerlPluginUtil.getRemotesCachePath()), true);
    var cacheDirs = cacheRoot == null ? null : cacheRoot.getChildren();
    if (cacheDirs == null || cacheDirs.length == 0) {
      return;
    }
    Set<VirtualFile> existingCaches = new LinkedHashSet<>(Arrays.asList(cacheDirs));
    for (Sdk sdk : PerlSdkTable.getInstance().getInterpreters()) {
      var sdkCacheRoot = PerlHostData.notNullFrom(sdk).getLocalCacheRoot();
      if (StringUtil.isEmpty(sdkCacheRoot)) {
        return;
      }
      existingCaches.remove(VirtualFileUtil.findFileByIoFile(new File(sdkCacheRoot), true));
    }
    if (existingCaches.isEmpty()) {
      return;
    }
    var notification = new Notification(
      PerlSdkNotificationGroups.CACHE_CLEANER,
      PerlBundle.message("perl.cache.cleaner.notification.title"),
      PerlBundle.message(
        "perl.cache.cleaner.notification.message",
        existingCaches.stream()
          .map(it -> "<li>" + it.getName() + "</li>")
          .sorted()
          .collect(Collectors.joining(""))),
      NotificationType.WARNING
    );

    notification.addAction(new PerlDumbAwareAction(PerlBundle.message("perl.cache.cleaner.action.clean")) {
      @Override
      public void actionPerformed(@NotNull AnActionEvent event) {
        new Task.Backgroundable(project, PerlBundle.message("perl.cache.cleaner.cleaning"), true) {
          @Override
          public void run(@NotNull ProgressIndicator indicator) {
            notification.expire();
            for (VirtualFile it : existingCaches) {
              indicator.checkCanceled();
              try {
                PerlRunUtil.setProgressText(
                  PerlBundle.message(
                    "perl.cache.cleaner.deleting",
                    StringUtil.shortenPathWithEllipsis(FileUtil.toSystemDependentName(it.getPath()), 50)
                  )
                );
                // TODO consulo: WriteAction.runAndWait does not accept throwable runnable
                IOException[] deleteError = new IOException[1];
                WriteAction.runAndWait(() -> {
                  try {
                    it.delete(this);
                  }
                  catch (IOException e) {
                    deleteError[0] = e;
                  }
                });
                if (deleteError[0] != null) {
                  throw deleteError[0];
                }
              }
              catch (IOException e) {
                log.error(e);
              }
            }
          }
        }.queue();
      }
    });

    Notifications.Bus.notify(notification, project);
  }
}
