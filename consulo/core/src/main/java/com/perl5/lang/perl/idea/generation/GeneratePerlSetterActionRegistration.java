// <action id="Perl5NewSetter"> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.idea.generation;

import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionParentRef;
import consulo.annotation.component.ActionRef;
import consulo.annotation.component.ActionRefAnchor;
import consulo.localize.LocalizeValue;

@ActionImpl(id = "Perl5NewSetter")
public class GeneratePerlSetterActionRegistration extends GeneratePerlSetterAction {
  public GeneratePerlSetterActionRegistration() {
    getTemplatePresentation().setText(LocalizeValue.localizeTODO(com.perl5.PerlBundle.message("action.Perl5NewSetter.text")));
    getTemplatePresentation().setDescription(LocalizeValue.localizeTODO(com.perl5.PerlBundle.message("action.Perl5NewSetter.description")));
  }
}
