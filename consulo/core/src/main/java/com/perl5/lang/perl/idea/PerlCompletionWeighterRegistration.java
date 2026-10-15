// <weigher> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.idea;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl(order = "before priority")
public class PerlCompletionWeighterRegistration extends com.perl5.lang.perl.idea.PerlCompletionWeighter {
}
