// <editorHighlighterProvider> in upstream perl5.lang.tt2.common.main.xml
package com.perl5.lang.tt2.filetypes;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public class TemplateToolkitEditorHighlighterProviderRegistration extends com.perl5.lang.tt2.filetypes.TemplateToolkitEditorHighlighterProvider {
  @Override
  public consulo.virtualFileSystem.fileType.FileType getFileType() {
    return com.perl5.lang.tt2.filetypes.TemplateToolkitFileType.INSTANCE;
  }
}
