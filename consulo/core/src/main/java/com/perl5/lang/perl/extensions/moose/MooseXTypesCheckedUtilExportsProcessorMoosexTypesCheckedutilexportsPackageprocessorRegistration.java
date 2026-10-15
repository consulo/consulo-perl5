// <packageProcessor key="MooseX::Types::CheckedUtilExports"> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.extensions.moose;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public final class MooseXTypesCheckedUtilExportsProcessorMoosexTypesCheckedutilexportsPackageprocessorRegistration implements com.perl5.lang.perl.extensions.packageprocessor.PerlPackageProcessorService.KeyedLazyInstance {
  private volatile com.perl5.lang.perl.extensions.packageprocessor.PerlPackageProcessor myInstance;

  @Override
  public String getKey() {
    return "MooseX::Types::CheckedUtilExports";
  }

  @Override
  public com.perl5.lang.perl.extensions.packageprocessor.PerlPackageProcessor getInstance() {
    com.perl5.lang.perl.extensions.packageprocessor.PerlPackageProcessor instance = myInstance;
    if (instance == null) {
      myInstance = instance = new MooseXTypesCheckedUtilExportsProcessor();
    }
    return instance;
  }
}
