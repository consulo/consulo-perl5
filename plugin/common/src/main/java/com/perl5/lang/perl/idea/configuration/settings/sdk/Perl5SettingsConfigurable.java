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

package com.perl5.lang.perl.idea.configuration.settings.sdk;

import consulo.annotation.component.ExtensionImpl;
import consulo.application.ApplicationManager;
import consulo.configurable.Configurable;
import consulo.configurable.ConfigurationException;
import consulo.configurable.NonDefaultProjectConfigurable;
import consulo.configurable.ProjectConfigurable;
import consulo.configurable.SearchableConfigurable;
import consulo.configurable.StandardConfigurableIds;
import consulo.disposer.Disposable;
import consulo.ide.setting.ShowSettingsUtil;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;
import com.perl5.PerlBundle;
import org.jetbrains.annotations.NotNull;
import jakarta.inject.Inject;
import org.jetbrains.annotations.Nullable;

/**
 * TODO consulo: upstream is a {@code ModuleAwareProjectConfigurable} registered as {@code projectConfigurable} with
 *  {@code groupId="language"} via {@link Perl5SettingsConfigurableProvider}: a project item (interpreter and project options) and an item
 *  per module (perl roots editor). Consulo has no "Languages & Frameworks" group, configurable is registered directly under the Editor
 *  group. The interpreter is configured per module with the Perl module extension and perl roots with the module content entries
 *  editor (Project Structure), so the page has project options only: {@link Perl5UnifiedProjectConfigurableWrapper} (unified UI,
 *  desktop and web) with tabs of {@link Perl5SettingsConfigurableExtension}s. Settings of Perl frameworks (Template Toolkit, Mason),
 *  upstream tabs of this page, are per module: they are edited with the framework module extensions.
 */
@ExtensionImpl
public class Perl5SettingsConfigurable implements SearchableConfigurable, ProjectConfigurable, NonDefaultProjectConfigurable,
                                                  Configurable.NoScroll {
  public static final String ID = "PERL5_MAIN_SETTINGS";

  private final @NotNull Project myProject;
  private @Nullable Perl5UnifiedProjectConfigurableWrapper myProjectConfigurable;

  @Inject
  public Perl5SettingsConfigurable(@NotNull Project project) {
    myProject = project;
  }

  @Override
  public @NotNull String getId() {
    return ID;
  }

  @Override
  public @Nullable String getParentId() {
    return StandardConfigurableIds.EDITOR_GROUP;
  }

  @Override
  public @NotNull LocalizeValue getDisplayName() {
    return LocalizeValue.of(PerlBundle.message("perl.perl5"));
  }

  @RequiredUIAccess
  @Override
  public @Nullable Component createUIComponent(@NotNull Disposable uiDisposable) {
    Perl5UnifiedProjectConfigurableWrapper projectConfigurable = new Perl5UnifiedProjectConfigurableWrapper(myProject);
    myProjectConfigurable = projectConfigurable;
    return projectConfigurable.createUIComponent(uiDisposable);
  }

  @RequiredUIAccess
  @Override
  public boolean isModified() {
    return myProjectConfigurable != null && myProjectConfigurable.isModified();
  }

  @RequiredUIAccess
  @Override
  public void apply() throws ConfigurationException {
    if (myProjectConfigurable != null) {
      myProjectConfigurable.apply();
    }
  }

  @RequiredUIAccess
  @Override
  public void reset() {
    if (myProjectConfigurable != null) {
      myProjectConfigurable.reset();
    }
  }

  @RequiredUIAccess
  @Override
  public void disposeUIResources() {
    if (myProjectConfigurable != null) {
      myProjectConfigurable.disposeUIResources();
      myProjectConfigurable = null;
    }
  }

  /**
   * TODO consulo: selection by class is the one which works on desktop (selection by {@link #ID} finds only top level pages and pages
   *  of {@code SearchableConfigurable.Parent}, this page is a child of the Editor group). Unified (web) {@code ShowSettingsUtil}
   *  ignores the page to select in all its methods, so there the dialog opens without selection.
   */
  public static void open(@NotNull Project project) {
    ApplicationManager.getApplication().invokeLater(
      () -> ShowSettingsUtil.getInstance().showSettingsDialog(project, Perl5SettingsConfigurable.class));
  }
}
