// <lang.psiStructureViewFactory> in upstream perl5.plugin.structureView.main.xml
package com.perl5.lang.pod.idea.structureView;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PodStructureViewFactoryRegistration extends com.perl5.lang.pod.idea.structureView.PodStructureViewFactory {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.pod.PodLanguage.INSTANCE;
  }
}
