// <action id="Embedded.Perl.newFile"> in upstream perl5.lang.embedded.backend.main.xml
package com.perl5.lang.embedded.idea.actions;

import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionParentRef;
import consulo.annotation.component.ActionRef;
import consulo.annotation.component.ActionRefAnchor;
import consulo.localize.LocalizeValue;

@ActionImpl(id = "Embedded.Perl.newFile", parents = @ActionParentRef(value = @ActionRef(id = "NewGroup"), anchor = ActionRefAnchor.AFTER, relatedToAction = @ActionRef(id = "Perl5.NewFile")))
public class EmbeddedPerlCreateFileFromTemplateActionRegistration extends EmbeddedPerlCreateFileFromTemplateAction {
}
