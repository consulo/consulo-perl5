// <lang.psiStructureViewFactory> in upstream perl5.lang.embedded.structureView.main.xml
package consulo.perl5.embedded.registration;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlStructureViewFactoryEmbeddedPerlRegistration extends com.perl5.lang.perl.idea.structureView.PerlStructureViewFactory {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.embedded.EmbeddedPerlLanguage.INSTANCE;
  }
}
