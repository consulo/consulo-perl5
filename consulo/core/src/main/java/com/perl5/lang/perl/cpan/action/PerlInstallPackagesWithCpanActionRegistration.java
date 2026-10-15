// <action id="perl5.install.with.cpan"> in upstream perl5.plugin.cpan.main.xml
package com.perl5.lang.perl.cpan.action;

import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionParentRef;
import consulo.annotation.component.ActionRef;
import consulo.annotation.component.ActionRefAnchor;
import consulo.localize.LocalizeValue;

@ActionImpl(id = "perl5.install.with.cpan", parents = @ActionParentRef(value = @ActionRef(id = "perl5.install"), anchor = ActionRefAnchor.LAST))
public class PerlInstallPackagesWithCpanActionRegistration extends PerlInstallPackagesWithCpanAction {
}
