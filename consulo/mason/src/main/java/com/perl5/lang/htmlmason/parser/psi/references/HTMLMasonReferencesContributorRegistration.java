// <psi.referenceContributor> in upstream perl5.lang.mason.htmlmason.backend.main.xml
package com.perl5.lang.htmlmason.parser.psi.references;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class HTMLMasonReferencesContributorRegistration extends com.perl5.lang.htmlmason.parser.psi.references.HTMLMasonReferencesContributor {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
