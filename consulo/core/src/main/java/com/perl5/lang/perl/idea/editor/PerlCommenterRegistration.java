// <lang.commenter> in upstream perl5.plugin.common.main.xml
package com.perl5.lang.perl.idea.editor;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlCommenterRegistration extends com.perl5.lang.perl.idea.editor.PerlCommenter {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
