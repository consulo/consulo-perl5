// <enterHandlerDelegate> in upstream perl5.lang.embedded.common.main.xml
package com.perl5.lang.embedded.idea.editor.smartkeys;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl(id = "EmbeddedPerlEnterHandler", order = "after PerlEnterHandler")
public class EmbeddedPerlEnterDelegateRegistration extends com.perl5.lang.embedded.idea.editor.smartkeys.EmbeddedPerlEnterDelegate {
}
