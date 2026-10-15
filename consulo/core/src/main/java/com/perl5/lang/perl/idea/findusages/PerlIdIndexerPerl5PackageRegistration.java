// <idIndexer> in upstream perl5.plugin.backend.main.xml
package com.perl5.lang.perl.idea.findusages;

import consulo.annotation.component.ExtensionImpl;

@ExtensionImpl
public class PerlIdIndexerPerl5PackageRegistration extends com.perl5.lang.perl.idea.findusages.PerlIdIndexer {
  @Override
  public consulo.virtualFileSystem.fileType.FileType getFileType() {
    return com.perl5.lang.perl.fileTypes.PerlFileTypePackage.INSTANCE;
  }
}
