package com.perl5.lang.perl.debugger.breakpoints;

import com.perl5.lang.perl.fileTypes.PerlFileTypeTest;
import consulo.annotation.component.ExtensionImpl;
import consulo.virtualFileSystem.fileType.FileType;

/**
 * Line breakpoints for {@link PerlFileTypeTest}, see {@link PerlLineBreakpointTypeResolverBase}
 */
@ExtensionImpl
public class PerlTestLineBreakpointTypeResolver extends PerlLineBreakpointTypeResolverBase {
  @Override
  public FileType getFileType() {
    return PerlFileTypeTest.INSTANCE;
  }
}
