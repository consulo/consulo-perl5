// <lang.elementManipulator> in upstream perl5.plugin.common.main.xml
package com.perl5.lang.perl.idea.manipulators;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl(order = "after QuotedString")
public class PerlBareStringManipulatorRegistration extends com.perl5.lang.perl.idea.manipulators.PerlBareStringManipulator {
  @Override
  public Class getElementClass() {
    return com.perl5.lang.perl.psi.mixins.PerlStringBareMixin.class;
  }
}
