// <elementDescriptionProvider> in upstream perl5.lang.mojo.common.main.xml
package com.perl5.lang.mojolicious.psi;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl(id = "perl5.mojo", order = "before perl5")
public class MojoElementDescriptionProviderRegistration extends com.perl5.lang.mojolicious.psi.MojoElementDescriptionProvider {
}
