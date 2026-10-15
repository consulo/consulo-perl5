// <lang.psiStructureViewFactory> in upstream perl5.plugin.structureView.main.xml
package com.perl5.lang.perl.idea.structureView;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlStructureViewFactoryRegistration extends com.perl5.lang.perl.idea.structureView.PerlStructureViewFactory {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
