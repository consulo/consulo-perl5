// <lang.foldingBuilder> in upstream perl5.plugin.common.main.xml
package com.perl5.lang.perl.idea.folding;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlFoldingBuilderRegistration extends com.perl5.lang.perl.idea.folding.PerlFoldingBuilder {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
