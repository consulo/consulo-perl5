// <lang.refactoringSupport> in upstream perl5.lang.tt2.backend.main.xml
package com.perl5.lang.tt2.idea.refactoring;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class TemplateToolkitRefactoringSupportProviderRegistration extends com.perl5.lang.tt2.idea.refactoring.TemplateToolkitRefactoringSupportProvider {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.tt2.TemplateToolkitLanguage.INSTANCE;
  }
}
