// <lang.indentStrategy> in upstream perl5.lang.mason.mason2.common.main.xml
package com.perl5.lang.mason2.idea.editor;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class MasonIndentStrategyRegistration extends com.perl5.lang.mason2.idea.editor.MasonIndentStrategy {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.mason2.Mason2Language.INSTANCE;
  }
}
