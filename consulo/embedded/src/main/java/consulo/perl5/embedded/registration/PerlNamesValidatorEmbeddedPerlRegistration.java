// <lang.namesValidator> in upstream perl5.lang.embedded.common.main.xml
package consulo.perl5.embedded.registration;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlNamesValidatorEmbeddedPerlRegistration extends com.perl5.lang.perl.idea.PerlNamesValidator {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.embedded.EmbeddedPerlLanguage.INSTANCE;
  }
}
