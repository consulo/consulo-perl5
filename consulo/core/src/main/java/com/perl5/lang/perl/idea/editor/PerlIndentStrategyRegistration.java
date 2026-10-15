// <lang.indentStrategy> in upstream perl5.plugin.common.main.xml
package com.perl5.lang.perl.idea.editor;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlIndentStrategyRegistration extends com.perl5.lang.perl.idea.editor.PerlIndentStrategy {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
