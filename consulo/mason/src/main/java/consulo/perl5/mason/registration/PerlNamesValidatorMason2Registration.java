// <lang.namesValidator> in upstream perl5.lang.mason.mason2.common.main.xml
package consulo.perl5.mason.registration;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlNamesValidatorMason2Registration extends com.perl5.lang.perl.idea.PerlNamesValidator {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.mason2.Mason2Language.INSTANCE;
  }
}
