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

package com.perl5.lang.perl.idea.actions;

import consulo.project.Project;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.InputBoxBuilder;
import consulo.localize.LocalizeValue;
import consulo.util.lang.StringUtil;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.adapters.PackageManagerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public abstract class PerlInstallPackageActionBase extends PerlActionBase {

  public PerlInstallPackageActionBase(@NotNull String managerName) {
    super(PerlBundle.message("perl.action.install.packages", managerName));
  }

  @Override
  protected boolean isEnabled(@NotNull AnActionEvent event) {
    return super.isEnabled(event) && getAdapter(event) != null;
  }

  @Override
  protected boolean alwaysHideDisabled() {
    return true;
  }

  protected abstract @Nullable PackageManagerAdapter getAdapter(@NotNull AnActionEvent e);

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    PackageManagerAdapter packageManagerAdapter = getAdapter(e);
    if (packageManagerAdapter == null) {
      return;
    }

    // consulo: Messages are Swing dialogs (not available in web), the unified input box is asynchronous
    InputBoxBuilder.text()
      .title(getTemplatePresentation().getTextValue())
      .text(LocalizeValue.of(PerlBundle.message("dialog.message.enter.packages.names")))
      .showAsync(e.getData(Project.KEY))
      .whenComplete((packageNames, error) -> {
        if (StringUtil.isEmpty(packageNames)) {
          return;
        }

        packageManagerAdapter.queueInstall(Arrays.asList(packageNames.split("[\\s,;]+")), false);
      });
  }
}
