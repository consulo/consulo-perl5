package com.perl5.lang.perl.idea.sdk.versionManager.plenv;

import com.perl5.lang.perl.idea.sdk.PerlHandlerBean;
import consulo.annotation.component.ExtensionImpl;

/**
 * Consulo registration of {@code <versionManagerHandler key="plenv"/>} from the upstream perl5.plugin.plenv.main.xml
 */
@ExtensionImpl
public final class PlenvHandlerRegistration extends PerlHandlerBean {
  public PlenvHandlerRegistration() {
    super("com.perl5.versionManagerHandler", "plenv", PlenvHandler.class);
  }
}
