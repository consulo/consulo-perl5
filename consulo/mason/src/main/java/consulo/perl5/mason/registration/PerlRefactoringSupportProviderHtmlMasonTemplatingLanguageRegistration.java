// <lang.refactoringSupport> in upstream perl5.lang.mason.htmlmason.backend.main.xml
package consulo.perl5.mason.registration;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlRefactoringSupportProviderHtmlMasonTemplatingLanguageRegistration extends com.perl5.lang.perl.idea.refactoring.PerlRefactoringSupportProvider {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.htmlmason.HTMLMasonLanguage.INSTANCE;
  }
}
