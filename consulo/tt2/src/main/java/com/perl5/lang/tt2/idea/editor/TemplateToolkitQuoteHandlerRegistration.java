// <quoteHandler> in upstream perl5.lang.tt2.common.main.xml
package com.perl5.lang.tt2.idea.editor;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public class TemplateToolkitQuoteHandlerRegistration extends com.perl5.lang.tt2.idea.editor.TemplateToolkitQuoteHandler implements consulo.language.editor.action.FileQuoteHandler {
  @Override
  public consulo.virtualFileSystem.fileType.FileType getFileType() {
    return com.perl5.lang.tt2.filetypes.TemplateToolkitFileType.INSTANCE;
  }
}
