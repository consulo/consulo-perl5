// <lang.formatter> in upstream perl5.lang.embedded.common.main.xml
package com.perl5.lang.embedded.idea.formatter;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class EmbeddedPerlFormattingModelBuilderRegistration extends com.perl5.lang.embedded.idea.formatter.EmbeddedPerlFormattingModelBuilder {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.embedded.EmbeddedPerlLanguage.INSTANCE;
  }
}
