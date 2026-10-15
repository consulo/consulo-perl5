// <completion.contributor> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.idea.completion;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlCompletionContributorRegistration extends com.perl5.lang.perl.idea.completion.PerlCompletionContributor {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
