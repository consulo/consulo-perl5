// <lang.namesValidator> in upstream perl5.lang.mojo.common.main.xml
package consulo.perl5.mojo.registration;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlNamesValidatorMojoliciousPerlRegistration extends com.perl5.lang.perl.idea.PerlNamesValidator {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.mojolicious.MojoliciousLanguage.INSTANCE;
  }
}
