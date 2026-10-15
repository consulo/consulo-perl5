// <psi.referenceContributor> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.psi.references;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlReferencesContributorRegistration extends com.perl5.lang.perl.psi.references.PerlReferencesContributor {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
