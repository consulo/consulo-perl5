// <subCallHandler key="helper"> in upstream perl5.lang.mojo.backend.main.xml
package com.perl5.lang.mojolicious.psi.impl;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public final class MojoHelperHandlerHelperSubcallhandlerRegistration implements com.perl5.lang.perl.psi.PerlSubCallHandler.KeyedLazyInstance {
  private volatile com.perl5.lang.perl.psi.PerlSubCallHandler<?> myInstance;

  @Override
  public String getKey() {
    return "helper";
  }

  @Override
  public com.perl5.lang.perl.psi.PerlSubCallHandler<?> getInstance() {
    com.perl5.lang.perl.psi.PerlSubCallHandler<?> instance = myInstance;
    if (instance == null) {
      myInstance = instance = new MojoHelperHandler();
    }
    return instance;
  }
}
