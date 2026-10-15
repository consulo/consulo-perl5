// <namespaceDefinitionHalder forClass="com.perl5.lang.perl.psi.PerlNamespaceDefinitionElement"> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.psi;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public final class PerlDefaultBackendNamespaceDefinitionHandlerPerlnamespacedefinitionelementNamespacedefinitionhalderRegistration implements com.perl5.lang.perl.psi.PerlNamespaceDefinitionHandlerKt.ClassExtensionPoint {
  private volatile com.perl5.lang.perl.psi.PerlNamespaceDefinitionHandler<?> myInstance;

  @Override
  public Class<?> getForClass() {
    return com.perl5.lang.perl.psi.PerlNamespaceDefinitionElement.class;
  }

  @Override
  public com.perl5.lang.perl.psi.PerlNamespaceDefinitionHandler<?> getInstance() {
    com.perl5.lang.perl.psi.PerlNamespaceDefinitionHandler<?> instance = myInstance;
    if (instance == null) {
      myInstance = instance = new PerlDefaultBackendNamespaceDefinitionHandler();
    }
    return instance;
  }
}
