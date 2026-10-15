// <documentationProvider> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.pod.idea.documentation;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PodDocumentationProviderRegistration extends com.perl5.lang.pod.idea.documentation.PodDocumentationProvider implements consulo.language.editor.documentation.LanguageDocumentationProvider {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.pod.PodLanguage.INSTANCE;
  }
}
