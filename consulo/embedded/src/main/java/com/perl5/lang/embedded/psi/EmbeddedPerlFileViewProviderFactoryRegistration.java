// <lang.fileViewProviderFactory> in upstream perl5.lang.embedded.common.main.xml
package com.perl5.lang.embedded.psi;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class EmbeddedPerlFileViewProviderFactoryRegistration extends com.perl5.lang.embedded.psi.EmbeddedPerlFileViewProviderFactory implements consulo.language.file.LanguageFileViewProviderFactory {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.embedded.EmbeddedPerlLanguage.INSTANCE;
  }
}
