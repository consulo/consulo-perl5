// <lang.foldingBuilder> in upstream perl5.lang.mojo.common.main.xml
package com.perl5.lang.mojolicious.idea.folding;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class MojoliciousFoldingBuilderRegistration extends com.perl5.lang.mojolicious.idea.folding.MojoliciousFoldingBuilder {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.mojolicious.MojoliciousLanguage.INSTANCE;
  }
}
