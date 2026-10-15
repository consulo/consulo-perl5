// <editorHighlighterProvider> in upstream perl5.lang.mason.mason2.common.main.xml
package com.perl5.lang.mason2.filetypes;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public class MasonEditorHighlighterProviderMason2TopLevelComponentRegistration extends com.perl5.lang.mason2.filetypes.MasonEditorHighlighterProvider {
  @Override
  public consulo.virtualFileSystem.fileType.FileType getFileType() {
    return com.perl5.lang.mason2.filetypes.MasonTopLevelComponentFileType.INSTANCE;
  }
}
