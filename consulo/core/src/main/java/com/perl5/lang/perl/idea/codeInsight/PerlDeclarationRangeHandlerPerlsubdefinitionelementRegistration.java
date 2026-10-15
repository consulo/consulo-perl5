// <declarationRangeHandler> in upstream perl5.plugin.common.main.xml
package com.perl5.lang.perl.idea.codeInsight;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public class PerlDeclarationRangeHandlerPerlsubdefinitionelementRegistration extends com.perl5.lang.perl.idea.codeInsight.PerlDeclarationRangeHandler {
  @Override
  public Class getElementClass() {
    return com.perl5.lang.perl.psi.PerlSubDefinitionElement.class;
  }
}
