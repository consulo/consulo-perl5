// <lang.whiteSpaceFormattingStrategy> in upstream perl5.plugin.common.main.xml
package com.perl5.lang.perl.idea.formatter;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlWhiteSpaceFormattingStrategyRegistration extends com.perl5.lang.perl.idea.formatter.PerlWhiteSpaceFormattingStrategy {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
