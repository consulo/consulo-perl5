// <lang.namesValidator> in upstream perl5.plugin.common.main.xml
package com.perl5.lang.perl.idea;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlNamesValidatorRegistration extends com.perl5.lang.perl.idea.PerlNamesValidator {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
