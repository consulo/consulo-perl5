package com.perl5.lang.perl.cpanminus.adapter;

import consulo.annotation.component.ExtensionImpl;

/**
 * Consulo registration of CpanminusAdapter.Factory (upstream registers it in xml; Consulo's annotation processor does not support nested classes).
 */
@ExtensionImpl
public class CpanminusAdapterFactoryRegistration extends CpanminusAdapter.Factory {
}
