// <annotator> in upstream perl5.lang.mason.mason2.backend.main.xml
package com.perl5.lang.mason2.idea.annotator;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.annotation.Annotator;
import consulo.language.editor.annotation.AnnotatorFactory;

@ExtensionImpl
public class MasonAnnotatorFactoryRegistration implements AnnotatorFactory {
  @Override
  public Annotator createAnnotator() {
    return new MasonAnnotator();
  }

  @Override
  public Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
