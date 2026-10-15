// <lang.braceMatcher> in upstream perl5.lang.mason.mason2.common.main.xml
package com.perl5.lang.mason2.idea.editor;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class MasonBraceMatcherRegistration extends com.perl5.lang.mason2.idea.editor.MasonBraceMatcher {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.mason2.Mason2Language.INSTANCE;
  }
}
