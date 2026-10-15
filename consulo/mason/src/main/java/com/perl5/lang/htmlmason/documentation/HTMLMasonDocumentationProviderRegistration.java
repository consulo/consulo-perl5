// <documentationProvider> in upstream perl5.lang.mason.htmlmason.backend.main.xml
package com.perl5.lang.htmlmason.documentation;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class HTMLMasonDocumentationProviderRegistration extends com.perl5.lang.htmlmason.documentation.HTMLMasonDocumentationProvider implements consulo.language.editor.documentation.LanguageDocumentationProvider {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.htmlmason.HTMLMasonLanguage.INSTANCE;
  }
}
