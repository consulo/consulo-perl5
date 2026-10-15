package com.perl5.lang.perl.debugger.breakpoints;

import consulo.annotation.access.RequiredReadAction;
import consulo.execution.debug.breakpoint.XBreakpointType;
import consulo.execution.debug.breakpoint.XLineBreakpointType;
import consulo.execution.debug.breakpoint.XLineBreakpointTypeResolver;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import org.jetbrains.annotations.Nullable;

/**
 * Consulo has no {@code XLineBreakpointType#canPutAt}: line breakpoint types are resolved per file type by
 * {@link XLineBreakpointTypeResolver}. Upstream {@link PerlLineBreakpointType#canPutAt} accepts any {@code PerlFileType},
 * so each perl file type needs its own resolver subclass (perl5 core file types are registered next to this class).
 */
public abstract class PerlLineBreakpointTypeResolverBase implements XLineBreakpointTypeResolver {
  @RequiredReadAction
  @Override
  public @Nullable XLineBreakpointType<?> resolveBreakpointType(Project project, VirtualFile virtualFile, int line) {
    PerlLineBreakpointType breakpointType = XBreakpointType.EXTENSION_POINT_NAME.findExtensionOrFail(PerlLineBreakpointType.class);
    return breakpointType.canPutAt(virtualFile, line, project) ? breakpointType : null;
  }
}
