package com.perl5.lang.perl.idea.sdk.versionManager.perlbrew;

import com.perl5.lang.perl.idea.sdk.PerlHandlerBean;
import consulo.annotation.component.ExtensionImpl;

/**
 * Consulo registration of {@code <versionManagerHandler key="perlbrew"/>} from the upstream perl5.plugin.perlbrew.main.xml
 */
@ExtensionImpl
public final class PerlBrewHandlerRegistration extends PerlHandlerBean {
  public PerlBrewHandlerRegistration() {
    super("com.perl5.versionManagerHandler", "perlbrew", PerlBrewHandler.class);
  }
}
