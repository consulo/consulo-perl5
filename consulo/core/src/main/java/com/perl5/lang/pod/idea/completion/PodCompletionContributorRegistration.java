// <completion.contributor> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.pod.idea.completion;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PodCompletionContributorRegistration extends com.perl5.lang.pod.idea.completion.PodCompletionContributor {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.pod.PodLanguage.INSTANCE;
  }
}
