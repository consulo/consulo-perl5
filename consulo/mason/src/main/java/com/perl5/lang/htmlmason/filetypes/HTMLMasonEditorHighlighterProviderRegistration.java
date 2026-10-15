// <editorHighlighterProvider> in upstream perl5.lang.mason.htmlmason.common.main.xml
package com.perl5.lang.htmlmason.filetypes;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public class HTMLMasonEditorHighlighterProviderRegistration extends com.perl5.lang.htmlmason.filetypes.HTMLMasonEditorHighlighterProvider {
  @Override
  public consulo.virtualFileSystem.fileType.FileType getFileType() {
    return com.perl5.lang.htmlmason.filetypes.HTMLMasonFileType.INSTANCE;
  }
}
