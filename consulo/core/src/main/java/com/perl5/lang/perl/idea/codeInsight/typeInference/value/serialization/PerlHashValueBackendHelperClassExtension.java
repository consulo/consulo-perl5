package com.perl5.lang.perl.idea.codeInsight.typeInference.value.serialization;

import com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlHashValue;
import consulo.annotation.component.ExtensionImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Consulo registration of upstream {@code <valueBackendHelper forClass="com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlHashValue"
 * implementationClass="com.perl5.lang.perl.idea.codeInsight.typeInference.value.serialization.PerlHashValueBackendHelper"/>} (perl5.plugin.backend.main.xml).
 */
@ExtensionImpl
public class PerlHashValueBackendHelperClassExtension implements PerlValueBackendHelper.ClassExtensionPoint {
  private final PerlValueBackendHelper<?> myInstance = new PerlHashValueBackendHelper();

  @Override
  public @NotNull Class<?> getForClass() {
    return PerlHashValue.class;
  }

  @Override
  public @NotNull PerlValueBackendHelper<?> getInstance() {
    return myInstance;
  }
}
