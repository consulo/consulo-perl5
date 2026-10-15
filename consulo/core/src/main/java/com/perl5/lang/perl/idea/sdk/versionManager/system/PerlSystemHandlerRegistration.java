package com.perl5.lang.perl.idea.sdk.versionManager.system;

import com.perl5.lang.perl.idea.sdk.PerlHandlerBean;
import consulo.annotation.component.ExtensionImpl;

/**
 * Consulo registration of {@code <versionManagerHandler key="system"/>} from the upstream plugin.xml
 */
@ExtensionImpl
public final class PerlSystemHandlerRegistration extends PerlHandlerBean {
  public PerlSystemHandlerRegistration() {
    super("com.perl5.versionManagerHandler", "system", PerlSystemHandler.class);
  }
}
