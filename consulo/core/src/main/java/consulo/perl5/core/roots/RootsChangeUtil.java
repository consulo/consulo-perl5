package consulo.perl5.core.roots;

import consulo.annotation.access.RequiredWriteAction;
import consulo.module.ModuleManager;
import consulo.project.Project;
import consulo.project.RootsChangeRescanningInfo;

/**
 * Plugin-side replacement of IntelliJ {@code ProjectRootManagerEx#makeRootsChange}: Consulo keeps {@code ProjectRootManagerEx} in
 * {@code consulo.module.content.internal}, which is not exported to plugins.
 */
public final class RootsChangeUtil {
  private RootsChangeUtil() {
  }

  /**
   * Runs {@code runnable} and fires a roots change for the {@code project}.
   * <p>
   * TODO consulo: there is no public API to fire a roots change. A commit of a module model fires a roots change, so a commit of the
   *  not changed module model is used. It always causes a total rescan ({@code rescanningInfo} is not respected) and the roots change
   *  is fired after the {@code runnable}, not around it.
   */
  @RequiredWriteAction
  public static void makeRootsChange(Project project, Runnable runnable, RootsChangeRescanningInfo rescanningInfo) {
    if (project.isDisposed()) {
      return;
    }
    runnable.run();
    if (!project.isDefault()) {
      ModuleManager.getInstance(project).getModifiableModel().commit();
    }
  }
}
