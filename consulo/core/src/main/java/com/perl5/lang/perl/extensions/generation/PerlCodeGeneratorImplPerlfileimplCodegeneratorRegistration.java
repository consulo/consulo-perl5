// <codeGenerator forClass="com.perl5.lang.perl.psi.impl.PerlFileImpl"> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.extensions.generation;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public final class PerlCodeGeneratorImplPerlfileimplCodegeneratorRegistration implements com.perl5.lang.perl.extensions.PerlCodeGenerator.ClassExtensionPoint {
  private volatile com.perl5.lang.perl.extensions.PerlCodeGenerator myInstance;

  @Override
  public Class<?> getForClass() {
    return com.perl5.lang.perl.psi.impl.PerlFileImpl.class;
  }

  @Override
  public com.perl5.lang.perl.extensions.PerlCodeGenerator getInstance() {
    com.perl5.lang.perl.extensions.PerlCodeGenerator instance = myInstance;
    if (instance == null) {
      myInstance = instance = new PerlCodeGeneratorImpl();
    }
    return instance;
  }
}
