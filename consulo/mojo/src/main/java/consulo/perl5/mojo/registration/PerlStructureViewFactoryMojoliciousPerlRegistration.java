// <lang.psiStructureViewFactory> in upstream perl5.lang.mojo.structureView.main.xml
package consulo.perl5.mojo.registration;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlStructureViewFactoryMojoliciousPerlRegistration extends com.perl5.lang.perl.idea.structureView.PerlStructureViewFactory {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.mojolicious.MojoliciousLanguage.INSTANCE;
  }
}
