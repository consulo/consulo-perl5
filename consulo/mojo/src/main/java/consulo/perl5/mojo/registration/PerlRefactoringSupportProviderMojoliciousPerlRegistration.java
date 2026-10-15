// <lang.refactoringSupport> in upstream perl5.lang.mojo.backend.main.xml
package consulo.perl5.mojo.registration;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlRefactoringSupportProviderMojoliciousPerlRegistration extends com.perl5.lang.perl.idea.refactoring.PerlRefactoringSupportProvider {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.mojolicious.MojoliciousLanguage.INSTANCE;
  }
}
