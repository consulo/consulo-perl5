// <lang.smartEnterProcessor> in upstream perl5.plugin.common.main.xml
package com.perl5.lang.perl.idea.editor.smartkeys;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlSmartEnterProcessorRegistration extends com.perl5.lang.perl.idea.editor.smartkeys.PerlSmartEnterProcessor {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
