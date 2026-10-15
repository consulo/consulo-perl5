// <lang.braceMatcher> in upstream perl5.lang.embedded.common.main.xml
package com.perl5.lang.embedded.idea.editor;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class EmbeddedPerlBraceMatcherRegistration extends com.perl5.lang.embedded.idea.editor.EmbeddedPerlBraceMatcher {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.embedded.EmbeddedPerlLanguage.INSTANCE;
  }
}
