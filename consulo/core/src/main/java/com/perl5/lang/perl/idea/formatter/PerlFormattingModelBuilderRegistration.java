// <lang.formatter> in upstream perl5.plugin.common.main.xml
package com.perl5.lang.perl.idea.formatter;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlFormattingModelBuilderRegistration extends com.perl5.lang.perl.idea.formatter.PerlFormattingModelBuilder {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
