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

package com.perl5.lang.htmlmason.idea.editor.notification;

import consulo.fileEditor.FileEditor;
import consulo.application.dumb.DumbAware;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiManager;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.fileEditor.EditorNotificationBuilder;
import consulo.localize.LocalizeValue;
import jakarta.inject.Inject;
import consulo.fileEditor.EditorNotificationProvider;
import com.perl5.lang.htmlmason.HTMLMasonUtil;
import com.perl5.lang.htmlmason.HtmlMasonBundle;
import com.perl5.lang.htmlmason.filetypes.HTMLMasonFileType;
import com.perl5.lang.htmlmason.parser.psi.impl.HTMLMasonFileImpl;
import consulo.perl5.core.module.PerlModuleSettingsUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;


@ExtensionImpl
public class HTMLMasonPathsNotification implements EditorNotificationProvider, DumbAware {
  private final Project myProject;

  @Inject
  public HTMLMasonPathsNotification(Project myProject) {
    this.myProject = myProject;
  }

  @Override
  public @NotNull String getId() {
    return "html-mason-paths";
  }

  // consulo: IntelliJ collectNotificationData (panel factory) -> Consulo buildNotification (builder)
  @RequiredReadAction
  @Override
  public @Nullable EditorNotificationBuilder buildNotification(@NotNull VirtualFile file,
                                                               @NotNull FileEditor fileEditor,
                                                               @NotNull Supplier<EditorNotificationBuilder> builderFactory) {
    if (file.getFileType() != HTMLMasonFileType.INSTANCE) {
      return null;
    }

    if (HTMLMasonUtil.getComponentRoots(myProject).isEmpty()) {
      return createNotificationPanel(builderFactory.get(),
                                     HtmlMasonBundle.message("link.label.html.mason.components.roots.are.not.configured"), file);
    }
    else {
      PsiFile psiFile = PsiManager.getInstance(myProject).findFile(file);
      if (psiFile instanceof HTMLMasonFileImpl htmlMasonFile && HTMLMasonUtil.getComponentRoot(htmlMasonFile) == null) {
        return createNotificationPanel(builderFactory.get(), HtmlMasonBundle.message("link.label.component.not.under.one.configured.roots"),
                                       file);
      }
    }
    return null;
  }

  private @NotNull EditorNotificationBuilder createNotificationPanel(@NotNull EditorNotificationBuilder panel,
                                                                     @NotNull String message,
                                                                     @NotNull VirtualFile file) {
    panel.withText(LocalizeValue.localizeTODO(message));
    // consulo: components roots are content folders, configured in module settings (with the HTML::Mason module extension enabled)
    panel.withAction(LocalizeValue.localizeTODO(HtmlMasonBundle.message("link.label.configure")),
                     e -> PerlModuleSettingsUtil.openRootsSettings(myProject, file));
    return panel;
  }
}
