// <codeInsight.overrideMethod> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.idea.generation.handlers;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlOverrideMethodHandlerRegistration extends com.perl5.lang.perl.idea.generation.handlers.PerlOverrideMethodHandler {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
