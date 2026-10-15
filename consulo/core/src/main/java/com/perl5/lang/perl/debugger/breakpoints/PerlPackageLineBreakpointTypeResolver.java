package com.perl5.lang.perl.debugger.breakpoints;

import com.perl5.lang.perl.fileTypes.PerlFileTypePackage;
import consulo.annotation.component.ExtensionImpl;
import consulo.virtualFileSystem.fileType.FileType;

/**
 * Line breakpoints for {@link PerlFileTypePackage}, see {@link PerlLineBreakpointTypeResolverBase}
 */
@ExtensionImpl
public class PerlPackageLineBreakpointTypeResolver extends PerlLineBreakpointTypeResolverBase {
  @Override
  public FileType getFileType() {
    return PerlFileTypePackage.INSTANCE;
  }
}
