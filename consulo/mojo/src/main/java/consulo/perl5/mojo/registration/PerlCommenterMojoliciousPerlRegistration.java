// <lang.commenter> in upstream perl5.lang.mojo.common.main.xml
package consulo.perl5.mojo.registration;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;

@ExtensionImpl
public class PerlCommenterMojoliciousPerlRegistration extends com.perl5.lang.perl.idea.editor.PerlCommenter {
  @Override
  public consulo.language.Language getLanguage() {
    return com.perl5.lang.mojolicious.MojoliciousLanguage.INSTANCE;
  }
}
