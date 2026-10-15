// <action id="perl5.mark.library"> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.idea.actions;

import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionParentRef;
import consulo.annotation.component.ActionRef;
import consulo.annotation.component.ActionRefAnchor;
import consulo.localize.LocalizeValue;

@ActionImpl(id = "perl5.mark.library", parents = @ActionParentRef(value = @ActionRef(id = "ProjectViewPopupMenu"), anchor = ActionRefAnchor.LAST))
public class PerlMarkLibrarySourceRootActionRegistration extends PerlMarkLibrarySourceRootAction {
}
