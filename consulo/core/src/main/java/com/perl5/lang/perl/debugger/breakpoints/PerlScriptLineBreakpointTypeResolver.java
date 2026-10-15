package com.perl5.lang.perl.debugger.breakpoints;

import com.perl5.lang.perl.fileTypes.PerlFileTypeScript;
import consulo.annotation.component.ExtensionImpl;
import consulo.virtualFileSystem.fileType.FileType;

/**
 * Line breakpoints for {@link PerlFileTypeScript}, see {@link PerlLineBreakpointTypeResolverBase}
 */
@ExtensionImpl
public class PerlScriptLineBreakpointTypeResolver extends PerlLineBreakpointTypeResolverBase {
  @Override
  public FileType getFileType() {
    return PerlFileTypeScript.INSTANCE;
  }
}
