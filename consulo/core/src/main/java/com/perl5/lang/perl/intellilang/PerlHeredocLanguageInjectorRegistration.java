// <multiHostInjector> in upstream perl5.plugin.intelliLang.main.xml
package com.perl5.lang.perl.intellilang;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl(order = "before TemporaryPlacesInjector")
public class PerlHeredocLanguageInjectorRegistration extends com.perl5.lang.perl.intellilang.PerlHeredocLanguageInjector {
}
