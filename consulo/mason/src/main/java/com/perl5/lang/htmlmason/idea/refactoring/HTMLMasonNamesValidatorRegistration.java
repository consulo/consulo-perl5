// <lang.namesValidator> in upstream perl5.lang.mason.htmlmason.common.main.xml
package com.perl5.lang.htmlmason.idea.refactoring;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class HTMLMasonNamesValidatorRegistration extends com.perl5.lang.htmlmason.idea.refactoring.HTMLMasonNamesValidator {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.htmlmason.HTMLMasonLanguage.INSTANCE;
  }
}
