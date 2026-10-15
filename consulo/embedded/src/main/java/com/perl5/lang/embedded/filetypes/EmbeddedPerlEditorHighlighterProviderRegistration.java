// <editorHighlighterProvider> in upstream perl5.lang.embedded.common.main.xml
package com.perl5.lang.embedded.filetypes;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public class EmbeddedPerlEditorHighlighterProviderRegistration extends com.perl5.lang.embedded.filetypes.EmbeddedPerlEditorHighlighterProvider {
  @Override
  public consulo.virtualFileSystem.fileType.FileType getFileType() {
    return com.perl5.lang.embedded.filetypes.EmbeddedPerlFileType.INSTANCE;
  }
}
