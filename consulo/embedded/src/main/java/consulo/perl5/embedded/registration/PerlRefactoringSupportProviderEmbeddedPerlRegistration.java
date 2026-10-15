// <lang.refactoringSupport> in upstream perl5.lang.embedded.backend.main.xml
package consulo.perl5.embedded.registration;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlRefactoringSupportProviderEmbeddedPerlRegistration extends com.perl5.lang.perl.idea.refactoring.PerlRefactoringSupportProvider {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.embedded.EmbeddedPerlLanguage.INSTANCE;
  }
}
