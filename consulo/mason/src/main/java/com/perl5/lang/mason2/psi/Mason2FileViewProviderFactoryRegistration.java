// <lang.fileViewProviderFactory> in upstream perl5.lang.mason.mason2.common.main.xml
package com.perl5.lang.mason2.psi;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class Mason2FileViewProviderFactoryRegistration extends com.perl5.lang.mason2.psi.Mason2FileViewProviderFactory implements consulo.language.file.LanguageFileViewProviderFactory {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.mason2.Mason2Language.INSTANCE;
  }
}
