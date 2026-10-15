package com.perl5.lang.perl.idea.sdk.versionManager.berrybrew;

import com.perl5.lang.perl.idea.sdk.PerlHandlerBean;
import consulo.annotation.component.ExtensionImpl;

/**
 * Consulo registration of {@code <versionManagerHandler key="berrybrew"/>} from the upstream perl5.plugin.berrybrew.main.xml
 */
@ExtensionImpl
public final class BerryBrewHandlerRegistration extends PerlHandlerBean {
  public BerryBrewHandlerRegistration() {
    super("com.perl5.versionManagerHandler", "berrybrew", BerryBrewHandler.class);
  }
}
