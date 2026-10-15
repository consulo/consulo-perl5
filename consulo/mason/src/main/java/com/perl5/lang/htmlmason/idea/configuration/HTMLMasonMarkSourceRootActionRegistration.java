// <action id="Perl.MarkHTMLMasonRoot"> in upstream perl5.lang.mason.htmlmason.backend.main.xml
package com.perl5.lang.htmlmason.idea.configuration;

import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionParentRef;
import consulo.annotation.component.ActionRef;
import consulo.annotation.component.ActionRefAnchor;
import consulo.localize.LocalizeValue;

@ActionImpl(id = "Perl.MarkHTMLMasonRoot", parents = @ActionParentRef(value = @ActionRef(id = "ProjectViewPopupMenu"), anchor = ActionRefAnchor.LAST))
public class HTMLMasonMarkSourceRootActionRegistration extends HTMLMasonMarkSourceRootAction {
}
