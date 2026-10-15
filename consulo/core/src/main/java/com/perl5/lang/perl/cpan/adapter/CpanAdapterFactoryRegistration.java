package com.perl5.lang.perl.cpan.adapter;

import consulo.annotation.component.ExtensionImpl;

/**
 * Consulo registration of CpanAdapter.Factory (upstream registers it in xml; Consulo's annotation processor does not support nested classes).
 */
@ExtensionImpl(order = "last")
public class CpanAdapterFactoryRegistration extends CpanAdapter.Factory {
}
