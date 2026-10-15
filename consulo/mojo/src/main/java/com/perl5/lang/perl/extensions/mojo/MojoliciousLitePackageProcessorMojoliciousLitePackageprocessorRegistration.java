// <packageProcessor key="Mojolicious::Lite"> in upstream perl5.lang.mojo.common.main.xml
package com.perl5.lang.perl.extensions.mojo;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public final class MojoliciousLitePackageProcessorMojoliciousLitePackageprocessorRegistration implements com.perl5.lang.perl.extensions.packageprocessor.PerlPackageProcessorService.KeyedLazyInstance {
  private volatile com.perl5.lang.perl.extensions.packageprocessor.PerlPackageProcessor myInstance;

  @Override
  public String getKey() {
    return "Mojolicious::Lite";
  }

  @Override
  public com.perl5.lang.perl.extensions.packageprocessor.PerlPackageProcessor getInstance() {
    com.perl5.lang.perl.extensions.packageprocessor.PerlPackageProcessor instance = myInstance;
    if (instance == null) {
      myInstance = instance = new MojoliciousLitePackageProcessor();
    }
    return instance;
  }
}
