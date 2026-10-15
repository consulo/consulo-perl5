// <typeHierarchyProvider> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.idea.hierarchy.namespace;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlTypeHierarchyProviderRegistration extends com.perl5.lang.perl.idea.hierarchy.namespace.PerlTypeHierarchyProvider {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
