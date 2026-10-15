// <packageProcessor key="Future::AsyncAwait"> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.extensions.futureAsyncAwait;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public final class FutureAsyncAwaitPackageProcessorFutureAsyncawaitPackageprocessorRegistration implements com.perl5.lang.perl.extensions.packageprocessor.PerlPackageProcessorService.KeyedLazyInstance {
  private volatile com.perl5.lang.perl.extensions.packageprocessor.PerlPackageProcessor myInstance;

  @Override
  public String getKey() {
    return "Future::AsyncAwait";
  }

  @Override
  public com.perl5.lang.perl.extensions.packageprocessor.PerlPackageProcessor getInstance() {
    com.perl5.lang.perl.extensions.packageprocessor.PerlPackageProcessor instance = myInstance;
    if (instance == null) {
      myInstance = instance = new FutureAsyncAwaitPackageProcessor();
    }
    return instance;
  }
}
