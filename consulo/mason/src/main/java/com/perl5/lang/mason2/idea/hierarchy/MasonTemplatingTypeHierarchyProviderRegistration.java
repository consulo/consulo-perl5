// <typeHierarchyProvider> in upstream perl5.lang.mason.mason2.backend.main.xml
package com.perl5.lang.mason2.idea.hierarchy;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class MasonTemplatingTypeHierarchyProviderRegistration extends com.perl5.lang.mason2.idea.hierarchy.MasonTemplatingTypeHierarchyProvider {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.mason2.Mason2TemplatingLanguage.INSTANCE;
  }
}
