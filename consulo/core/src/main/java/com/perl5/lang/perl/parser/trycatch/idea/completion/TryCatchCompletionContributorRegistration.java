// <completion.contributor> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.parser.trycatch.idea.completion;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class TryCatchCompletionContributorRegistration extends com.perl5.lang.perl.parser.trycatch.idea.completion.TryCatchCompletionContributor {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
