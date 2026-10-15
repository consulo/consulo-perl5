// <lang.indentStrategy> in upstream perl5.lang.mason.htmlmason.common.main.xml
package com.perl5.lang.htmlmason.idea.editor;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class HTMLMasonIndentStrategyRegistration extends com.perl5.lang.htmlmason.idea.editor.HTMLMasonIndentStrategy {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.htmlmason.HTMLMasonLanguage.INSTANCE;
  }
}
