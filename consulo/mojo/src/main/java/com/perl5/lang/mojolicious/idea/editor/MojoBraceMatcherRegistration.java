// <lang.braceMatcher> in upstream perl5.lang.mojo.common.main.xml
package com.perl5.lang.mojolicious.idea.editor;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class MojoBraceMatcherRegistration extends com.perl5.lang.mojolicious.idea.editor.MojoBraceMatcher {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.mojolicious.MojoliciousLanguage.INSTANCE;
  }
}
