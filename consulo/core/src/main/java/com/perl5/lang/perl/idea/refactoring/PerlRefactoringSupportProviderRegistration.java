// <lang.refactoringSupport> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.idea.refactoring;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlRefactoringSupportProviderRegistration extends com.perl5.lang.perl.idea.refactoring.PerlRefactoringSupportProvider {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
