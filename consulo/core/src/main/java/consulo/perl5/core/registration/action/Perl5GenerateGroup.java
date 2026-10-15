// <group id="perl5.GenerateGroup"> in upstream perl5.plugin.backend.main.xml
package consulo.perl5.core.registration.action;

import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionParentRef;
import consulo.annotation.component.ActionRef;
import consulo.annotation.component.ActionRefAnchor;
import consulo.localize.LocalizeValue;
import consulo.ui.ex.action.DefaultActionGroup;

@ActionImpl(id = "perl5.GenerateGroup", parents = @ActionParentRef(value = @ActionRef(id = "GenerateGroup"), anchor = ActionRefAnchor.FIRST), children = {
    @ActionRef(type = com.perl5.lang.perl.idea.generation.GeneratePerlConstructorActionRegistration.class),
    @ActionRef(type = com.perl5.lang.perl.idea.generation.GeneratePerlGetterActionRegistration.class),
    @ActionRef(type = com.perl5.lang.perl.idea.generation.GeneratePerlSetterActionRegistration.class),
    @ActionRef(type = com.perl5.lang.perl.idea.generation.GeneratePerlGetterSetterActionRegistration.class)
  })
public class Perl5GenerateGroup extends DefaultActionGroup {
  public Perl5GenerateGroup() {
    setPopup(false);
  }
}
