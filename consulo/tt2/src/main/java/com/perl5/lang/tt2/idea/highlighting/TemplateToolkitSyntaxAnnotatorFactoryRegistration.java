// <annotator> in upstream perl5.lang.tt2.backend.main.xml
package com.perl5.lang.tt2.idea.highlighting;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.annotation.Annotator;
import consulo.language.editor.annotation.AnnotatorFactory;

@ExtensionImpl
public class TemplateToolkitSyntaxAnnotatorFactoryRegistration implements AnnotatorFactory {
  @Override
  public Annotator createAnnotator() {
    return new TemplateToolkitSyntaxAnnotator();
  }

  @Override
  public Language getLanguage() {
    return com.perl5.lang.tt2.TemplateToolkitLanguage.INSTANCE;
  }
}
