// <lang.formatter> in upstream perl5.lang.mason.htmlmason.common.main.xml
package com.perl5.lang.htmlmason.idea.formatter;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class HTMLMasonFormattingModelBuilderRegistration extends com.perl5.lang.htmlmason.idea.formatter.HTMLMasonFormattingModelBuilder {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.htmlmason.HTMLMasonLanguage.INSTANCE;
  }
}
