package com.perl5.lang.perl.idea.codeInsight.typeInference.value.serialization;

import com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlArrayElementValue;
import consulo.annotation.component.ExtensionImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Consulo registration of upstream {@code <valueBackendHelper forClass="com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlArrayElementValue"
 * implementationClass="com.perl5.lang.perl.idea.codeInsight.typeInference.value.serialization.PerlArrayElementValueBackendHelper"/>} (perl5.plugin.backend.main.xml).
 */
@ExtensionImpl
public class PerlArrayElementValueBackendHelperClassExtension implements PerlValueBackendHelper.ClassExtensionPoint {
  private final PerlValueBackendHelper<?> myInstance = new PerlArrayElementValueBackendHelper();

  @Override
  public @NotNull Class<?> getForClass() {
    return PerlArrayElementValue.class;
  }

  @Override
  public @NotNull PerlValueBackendHelper<?> getInstance() {
    return myInstance;
  }
}
