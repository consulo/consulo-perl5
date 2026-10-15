// <action id="perl5.mojo.install"> in upstream perl5.lang.mojo.backend.main.xml
package com.perl5.lang.mojolicious.idea.actions;

import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionParentRef;
import consulo.annotation.component.ActionRef;
import consulo.annotation.component.ActionRefAnchor;
import consulo.localize.LocalizeValue;

@ActionImpl(id = "perl5.mojo.install", parents = @ActionParentRef(value = @ActionRef(id = "perl5.install"), anchor = ActionRefAnchor.LAST))
public class InstallMojoliciousActionRegistration extends InstallMojoliciousAction {
}
