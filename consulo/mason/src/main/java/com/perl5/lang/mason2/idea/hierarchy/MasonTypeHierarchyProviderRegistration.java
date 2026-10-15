// <typeHierarchyProvider> in upstream perl5.lang.mason.mason2.backend.main.xml
package com.perl5.lang.mason2.idea.hierarchy;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class MasonTypeHierarchyProviderRegistration extends com.perl5.lang.mason2.idea.hierarchy.MasonTypeHierarchyProvider {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.mason2.Mason2Language.INSTANCE;
  }
}
