// <lang.foldingBuilder> in upstream perl5.lang.embedded.common.main.xml
package com.perl5.lang.embedded.idea.folding;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class EmbeddedPerlFoldingBuilderRegistration extends com.perl5.lang.embedded.idea.folding.EmbeddedPerlFoldingBuilder {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.embedded.EmbeddedPerlLanguage.INSTANCE;
  }
}
