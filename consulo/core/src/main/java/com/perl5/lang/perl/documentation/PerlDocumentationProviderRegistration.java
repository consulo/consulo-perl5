// <documentationProvider> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.documentation;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlDocumentationProviderRegistration extends com.perl5.lang.perl.documentation.PerlDocumentationProvider implements consulo.language.editor.documentation.LanguageDocumentationProvider {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
