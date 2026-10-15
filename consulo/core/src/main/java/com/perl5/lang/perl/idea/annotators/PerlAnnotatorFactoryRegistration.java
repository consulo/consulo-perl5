// <annotator> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.idea.annotators;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.annotation.Annotator;
import consulo.language.editor.annotation.AnnotatorFactory;

@ExtensionImpl(id = "PERL_SUBS_ANNOTATOR")
public class PerlAnnotatorFactoryRegistration implements AnnotatorFactory {
  @Override
  public Annotator createAnnotator() {
    return new PerlAnnotator();
  }

  @Override
  public Language getLanguage() {
    return com.perl5.lang.perl.PerlLanguage.INSTANCE;
  }
}
