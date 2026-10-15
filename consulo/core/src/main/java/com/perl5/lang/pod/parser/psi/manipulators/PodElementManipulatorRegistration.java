// <lang.elementManipulator> in upstream perl5.plugin.common.main.xml
package com.perl5.lang.pod.parser.psi.manipulators;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public class PodElementManipulatorRegistration extends com.perl5.lang.pod.parser.psi.manipulators.PodElementManipulator {
  @Override
  public Class getElementClass() {
    return com.perl5.lang.pod.parser.psi.PodCompositeElement.class;
  }
}
