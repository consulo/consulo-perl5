package com.perl5.lang.perl.idea.codeInsight.typeInference.value.serialization;

import com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlDuckValue;
import consulo.annotation.component.ExtensionImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Consulo registration of upstream {@code <valueBackendHelper forClass="com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlDuckValue"
 * implementationClass="com.perl5.lang.perl.idea.codeInsight.typeInference.value.serialization.PerlDuckValueBackendHelper"/>} (perl5.plugin.backend.main.xml).
 */
@ExtensionImpl
public class PerlDuckValueBackendHelperClassExtension implements PerlValueBackendHelper.ClassExtensionPoint {
  private final PerlValueBackendHelper<?> myInstance = new PerlDuckValueBackendHelper();

  @Override
  public @NotNull Class<?> getForClass() {
    return PerlDuckValue.class;
  }

  @Override
  public @NotNull PerlValueBackendHelper<?> getInstance() {
    return myInstance;
  }
}
