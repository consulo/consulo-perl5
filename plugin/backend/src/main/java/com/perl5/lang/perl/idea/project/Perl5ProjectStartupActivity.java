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

package com.perl5.lang.perl.idea.project;

import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.platform.Platform;
import consulo.project.startup.BackgroundStartupActivity;
import consulo.project.ui.notification.Notification;
import consulo.project.ui.notification.NotificationAction;
import consulo.project.ui.notification.NotificationType;
import consulo.project.ui.notification.Notifications;
import consulo.project.Project;
import consulo.ui.UIAccess;
import consulo.fileEditor.util.FileContentUtil;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.idea.sdk.PerlSdkNotificationGroups;
import com.perl5.lang.perl.idea.configuration.settings.PerlApplicationSettings;
import com.perl5.lang.perl.util.PerlPluginUtil;
import org.jetbrains.annotations.NotNull;

import static consulo.application.ApplicationManager.getApplication;

@ExtensionImpl
public final class Perl5ProjectStartupActivity implements BackgroundStartupActivity {
  // TODO consulo: upstream implements Kotlin `suspend fun execute(project: Project)` of ProjectActivity (coroutine API), mapped to BackgroundStartupActivity
  @Override
  public void runActivity(@NotNull Project project, @NotNull UIAccess uiAccess) {
    if (project.isDefault()) {
      return;
    }
    var settings = PerlApplicationSettings.getInstance();
    if (settings.shouldShowAnnounce()) {

      settings.setAnnounceShown();
      var notification = new Notification(
        PerlSdkNotificationGroups.PLUGIN_UPDATE,
        PerlBundle.message("plugin.update.baloon.title", PerlPluginUtil.getPluginVersion()),
        PerlBundle.message("plugin.update.baloon.text"),
        NotificationType.INFORMATION
      ).setImportant(true)
        .addAction(
          // TODO consulo: IntelliJ BrowseNotificationAction is missing, NotificationAction opening the url is used
          NotificationAction.create(LocalizeValue.of(PerlBundle.message("plugin.update.baloon.changes")),
                                    (e, n) -> Platform.current().openInBrowser("https://plugins.jetbrains.com/plugin/7796"))
        )
        .addAction(
          NotificationAction.create(LocalizeValue.of(PerlBundle.message("plugin.update.baloon.tracker")),
                                    (e, n) -> Platform.current().openInBrowser("https://github.com/hurricup/Perl5-IDEA/issues"))
        );

      Notifications.Bus.notify(notification);
    }
    if (!getApplication().isUnitTestMode()) {
      // TODO consulo: upstream suspends with `withContext(Dispatchers.EDT) { FileContentUtil::reparseOpenedFiles }` (coroutine API);
      //  that lambda only creates a callable reference and never invokes it, so upstream does not actually reparse files here
      getApplication().invokeAndWait(() -> {
        Runnable reparseOpenedFiles = FileContentUtil::reparseOpenedFiles;
      });
    }
  }
}
