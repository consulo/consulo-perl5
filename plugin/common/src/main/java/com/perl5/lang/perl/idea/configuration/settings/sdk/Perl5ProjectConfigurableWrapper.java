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

import consulo.configurable.Configurable;
import consulo.configurable.ConfigurationException;
import consulo.configurable.UnnamedConfigurable;
import consulo.disposer.Disposable;
import consulo.project.Project;
import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.awt.JBScrollPane;
import consulo.ui.ex.awt.JBTabbedPane;
import consulo.ui.ex.awt.JBEmptyBorder;
import consulo.ui.ex.awt.JBUI;
import consulo.ui.ex.awtUnsafe.TargetAWT;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Perl5ProjectConfigurableWrapper implements UnnamedConfigurable {
  private final List<Configurable> myConfigurables;

  public Perl5ProjectConfigurableWrapper(@NotNull Project project) {
    myConfigurables = Perl5SettingsConfigurableExtension.stream()
      .map(ext -> ext.createProjectConfigurable(project))
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
    assert !myConfigurables.isEmpty();
  }

  @RequiredUIAccess
  @Override
  public @Nullable Component createUIComponent(Disposable parentDisposable) {
    if (myConfigurables.size() == 1) {
      return myConfigurables.getFirst().createUIComponent(parentDisposable);
    }

    JBTabbedPane tabbedPane = new JBTabbedPane();
    tabbedPane.setTabComponentInsets(JBUI.emptyInsets());

    myConfigurables.forEach(configurable -> {
      JComponent configurableComponent = (JComponent) TargetAWT.to(configurable.createUIComponent(parentDisposable));

      JBScrollPane scrollPane = new JBScrollPane(configurableComponent,
                                                 ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                 ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      scrollPane.setBorder(new JBEmptyBorder(JBUI.emptyInsets()));

      tabbedPane.add(configurable.getDisplayName().get(), scrollPane);
    });
    return TargetAWT.wrap(tabbedPane);
  }

  @Override
  public boolean isModified() {
    for (Configurable configurable : myConfigurables) {
      if (configurable.isModified()) {
        return true;
      }
    }

    return false;
  }

  @Override
  public void apply() throws ConfigurationException {
    for (Configurable configurable : myConfigurables) {
      configurable.apply();
    }
  }

  @Override
  public void reset() {
    myConfigurables.forEach(Configurable::reset);
  }

  @Override
  public void disposeUIResources() {
    myConfigurables.forEach(Configurable::disposeUIResources);
  }
}
