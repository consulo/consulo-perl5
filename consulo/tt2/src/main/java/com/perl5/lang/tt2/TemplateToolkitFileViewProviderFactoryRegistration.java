// <lang.fileViewProviderFactory> in upstream perl5.lang.tt2.common.main.xml
package com.perl5.lang.tt2;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class TemplateToolkitFileViewProviderFactoryRegistration extends com.perl5.lang.tt2.TemplateToolkitFileViewProviderFactory implements consulo.language.file.LanguageFileViewProviderFactory {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.tt2.TemplateToolkitLanguage.INSTANCE;
  }
}
