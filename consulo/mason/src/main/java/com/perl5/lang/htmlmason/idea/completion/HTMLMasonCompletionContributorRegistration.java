// <completion.contributor> in upstream perl5.lang.mason.htmlmason.backend.main.xml
package com.perl5.lang.htmlmason.idea.completion;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class HTMLMasonCompletionContributorRegistration extends com.perl5.lang.htmlmason.idea.completion.HTMLMasonCompletionContributor {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
