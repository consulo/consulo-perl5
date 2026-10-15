// <packageProcessor key="CGI"> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.extensions.cgi;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public final class PerlCgiPackageProcessorCgiPackageprocessorRegistration implements com.perl5.lang.perl.extensions.packageprocessor.PerlPackageProcessorService.KeyedLazyInstance {
  private volatile com.perl5.lang.perl.extensions.packageprocessor.PerlPackageProcessor myInstance;

  @Override
  public String getKey() {
    return "CGI";
  }

  @Override
  public com.perl5.lang.perl.extensions.packageprocessor.PerlPackageProcessor getInstance() {
    com.perl5.lang.perl.extensions.packageprocessor.PerlPackageProcessor instance = myInstance;
    if (instance == null) {
      myInstance = instance = new PerlCgiPackageProcessor();
    }
    return instance;
  }
}
