// <lang.elementManipulator> in upstream perl5.plugin.common.main.xml
package com.perl5.lang.perl.idea.manipulators;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl(id = "QuotedString")
public class PerlStringManipulatorRegistration extends com.perl5.lang.perl.idea.manipulators.PerlStringManipulator {
  @Override
  public Class getElementClass() {
    return com.perl5.lang.perl.psi.mixins.PerlStringMixin.class;
  }
}
