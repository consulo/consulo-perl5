// <completion.contributor> in upstream perl5.lang.tt2.backend.main.xml
package com.perl5.lang.tt2.idea.completion;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class TemplateToolkitCompletionContributorRegistration extends com.perl5.lang.tt2.idea.completion.TemplateToolkitCompletionContributor {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.tt2.TemplateToolkitLanguage.INSTANCE;
  }
}
