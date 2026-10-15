// <action id="perl5.perlbrew.install.patchperl"> in upstream perl5.plugin.perlbrew.main.xml
package com.perl5.lang.perl.idea.sdk.versionManager.perlbrew;

import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionParentRef;
import consulo.annotation.component.ActionRef;
import consulo.annotation.component.ActionRefAnchor;
import consulo.localize.LocalizeValue;

@ActionImpl(id = "perl5.perlbrew.install.patchperl", parents = @ActionParentRef(value = @ActionRef(id = "perl5.install"), anchor = ActionRefAnchor.LAST))
public class PerlBrewInstallPatchPerlActionRegistration extends PerlBrewInstallPatchPerlAction {
  public PerlBrewInstallPatchPerlActionRegistration() {
    getTemplatePresentation().setIcon(consulo.perl5.icon.Perl5IconGroup.perlbrew());
  }
}
