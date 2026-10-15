// <editorHighlighterProvider> in upstream perl5.lang.mojo.common.main.xml
package com.perl5.lang.mojolicious.filetypes;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public class MojoliciousEditorHighlighterProviderRegistration extends com.perl5.lang.mojolicious.filetypes.MojoliciousEditorHighlighterProvider {
  @Override
  public consulo.virtualFileSystem.fileType.FileType getFileType() {
    return com.perl5.lang.mojolicious.filetypes.MojoliciousFileType.INSTANCE;
  }
}
