// <lang.elementManipulator> in upstream perl5.lang.tt2.common.main.xml
package com.perl5.lang.tt2.psi.manipulators;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public class TemplateToolkitStringManipulatorRegistration extends com.perl5.lang.tt2.psi.manipulators.TemplateToolkitStringManipulator {
  @Override
  public Class getElementClass() {
    return com.perl5.lang.tt2.psi.TemplateToolkitString.class;
  }
}
