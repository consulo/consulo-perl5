package com.perl5.lang.perl.idea.configuration.settings.sdk;

import consulo.annotation.component.ExtensionImpl;
import consulo.configurable.Configurable;
import consulo.project.Project;
import org.jspecify.annotations.Nullable;

/**
 * Consulo registration of {@code <settings.configurable.extension implementation="...Perl5SettingsConfigurableExtensionImpl" order="first"/>}
 */
@ExtensionImpl(order = "first")
public final class Perl5SettingsConfigurableExtensionImplRegistration extends Perl5SettingsConfigurableExtensionImpl {
  /**
   * Unified UI counterpart of upstream {@link Perl5ProjectConfigurable}, works in desktop and web
   */
  @Override
  public @Nullable Configurable createProjectConfigurable(Project project) {
    return new Perl5UnifiedProjectConfigurable(project);
  }
}
