// <action id="perl5.perlbrew.createlib"> in upstream perl5.plugin.perlbrew.main.xml
package com.perl5.lang.perl.idea.sdk.versionManager.perlbrew;

import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionParentRef;
import consulo.annotation.component.ActionRef;
import consulo.annotation.component.ActionRefAnchor;
import consulo.localize.LocalizeValue;

@ActionImpl(id = "perl5.perlbrew.createlib", parents = @ActionParentRef(value = @ActionRef(id = "perl5.tools"), anchor = ActionRefAnchor.LAST))
public class PerlBrewCreateLibraryActionRegistration extends PerlBrewCreateLibraryAction {
  public PerlBrewCreateLibraryActionRegistration() {
    getTemplatePresentation().setIcon(consulo.perl5.icon.Perl5IconGroup.perlbrew());
  }
}
