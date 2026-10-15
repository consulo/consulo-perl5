// <namespaceDefinitionHalder forClass="com.perl5.lang.mason2.psi.impl.MasonNamespaceDefinitionImpl"> in upstream perl5.lang.mason.mason2.backend.main.xml
package com.perl5.lang.mason2.psi.impl;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public final class MasonBackendNamespaceDefinitionHandlerMasonnamespacedefinitionimplNamespacedefinitionhalderRegistration implements com.perl5.lang.perl.psi.PerlNamespaceDefinitionHandlerKt.ClassExtensionPoint {
  private volatile com.perl5.lang.perl.psi.PerlNamespaceDefinitionHandler<?> myInstance;

  @Override
  public Class<?> getForClass() {
    return com.perl5.lang.mason2.psi.impl.MasonNamespaceDefinitionImpl.class;
  }

  @Override
  public com.perl5.lang.perl.psi.PerlNamespaceDefinitionHandler<?> getInstance() {
    com.perl5.lang.perl.psi.PerlNamespaceDefinitionHandler<?> instance = myInstance;
    if (instance == null) {
      myInstance = instance = new MasonBackendNamespaceDefinitionHandler();
    }
    return instance;
  }
}
