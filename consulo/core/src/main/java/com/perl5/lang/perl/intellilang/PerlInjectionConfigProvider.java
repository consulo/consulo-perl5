package com.perl5.lang.perl.intellilang;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.inject.advanced.InjectionConfigProvider;

import java.io.InputStream;

/**
 * Consulo registration of {@code <intelliLang.injectionConfig config="perlInjections.xml"/>} from the upstream
 * perl5.plugin.intelliLang.main.xml
 */
@ExtensionImpl
public final class PerlInjectionConfigProvider implements InjectionConfigProvider {
  @Override
  public InputStream openConfigFileStream() throws Exception {
    return getClass().getResourceAsStream("/perlInjections.xml");
  }
}
