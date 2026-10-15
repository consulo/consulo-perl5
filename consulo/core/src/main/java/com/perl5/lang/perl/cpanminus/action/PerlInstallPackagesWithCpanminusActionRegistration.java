// <action id="perl5.install.with.cpanm"> in upstream perl5.plugin.cpanminus.backend.main.xml
package com.perl5.lang.perl.cpanminus.action;

import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionParentRef;
import consulo.annotation.component.ActionRef;
import consulo.annotation.component.ActionRefAnchor;
import consulo.localize.LocalizeValue;

@ActionImpl(id = "perl5.install.with.cpanm", parents = @ActionParentRef(value = @ActionRef(id = "perl5.install"), anchor = ActionRefAnchor.LAST))
public class PerlInstallPackagesWithCpanminusActionRegistration extends PerlInstallPackagesWithCpanminusAction {
}
