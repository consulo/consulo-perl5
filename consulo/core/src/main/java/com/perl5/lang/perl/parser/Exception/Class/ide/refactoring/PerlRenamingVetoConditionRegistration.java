// <vetoRenameCondition> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.parser.Exception.Class.ide.refactoring;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public class PerlRenamingVetoConditionRegistration implements consulo.language.editor.refactoring.rename.VetoRenameCondition {
  private final com.perl5.lang.perl.parser.Exception.Class.ide.refactoring.PerlRenamingVetoCondition myDelegate = new com.perl5.lang.perl.parser.Exception.Class.ide.refactoring.PerlRenamingVetoCondition();

  @Override
  public boolean isVetoed(consulo.language.psi.PsiElement element) {
    return myDelegate.value(element);
  }
}
