// <lang.fileViewProviderFactory> in upstream perl5.plugin.common.main.xml
package com.perl5.lang.perl.psi;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlFileViewProviderFactoryRegistration extends com.perl5.lang.perl.psi.PerlFileViewProviderFactory implements consulo.language.file.LanguageFileViewProviderFactory {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
