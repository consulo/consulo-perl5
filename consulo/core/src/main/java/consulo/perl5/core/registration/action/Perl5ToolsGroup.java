// <group id="perl5.tools"> in upstream perl5.plugin.backend.main.xml
package consulo.perl5.core.registration.action;

import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionParentRef;
import consulo.annotation.component.ActionRef;
import consulo.annotation.component.ActionRefAnchor;
import consulo.localize.LocalizeValue;
import consulo.ui.ex.action.DefaultActionGroup;

@ActionImpl(id = "perl5.tools", parents = @ActionParentRef(value = @ActionRef(id = "ToolsMenu"), anchor = ActionRefAnchor.LAST), children = {
    @ActionRef(type = com.perl5.lang.perl.idea.actions.PerlRegenerateXSubsActionRegistration.class),
    @ActionRef(type = com.perl5.lang.perl.idea.actions.PerlFormatWithPerlTidyActionRegistration.class),
    @ActionRef(type = com.perl5.lang.perl.idea.actions.PerlDeparseFileActionRegistration.class),
    @ActionRef(type = com.perl5.lang.perl.idea.actions.PerlSynInterpreterActionRegistration.class)
  })
public class Perl5ToolsGroup extends DefaultActionGroup {
  public Perl5ToolsGroup() {
    setPopup(true);
    getTemplatePresentation().setText(LocalizeValue.localizeTODO(com.perl5.PerlBundle.message("group.perl5.tools.text")));
    getTemplatePresentation().setIcon(consulo.perl5.icon.Perl5IconGroup.perl5language());
  }
}
