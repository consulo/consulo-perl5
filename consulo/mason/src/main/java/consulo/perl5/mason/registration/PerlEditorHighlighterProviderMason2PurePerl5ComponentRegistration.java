// <editorHighlighterProvider> in upstream perl5.lang.mason.mason2.common.main.xml
package consulo.perl5.mason.registration;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public class PerlEditorHighlighterProviderMason2PurePerl5ComponentRegistration extends com.perl5.lang.perl.idea.highlighter.PerlEditorHighlighterProvider {
  @Override
  public consulo.virtualFileSystem.fileType.FileType getFileType() {
    return com.perl5.lang.mason2.filetypes.MasonPurePerlComponentFileType.INSTANCE;
  }
}
