// <action id="Perl.MarkMason2Root"> in upstream perl5.lang.mason.mason2.backend.main.xml
package com.perl5.lang.mason2.idea.configuration;

import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionParentRef;
import consulo.annotation.component.ActionRef;
import consulo.annotation.component.ActionRefAnchor;
import consulo.localize.LocalizeValue;

@ActionImpl(id = "Perl.MarkMason2Root", parents = @ActionParentRef(value = @ActionRef(id = "ProjectViewPopupMenu"), anchor = ActionRefAnchor.LAST))
public class Mason2MarkSourceRootActionRegistration extends Mason2MarkSourceRootAction {
}
