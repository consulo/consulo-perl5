// <lang.elementManipulator> in upstream perl5.plugin.common.main.xml
package com.perl5.lang.perl.idea.manipulators;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public class PerlStringContentManipulatorRegistration extends com.perl5.lang.perl.idea.manipulators.PerlStringContentManipulator {
  @Override
  public Class getElementClass() {
    return com.perl5.lang.perl.psi.PerlStringContentElement.class;
  }
}
