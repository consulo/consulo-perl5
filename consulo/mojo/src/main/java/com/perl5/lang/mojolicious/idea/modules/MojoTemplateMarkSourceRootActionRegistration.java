// <action id="perl5.mojo.mark.root"> in upstream perl5.lang.mojo.backend.main.xml
package com.perl5.lang.mojolicious.idea.modules;

import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionParentRef;
import consulo.annotation.component.ActionRef;
import consulo.annotation.component.ActionRefAnchor;
import consulo.localize.LocalizeValue;

@ActionImpl(id = "perl5.mojo.mark.root", parents = @ActionParentRef(value = @ActionRef(id = "ProjectViewPopupMenu"), anchor = ActionRefAnchor.LAST))
public class MojoTemplateMarkSourceRootActionRegistration extends MojoTemplateMarkSourceRootAction {
}
