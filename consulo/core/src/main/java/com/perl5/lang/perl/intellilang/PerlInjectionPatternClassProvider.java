package com.perl5.lang.perl.intellilang;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.inject.advanced.pattern.PatternClassProvider;
import org.jetbrains.annotations.Nullable;

/**
 * Consulo registration of {@code <patterns.patternClass className="com.perl5.lang.perl.intellilang.PerlInjectionPatterns" alias="perl5"/>}
 * from the upstream perl5.plugin.intelliLang.main.xml
 */
@ExtensionImpl
public final class PerlInjectionPatternClassProvider implements PatternClassProvider {
  @Override
  public @Nullable String getAlias() {
    return "perl5";
  }

  @Override
  public Class<?> getPatternClass() {
    return PerlInjectionPatterns.class;
  }
}
