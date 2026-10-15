// <completion.contributor> in upstream perl5.lang.mason.mason2.backend.main.xml
package com.perl5.lang.mason2.idea.completion;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class MasonCompletionContributorRegistration extends com.perl5.lang.mason2.idea.completion.MasonCompletionContributor {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
