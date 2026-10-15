// <lang.formatter> in upstream perl5.lang.mojo.common.main.xml
package com.perl5.lang.mojolicious.idea.formatter;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class MojoliciousFormattingModelBuilderRegistration extends com.perl5.lang.mojolicious.idea.formatter.MojoliciousFormattingModelBuilder {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.mojolicious.MojoliciousLanguage.INSTANCE;
  }
}
