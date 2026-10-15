// <lang.fileViewProviderFactory> in upstream perl5.lang.mojo.common.main.xml
package com.perl5.lang.mojolicious.psi;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class MojoliciousFileViewProviderFactoryRegistration extends com.perl5.lang.mojolicious.psi.MojoliciousFileViewProviderFactory implements consulo.language.file.LanguageFileViewProviderFactory {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.mojolicious.MojoliciousLanguage.INSTANCE;
  }
}
