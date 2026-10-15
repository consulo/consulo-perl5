// <group id="perl5.mojo.NewGroup"> in upstream perl5.lang.mojo.backend.main.xml
package consulo.perl5.mojo.registration.action;

import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionParentRef;
import consulo.annotation.component.ActionRef;
import consulo.annotation.component.ActionRefAnchor;
import consulo.localize.LocalizeValue;
import consulo.ui.ex.action.DefaultActionGroup;

@ActionImpl(id = "perl5.mojo.NewGroup", parents = @ActionParentRef(value = @ActionRef(id = "NewGroup"), anchor = ActionRefAnchor.AFTER, relatedToAction = @ActionRef(id = "Perl5.NewFile")), children = {
    @ActionRef(type = com.perl5.lang.mojolicious.idea.ide.MojoCreateFileFromTemplateActionRegistration.class),
    @ActionRef(type = com.perl5.lang.mojolicious.idea.actions.MojoGenerateAppActionRegistration.class),
    @ActionRef(type = com.perl5.lang.mojolicious.idea.actions.MojoGenerateLiteAppActionRegistration.class),
    @ActionRef(type = com.perl5.lang.mojolicious.idea.actions.MojoGeneratePluginActionRegistration.class)
  })
public class Perl5MojoNewGroup extends DefaultActionGroup {
  public Perl5MojoNewGroup() {
    setPopup(false);
  }
}
