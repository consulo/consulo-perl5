// <lang.findUsagesProvider> in upstream perl5.lang.mason.mason2.backend.main.xml
package com.perl5.lang.mason2.idea.findusages;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class MasonTemplatingFindUsagesProviderMason2TemplatingLanguageRegistration extends com.perl5.lang.mason2.idea.findusages.MasonTemplatingFindUsagesProvider {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.mason2.Mason2TemplatingLanguage.INSTANCE;
  }
}
