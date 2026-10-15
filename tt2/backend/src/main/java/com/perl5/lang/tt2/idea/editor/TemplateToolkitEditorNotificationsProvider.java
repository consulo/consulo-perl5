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

package com.perl5.lang.tt2.idea.editor;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.fileEditor.EditorNotificationBuilder;
import consulo.fileEditor.FileEditor;
import consulo.application.dumb.DumbAware;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import consulo.fileEditor.EditorNotificationProvider;
import consulo.perl5.core.module.PerlModuleSettingsUtil;
import com.perl5.lang.tt2.TemplateToolkitBundle;
import com.perl5.lang.tt2.filetypes.TemplateToolkitFileType;
import com.perl5.lang.tt2.idea.settings.TemplateToolkitSettingsBackendHelper;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;


@ExtensionImpl
public class TemplateToolkitEditorNotificationsProvider implements EditorNotificationProvider, DumbAware {
  private final Project myProject;

  @Inject
  public TemplateToolkitEditorNotificationsProvider(Project project) {
    myProject = project;
  }

  @Override
  public @NotNull String getId() {
    return "perl5-tt2-not-in-root";
  }

  // consulo: IntelliJ collectNotificationData(project, file) -> Consulo buildNotification(file, fileEditor, builderFactory)
  @RequiredReadAction
  @Override
  public @Nullable EditorNotificationBuilder buildNotification(@NotNull VirtualFile file,
                                                               @NotNull FileEditor fileEditor,
                                                               @NotNull Supplier<EditorNotificationBuilder> builderFactory) {
    if (file.getFileType() != TemplateToolkitFileType.INSTANCE) {
      return null;
    }
    var settings = TemplateToolkitSettingsBackendHelper.getInstance(myProject);
    if (settings.isVirtualFileUnderRoot(file)) {
      return null;
    }
    return createNotificationPanel(builderFactory.get(), file);
  }

  private @NotNull EditorNotificationBuilder createNotificationPanel(@NotNull EditorNotificationBuilder panel, @NotNull VirtualFile file) {
    panel.withText(LocalizeValue.localizeTODO(TemplateToolkitBundle.message("tt2.error.file.not.in.root")));
    // consulo: templates roots are content folders, configured in module settings (with the Template Toolkit module extension enabled)
    panel.withAction(LocalizeValue.localizeTODO(TemplateToolkitBundle.message("link.label.configure")),
                     e -> PerlModuleSettingsUtil.openRootsSettings(myProject, file));
    return panel;
  }
}
