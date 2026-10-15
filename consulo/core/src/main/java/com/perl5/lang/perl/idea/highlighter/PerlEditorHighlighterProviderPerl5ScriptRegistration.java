// <editorHighlighterProvider> in upstream perl5.plugin.common.main.xml
package com.perl5.lang.perl.idea.highlighter;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public class PerlEditorHighlighterProviderPerl5ScriptRegistration extends com.perl5.lang.perl.idea.highlighter.PerlEditorHighlighterProvider {
  @Override
  public consulo.virtualFileSystem.fileType.FileType getFileType() {
    return com.perl5.lang.perl.fileTypes.PerlFileTypeScript.INSTANCE;
  }
}
