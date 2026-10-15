// <subCallHandler key="mk_ro_accessors"> in upstream perl5.plugin.common.main.xml
package com.perl5.lang.perl.parser.Class.Accessor.psi.impl;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public final class PerlClassAccessorHandlerPerlClassAccessorRoHandlerMkRoAccessorsSubcallhandlerRegistration implements com.perl5.lang.perl.psi.PerlSubCallHandler.KeyedLazyInstance {
  private volatile com.perl5.lang.perl.psi.PerlSubCallHandler<?> myInstance;

  @Override
  public String getKey() {
    return "mk_ro_accessors";
  }

  @Override
  public com.perl5.lang.perl.psi.PerlSubCallHandler<?> getInstance() {
    com.perl5.lang.perl.psi.PerlSubCallHandler<?> instance = myInstance;
    if (instance == null) {
      myInstance = instance = new PerlClassAccessorHandler.PerlClassAccessorRoHandler();
    }
    return instance;
  }
}
