// <group id="perl5.install"> in upstream perl5.plugin.backend.main.xml
package consulo.perl5.core.registration.action;

import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionParentRef;
import consulo.annotation.component.ActionRef;
import consulo.annotation.component.ActionRefAnchor;
import consulo.localize.LocalizeValue;
import consulo.ui.ex.action.DefaultActionGroup;

@ActionImpl(id = "perl5.install", parents = @ActionParentRef(value = @ActionRef(id = "perl5.tools"), anchor = ActionRefAnchor.LAST), children = {
    @ActionRef(type = com.perl5.lang.perl.idea.sdk.versionManager.PerlInstallActionRegistration.class)
  })
public class Perl5InstallGroup extends DefaultActionGroup {
  public Perl5InstallGroup() {
    setPopup(true);
    getTemplatePresentation().setText(LocalizeValue.localizeTODO(com.perl5.PerlBundle.message("group.perl5.install.text")));
  }
}
