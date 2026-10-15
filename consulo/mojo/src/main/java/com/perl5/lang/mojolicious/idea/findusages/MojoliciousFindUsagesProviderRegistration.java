// <lang.findUsagesProvider> in upstream perl5.lang.mojo.backend.main.xml
package com.perl5.lang.mojolicious.idea.findusages;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class MojoliciousFindUsagesProviderRegistration extends com.perl5.lang.mojolicious.idea.findusages.MojoliciousFindUsagesProvider {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.mojolicious.MojoliciousLanguage.INSTANCE;
  }
}
