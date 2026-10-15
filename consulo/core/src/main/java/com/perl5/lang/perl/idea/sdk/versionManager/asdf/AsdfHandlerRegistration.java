package com.perl5.lang.perl.idea.sdk.versionManager.asdf;

import com.perl5.lang.perl.idea.sdk.PerlHandlerBean;
import consulo.annotation.component.ExtensionImpl;

/**
 * Consulo registration of {@code <versionManagerHandler key="asdf"/>} from the upstream perl5.plugin.asdf.main.xml
 */
@ExtensionImpl
public final class AsdfHandlerRegistration extends PerlHandlerBean {
  public AsdfHandlerRegistration() {
    super("com.perl5.versionManagerHandler", "asdf", AsdfHandler.class);
  }
}
