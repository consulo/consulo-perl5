package com.perl5.lang.perl.idea.sdk.implementation.porters;

import com.perl5.lang.perl.idea.sdk.PerlHandlerBean;
import consulo.annotation.component.ExtensionImpl;

/**
 * Consulo registration of {@code <implementationHandler key="porters"/>} from the upstream plugin.xml
 */
@ExtensionImpl
public final class PerlPortersHandlerRegistration extends PerlHandlerBean {
  public PerlPortersHandlerRegistration() {
    super("com.perl5.implementationHandler", "porters", PerlPortersHandler.class);
  }
}
