// <typeHierarchyProvider> in upstream perl5.lang.mason.htmlmason.backend.main.xml
package com.perl5.lang.htmlmason.idea.hierarchy;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class HTMLMasonTypeHierarchyProviderRegistration extends com.perl5.lang.htmlmason.idea.hierarchy.HTMLMasonTypeHierarchyProvider {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.htmlmason.HTMLMasonLanguage.INSTANCE;
  }
}
