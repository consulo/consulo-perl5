// <action id="Perl5.NewFile"> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.idea.actions;

import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionParentRef;
import consulo.annotation.component.ActionRef;
import consulo.annotation.component.ActionRefAnchor;
import consulo.localize.LocalizeValue;

@ActionImpl(id = "Perl5.NewFile", parents = @ActionParentRef(value = @ActionRef(id = "NewGroup"), anchor = ActionRefAnchor.FIRST))
public class PerlFileFromTemplateActionRegistration extends PerlFileFromTemplateAction {
}
