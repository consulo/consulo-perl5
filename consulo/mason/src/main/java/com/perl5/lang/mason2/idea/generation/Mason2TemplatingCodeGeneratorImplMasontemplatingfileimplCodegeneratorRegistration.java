// <codeGenerator forClass="com.perl5.lang.mason2.psi.impl.MasonTemplatingFileImpl"> in upstream perl5.lang.mason.mason2.backend.main.xml
package com.perl5.lang.mason2.idea.generation;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public final class Mason2TemplatingCodeGeneratorImplMasontemplatingfileimplCodegeneratorRegistration implements com.perl5.lang.perl.extensions.PerlCodeGenerator.ClassExtensionPoint {
  private volatile com.perl5.lang.perl.extensions.PerlCodeGenerator myInstance;

  @Override
  public Class<?> getForClass() {
    return com.perl5.lang.mason2.psi.impl.MasonTemplatingFileImpl.class;
  }

  @Override
  public com.perl5.lang.perl.extensions.PerlCodeGenerator getInstance() {
    com.perl5.lang.perl.extensions.PerlCodeGenerator instance = myInstance;
    if (instance == null) {
      myInstance = instance = new Mason2TemplatingCodeGeneratorImpl();
    }
    return instance;
  }
}
