// <spellchecker.support> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.idea.editor;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlSpellCheckingStrategyRegistration extends com.perl5.lang.perl.idea.editor.PerlSpellCheckingStrategy {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
