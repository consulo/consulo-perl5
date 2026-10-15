// <editorHighlighterProvider> in upstream perl5.plugin.cpanminus.common.main.xml
package com.perl5.lang.perl.idea.highlighter;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public class PerlEditorHighlighterProviderCpanfileRegistration extends com.perl5.lang.perl.idea.highlighter.PerlEditorHighlighterProvider {
  @Override
  public consulo.virtualFileSystem.fileType.FileType getFileType() {
    return com.perl5.lang.perl.cpanminus.cpanfile.PerlFileTypeCpanfile.INSTANCE;
  }
}
