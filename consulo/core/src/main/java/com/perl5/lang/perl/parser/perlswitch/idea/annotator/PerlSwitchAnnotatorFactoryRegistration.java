// <annotator> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.parser.perlswitch.idea.annotator;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.annotation.Annotator;
import consulo.language.editor.annotation.AnnotatorFactory;

@ExtensionImpl
public class PerlSwitchAnnotatorFactoryRegistration implements AnnotatorFactory {
  @Override
  public Annotator createAnnotator() {
    return new PerlSwitchAnnotator();
  }

  @Override
  public Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
