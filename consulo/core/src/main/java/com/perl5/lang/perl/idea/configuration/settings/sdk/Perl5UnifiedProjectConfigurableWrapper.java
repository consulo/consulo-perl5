package com.perl5.lang.perl.idea.configuration.settings.sdk;

import consulo.configurable.Configurable;
import consulo.configurable.ConfigurationException;
import consulo.configurable.UnnamedConfigurable;
import consulo.disposer.Disposable;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.ui.Component;
import consulo.ui.Label;
import consulo.ui.Tab;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.layout.ScrollableLayout;
import consulo.ui.layout.TabbedLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Unified UI (works in desktop and web) counterpart of {@link Perl5ProjectConfigurableWrapper}: project-level configurables of
 * {@link Perl5SettingsConfigurableExtension}s, shown in tabs if there are several of them.
 */
public class Perl5UnifiedProjectConfigurableWrapper implements UnnamedConfigurable, Configurable.NoScroll {
  private final List<Configurable> myConfigurables;
  /**
   * configurables which provided unified UI, only they are reset/applied: Swing-only configurables have no components to work with
   */
  private final List<Configurable> myActiveConfigurables = new ArrayList<>();

  public Perl5UnifiedProjectConfigurableWrapper(Project project) {
    // consulo: settings of Perl frameworks (Template Toolkit, Mason) are per module, they are edited with the framework module extensions
    //  (module settings) and not contributed here
    myConfigurables = Perl5SettingsConfigurableExtension.stream()
      .map(ext -> ext.createProjectConfigurable(project))
      .filter(Objects::nonNull)
      .toList();
    assert !myConfigurables.isEmpty();
  }

  @RequiredUIAccess
  @Override
  public Component createUIComponent(Disposable uiDisposable) {
    myActiveConfigurables.clear();
    if (myConfigurables.size() == 1) {
      return createConfigurableComponent(myConfigurables.getFirst(), uiDisposable);
    }

    TabbedLayout tabbedLayout = TabbedLayout.create();
    for (Configurable configurable : myConfigurables) {
      Component configurableComponent = createConfigurableComponent(configurable, uiDisposable);

      LocalizeValue title = configurable.getDisplayName();
      Tab tab = tabbedLayout.createTab();
      tab.setRenderer((it, presentation) -> presentation.append(title));
      tabbedLayout.addTab(tab, configurableComponent);
    }
    return tabbedLayout;
  }

  @RequiredUIAccess
  private Component createConfigurableComponent(Configurable configurable, Disposable uiDisposable) {
    Component component = configurable.createUIComponent(uiDisposable);
    if (component == null) {
      // TODO consulo: the configurable provides only Swing UI, which can't be shown by unified UI
      return Label.create(LocalizeValue.localizeTODO("Not supported UI"));
    }
    myActiveConfigurables.add(configurable);
    // upstream: every tab is wrapped into a scroll pane, configurables with own scrollable components are not wrapped
    return configurable instanceof Configurable.NoScroll ? component : ScrollableLayout.create(component);
  }

  @RequiredUIAccess
  @Override
  public boolean isModified() {
    for (Configurable configurable : myActiveConfigurables) {
      if (configurable.isModified()) {
        return true;
      }
    }

    return false;
  }

  @RequiredUIAccess
  @Override
  public void apply() throws ConfigurationException {
    for (Configurable configurable : myActiveConfigurables) {
      configurable.apply();
    }
  }

  @RequiredUIAccess
  @Override
  public void reset() {
    myActiveConfigurables.forEach(Configurable::reset);
  }

  @RequiredUIAccess
  @Override
  public void disposeUIResources() {
    myActiveConfigurables.forEach(Configurable::disposeUIResources);
    myActiveConfigurables.clear();
  }
}
