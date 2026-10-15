// <lang.findUsagesProvider> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.idea.findusages;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlFindUsagesProviderRegistration extends com.perl5.lang.perl.idea.findusages.PerlFindUsagesProvider {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
