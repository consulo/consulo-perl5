package consulo.perl5.core.progress;

import consulo.application.ReadAction;
import consulo.application.progress.EmptyProgressIndicator;
import consulo.application.progress.ProgressIndicator;
import consulo.application.progress.ProgressManager;

/**
 * Plugin-side replacement of IntelliJ {@code ProgressIndicatorUtils#scheduleWithWriteActionPriority}: Consulo keeps it in
 * {@code consulo.application.internal}, which is not exported to plugins. Runs the task in a non-blocking read action,
 * which is canceled by write actions and restarted afterwards.
 */
public final class ProgressIndicatorUtils {
  private ProgressIndicatorUtils() {
  }

  public static void scheduleWithWriteActionPriority(ReadTask task) {
    ReadAction.nonBlocking(() -> {
      ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
      task.computeInReadAction(indicator != null ? indicator : new EmptyProgressIndicator());
    }).submitDefault();
  }
}
