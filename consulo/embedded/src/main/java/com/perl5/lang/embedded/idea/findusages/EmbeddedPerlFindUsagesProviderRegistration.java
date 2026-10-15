// <lang.findUsagesProvider> in upstream perl5.lang.embedded.backend.main.xml
package com.perl5.lang.embedded.idea.findusages;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class EmbeddedPerlFindUsagesProviderRegistration extends com.perl5.lang.embedded.idea.findusages.EmbeddedPerlFindUsagesProvider {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.embedded.EmbeddedPerlLanguage.INSTANCE;
  }
}
