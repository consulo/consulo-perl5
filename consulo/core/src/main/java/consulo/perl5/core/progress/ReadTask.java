package consulo.perl5.core.progress;

import consulo.annotation.access.RequiredReadAction;
import consulo.application.progress.ProgressIndicator;
import consulo.component.ProcessCanceledException;

/**
 * Plugin-side replacement of IntelliJ {@code com.intellij.openapi.progress.ReadTask}: Consulo keeps its own copy in
 * {@code consulo.application.internal}, which is not exported to plugins. Scheduled with {@link ProgressIndicatorUtils}.
 */
public abstract class ReadTask {
  @RequiredReadAction
  public abstract void computeInReadAction(ProgressIndicator indicator) throws ProcessCanceledException;

  /**
   * Upstream contract: invoked when the computation was canceled by a write action. The non-blocking read action used by
   * {@link ProgressIndicatorUtils} restarts the computation itself, so this is not invoked.
   */
  public void onCanceled(ProgressIndicator indicator) {
  }
}
