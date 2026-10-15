// <lang.treePatcher> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.pod.parser;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PodTreePatcherRegistration extends com.perl5.lang.pod.parser.PodTreePatcher {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.pod.PodLanguage.INSTANCE;
  }
}
