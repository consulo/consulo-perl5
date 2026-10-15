// <lang.psiStructureViewFactory> in upstream perl5.lang.mason.mason2.structureView.main.xml
package consulo.perl5.mason.registration;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlStructureViewFactoryMason2Registration extends com.perl5.lang.perl.idea.structureView.PerlStructureViewFactory {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.mason2.Mason2Language.INSTANCE;
  }
}
