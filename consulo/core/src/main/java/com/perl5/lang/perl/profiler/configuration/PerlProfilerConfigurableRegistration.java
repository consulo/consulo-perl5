package com.perl5.lang.perl.profiler.configuration;

import consulo.annotation.component.ExtensionImpl;
import consulo.configurable.ApplicationConfigurable;
import consulo.execution.profiler.ProfilerConfigurableIds;
import org.jetbrains.annotations.Nullable;

/**
 * Consulo registration of upstream
 * {@code <applicationConfigurable parentId="profiler" id="profiler.perl5" provider="PerlProfilerConfigurationEditorConfigurableProvider"/>}
 * in perl5.plugin.profiler.main.xml
 */
@ExtensionImpl
public class PerlProfilerConfigurableRegistration extends PerlProfilerConfigurationEditorConfigurableProvider.PerlProfilerConfigurable
  implements ApplicationConfigurable {
  @Override
  public @Nullable String getParentId() {
    return ProfilerConfigurableIds.GROUP;
  }
}
