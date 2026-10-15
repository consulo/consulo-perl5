package com.perl5.lang.perl.idea.codeInsight.typeInference.value.serialization;

import com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlScalarContextValue;
import consulo.annotation.component.ExtensionImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Consulo registration of upstream {@code <valueBackendHelper forClass="com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlScalarContextValue"
 * implementationClass="com.perl5.lang.perl.idea.codeInsight.typeInference.value.serialization.PerlScalarContextValueBackendHelper"/>} (perl5.plugin.backend.main.xml).
 */
@ExtensionImpl
public class PerlScalarContextValueBackendHelperClassExtension implements PerlValueBackendHelper.ClassExtensionPoint {
  private final PerlValueBackendHelper<?> myInstance = new PerlScalarContextValueBackendHelper();

  @Override
  public @NotNull Class<?> getForClass() {
    return PerlScalarContextValue.class;
  }

  @Override
  public @NotNull PerlValueBackendHelper<?> getInstance() {
    return myInstance;
  }
}
