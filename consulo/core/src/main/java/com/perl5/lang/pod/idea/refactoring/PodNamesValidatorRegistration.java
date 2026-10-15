// <lang.namesValidator> in upstream perl5.plugin.common.main.xml
package com.perl5.lang.pod.idea.refactoring;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PodNamesValidatorRegistration extends com.perl5.lang.pod.idea.refactoring.PodNamesValidator {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.pod.PodLanguage.INSTANCE;
  }
}
