// <lang.foldingBuilder> in upstream perl5.lang.mason.mason2.common.main.xml
package com.perl5.lang.mason2.idea.folding;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class MasonFoldingBuilderRegistration extends com.perl5.lang.mason2.idea.folding.MasonFoldingBuilder {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.mason2.Mason2Language.INSTANCE;
  }
}
