// <lang.findUsagesProvider> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.pod.idea.findusages;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PodFindUsagesProviderRegistration extends com.perl5.lang.pod.idea.findusages.PodFindUsagesProvider {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.pod.PodLanguage.INSTANCE;
  }
}
