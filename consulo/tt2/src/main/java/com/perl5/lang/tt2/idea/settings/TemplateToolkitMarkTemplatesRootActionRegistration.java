// <action id="Perl.MarkAdTT2Root"> in upstream perl5.lang.tt2.backend.main.xml
package com.perl5.lang.tt2.idea.settings;

import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionParentRef;
import consulo.annotation.component.ActionRef;
import consulo.annotation.component.ActionRefAnchor;
import consulo.localize.LocalizeValue;

@ActionImpl(id = "Perl.MarkAdTT2Root", parents = @ActionParentRef(value = @ActionRef(id = "ProjectViewPopupMenu"), anchor = ActionRefAnchor.LAST))
public class TemplateToolkitMarkTemplatesRootActionRegistration extends TemplateToolkitMarkTemplatesRootAction {
}
