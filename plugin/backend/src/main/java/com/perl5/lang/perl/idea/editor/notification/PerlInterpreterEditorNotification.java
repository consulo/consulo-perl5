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

package com.perl5.lang.perl.idea.editor.notification;

import consulo.fileEditor.FileEditor;
import consulo.application.dumb.DumbAware;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import consulo.language.file.light.LightVirtualFile;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.fileEditor.EditorNotificationBuilder;
import consulo.localize.LocalizeValue;
import jakarta.inject.Inject;
import consulo.fileEditor.EditorNotificationProvider;
import consulo.fileEditor.EditorNotifications;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.fileTypes.PerlFileType;
import com.perl5.lang.perl.idea.configuration.settings.PerlLocalSettings;
import com.perl5.lang.perl.idea.project.PerlProjectManager;
import consulo.perl5.core.module.PerlModuleSettingsUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;


@ExtensionImpl
public class PerlInterpreterEditorNotification implements EditorNotificationProvider, DumbAware {
  private final Project myProject;

  @Inject
  public PerlInterpreterEditorNotification(Project myProject) {
    this.myProject = myProject;
  }

  @Override
  public @NotNull String getId() {
    return "perl5-interpreter";
  }

  @RequiredReadAction
  @Override
  public @Nullable EditorNotificationBuilder buildNotification(@NotNull VirtualFile virtualFile,
                                                               @NotNull FileEditor fileEditor,
                                                               @NotNull Supplier<EditorNotificationBuilder> builderFactory) {
    if (!(virtualFile.getFileType() instanceof PerlFileType) || virtualFile instanceof LightVirtualFile) {
      return null;
    }

    final PerlLocalSettings perlLocalSettings = PerlLocalSettings.getInstance(myProject);
    if (perlLocalSettings.DISABLE_NO_INTERPRETER_WARNING) {
      return null;
    }

    // consulo: the interpreter of the file module (Perl module extension), the project one for files outside of modules
    if (PerlProjectManager.getSdk(myProject, virtualFile) != null) {
      return null;
    }
    return createNotificationPanel(perlLocalSettings, builderFactory.get(), virtualFile);
  }

  private @NotNull EditorNotificationBuilder createNotificationPanel(@NotNull PerlLocalSettings perlLocalSettings,
                                                                     @NotNull EditorNotificationBuilder panel,
                                                                     @NotNull VirtualFile virtualFile) {
    panel.withText(LocalizeValue.localizeTODO(PerlBundle.message("perl.notification.sdk.not.configured")));
    // consulo: the interpreter is configured in the Perl module extension of the file module
    panel.withAction(LocalizeValue.localizeTODO(PerlBundle.message("perl.notification.configure")),
                     e -> PerlModuleSettingsUtil.openInterpreterSettings(myProject, virtualFile));
    panel.withAction(LocalizeValue.localizeTODO(PerlBundle.message("perl.notification.disable.notification")), e -> {
      perlLocalSettings.DISABLE_NO_INTERPRETER_WARNING = true;
      EditorNotifications.getInstance(myProject).updateAllNotifications();
    });

    return panel;
  }
}
