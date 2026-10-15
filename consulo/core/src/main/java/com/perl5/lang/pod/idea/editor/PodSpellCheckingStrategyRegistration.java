// <spellchecker.support> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.pod.idea.editor;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PodSpellCheckingStrategyRegistration extends com.perl5.lang.pod.idea.editor.PodSpellCheckingStrategy {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.pod.PodLanguage.INSTANCE;
  }
}
