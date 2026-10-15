// <codeInsight.parameterInfo> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.idea.codeInsight;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlParameterInfoHandlerRegistration extends com.perl5.lang.perl.idea.codeInsight.PerlParameterInfoHandler {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
