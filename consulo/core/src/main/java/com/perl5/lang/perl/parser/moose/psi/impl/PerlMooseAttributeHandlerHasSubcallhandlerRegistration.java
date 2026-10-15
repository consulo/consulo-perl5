// <subCallHandler key="has"> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.parser.moose.psi.impl;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public final class PerlMooseAttributeHandlerHasSubcallhandlerRegistration implements com.perl5.lang.perl.psi.PerlSubCallHandler.KeyedLazyInstance {
  private volatile com.perl5.lang.perl.psi.PerlSubCallHandler<?> myInstance;

  @Override
  public String getKey() {
    return "has";
  }

  @Override
  public com.perl5.lang.perl.psi.PerlSubCallHandler<?> getInstance() {
    com.perl5.lang.perl.psi.PerlSubCallHandler<?> instance = myInstance;
    if (instance == null) {
      myInstance = instance = new PerlMooseAttributeHandler();
    }
    return instance;
  }
}
