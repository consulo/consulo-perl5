// <codeInsight.lineMarkerProvider> in upstream perl5.lang.mason.htmlmason.backend.main.xml
package com.perl5.lang.htmlmason.idea.navigation;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class HTMLMasonLineMarkerProviderRegistration extends com.perl5.lang.htmlmason.idea.navigation.HTMLMasonLineMarkerProvider {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.htmlmason.HTMLMasonLanguage.INSTANCE;
  }
}
