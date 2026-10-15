package com.perl5.lang.perl.idea.sdk.host.local;

import com.perl5.lang.perl.idea.sdk.PerlHandlerBean;
import consulo.annotation.component.ExtensionImpl;

/**
 * Consulo registration of {@code <hostHandler key="localhost"/>} from the upstream plugin.xml
 */
@ExtensionImpl
public final class PerlLocalHostHandlerRegistration extends PerlHandlerBean {
  public PerlLocalHostHandlerRegistration() {
    super("com.perl5.hostHandler", "localhost", PerlLocalHostHandler.class);
  }
}
