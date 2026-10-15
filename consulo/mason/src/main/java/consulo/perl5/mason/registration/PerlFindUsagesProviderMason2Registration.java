// <lang.findUsagesProvider> in upstream perl5.lang.mason.mason2.backend.main.xml
package consulo.perl5.mason.registration;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlFindUsagesProviderMason2Registration extends com.perl5.lang.perl.idea.findusages.PerlFindUsagesProvider {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.mason2.Mason2Language.INSTANCE;
  }
}
