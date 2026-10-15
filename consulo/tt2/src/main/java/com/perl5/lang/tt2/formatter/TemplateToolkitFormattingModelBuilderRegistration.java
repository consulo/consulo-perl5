// <lang.formatter> in upstream perl5.lang.tt2.common.main.xml
package com.perl5.lang.tt2.formatter;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class TemplateToolkitFormattingModelBuilderRegistration extends com.perl5.lang.tt2.formatter.TemplateToolkitFormattingModelBuilder {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.tt2.TemplateToolkitLanguage.INSTANCE;
  }
}
