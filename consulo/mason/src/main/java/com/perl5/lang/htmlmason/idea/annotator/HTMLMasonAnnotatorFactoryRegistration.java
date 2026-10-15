// <annotator> in upstream perl5.lang.mason.htmlmason.backend.main.xml
package com.perl5.lang.htmlmason.idea.annotator;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.annotation.Annotator;
import consulo.language.editor.annotation.AnnotatorFactory;

@ExtensionImpl
public class HTMLMasonAnnotatorFactoryRegistration implements AnnotatorFactory {
  @Override
  public Annotator createAnnotator() {
    return new HTMLMasonAnnotator();
  }

  @Override
  public Language getLanguage() {
    return com.perl5.lang.htmlmason.HTMLMasonLanguage.INSTANCE;
  }
}
