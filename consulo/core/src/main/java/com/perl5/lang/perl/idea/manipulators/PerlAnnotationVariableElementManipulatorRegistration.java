// <lang.elementManipulator> in upstream perl5.plugin.common.main.xml
package com.perl5.lang.perl.idea.manipulators;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public class PerlAnnotationVariableElementManipulatorRegistration extends com.perl5.lang.perl.idea.manipulators.PerlAnnotationVariableElementManipulator {
  @Override
  public Class getElementClass() {
    return com.perl5.lang.perl.psi.PerlAnnotationVariableElement.class;
  }
}
