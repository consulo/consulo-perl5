package com.perl5.lang.perl.idea.codeInsight.typeInference.value.serialization;

import com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlCallStaticValue;
import consulo.annotation.component.ExtensionImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Consulo registration of upstream {@code <valueBackendHelper forClass="com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlCallStaticValue"
 * implementationClass="com.perl5.lang.perl.idea.codeInsight.typeInference.value.serialization.PerlCallStaticValueBackendHelper"/>} (perl5.plugin.backend.main.xml).
 */
@ExtensionImpl
public class PerlCallStaticValueBackendHelperClassExtension implements PerlValueBackendHelper.ClassExtensionPoint {
  private final PerlValueBackendHelper<?> myInstance = new PerlCallStaticValueBackendHelper();

  @Override
  public @NotNull Class<?> getForClass() {
    return PerlCallStaticValue.class;
  }

  @Override
  public @NotNull PerlValueBackendHelper<?> getInstance() {
    return myInstance;
  }
}
