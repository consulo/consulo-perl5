// <lang.braceMatcher> in upstream perl5.lang.tt2.common.main.xml
package com.perl5.lang.tt2.idea.editor;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class TemplateToolkitBraceMatcherRegistration extends com.perl5.lang.tt2.idea.editor.TemplateToolkitBraceMatcher {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.tt2.TemplateToolkitLanguage.INSTANCE;
  }
}
