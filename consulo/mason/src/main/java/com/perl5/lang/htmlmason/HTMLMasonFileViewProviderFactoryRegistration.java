// <lang.fileViewProviderFactory> in upstream perl5.lang.mason.htmlmason.common.main.xml
package com.perl5.lang.htmlmason;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class HTMLMasonFileViewProviderFactoryRegistration extends com.perl5.lang.htmlmason.HTMLMasonFileViewProviderFactory implements consulo.language.file.LanguageFileViewProviderFactory {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.htmlmason.HTMLMasonLanguage.INSTANCE;
  }
}
