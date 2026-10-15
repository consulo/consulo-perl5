// <lang.foldingBuilder> in upstream perl5.plugin.common.main.xml
package com.perl5.lang.pod.idea.editor;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PodFoldingBuilderRegistration extends com.perl5.lang.pod.idea.editor.PodFoldingBuilder {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.pod.PodLanguage.INSTANCE;
  }
}
