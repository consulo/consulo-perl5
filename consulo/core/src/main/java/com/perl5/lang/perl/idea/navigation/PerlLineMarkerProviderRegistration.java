// <codeInsight.lineMarkerProvider> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.idea.navigation;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlLineMarkerProviderRegistration extends com.perl5.lang.perl.idea.navigation.PerlLineMarkerProvider {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
