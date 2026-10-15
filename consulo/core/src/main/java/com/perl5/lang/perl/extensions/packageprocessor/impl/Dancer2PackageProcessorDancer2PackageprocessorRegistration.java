// <packageProcessor key="Dancer2"> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.extensions.packageprocessor.impl;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public final class Dancer2PackageProcessorDancer2PackageprocessorRegistration implements com.perl5.lang.perl.extensions.packageprocessor.PerlPackageProcessorService.KeyedLazyInstance {
  private volatile com.perl5.lang.perl.extensions.packageprocessor.PerlPackageProcessor myInstance;

  @Override
  public String getKey() {
    return "Dancer2";
  }

  @Override
  public com.perl5.lang.perl.extensions.packageprocessor.PerlPackageProcessor getInstance() {
    com.perl5.lang.perl.extensions.packageprocessor.PerlPackageProcessor instance = myInstance;
    if (instance == null) {
      myInstance = instance = new Dancer2PackageProcessor();
    }
    return instance;
  }
}
