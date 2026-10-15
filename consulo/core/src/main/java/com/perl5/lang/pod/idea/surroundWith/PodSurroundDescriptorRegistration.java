// <lang.surroundDescriptor> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.pod.idea.surroundWith;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PodSurroundDescriptorRegistration extends com.perl5.lang.pod.idea.surroundWith.PodSurroundDescriptor {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.pod.PodLanguage.INSTANCE;
  }
}
