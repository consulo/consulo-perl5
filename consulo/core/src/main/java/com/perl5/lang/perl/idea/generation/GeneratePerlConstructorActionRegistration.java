// <action id="Perl5NewConstructor"> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.idea.generation;

import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionParentRef;
import consulo.annotation.component.ActionRef;
import consulo.annotation.component.ActionRefAnchor;
import consulo.localize.LocalizeValue;

@ActionImpl(id = "Perl5NewConstructor")
public class GeneratePerlConstructorActionRegistration extends GeneratePerlConstructorAction {
  public GeneratePerlConstructorActionRegistration() {
    getTemplatePresentation().setText(LocalizeValue.localizeTODO(com.perl5.PerlBundle.message("action.Perl5NewConstructor.text")));
    getTemplatePresentation().setDescription(LocalizeValue.localizeTODO(com.perl5.PerlBundle.message("action.Perl5NewConstructor.description")));
  }
}
