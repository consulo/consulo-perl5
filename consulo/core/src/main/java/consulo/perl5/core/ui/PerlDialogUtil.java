package consulo.perl5.core.ui;

import consulo.application.Application;
import consulo.logging.Logger;
import consulo.project.Project;
import consulo.ui.DialogCancelledException;
import consulo.ui.UIAccess;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.dialog.Dialog;
import consulo.ui.ex.dialog.DialogDescriptor;
import consulo.ui.ex.dialog.DialogService;
import consulo.ui.ex.dialog.DialogValue;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Upstream asks the user with modal Swing dialogs ({@code Messages}) and goes on synchronously, also from background threads
 * through {@code invokeAndWait}. Swing dialogs are not available in Consulo Web, while the unified message and input boxes
 * ({@link consulo.ui.MessageBoxBuilder}, {@link consulo.ui.InputBoxBuilder}) and dialogs ({@link #showAsync}) never block and
 * answer asynchronously.
 */
public final class PerlDialogUtil {
  private static final Logger LOG = Logger.getInstance(PerlDialogUtil.class);

  private PerlDialogUtil() {
  }

  /**
   * Shows a unified dialog and passes its answer to the {@code continuation}: null when the dialog was dismissed or could not
   * be shown.
   * <p>
   * On the UI thread the continuation runs once the answer arrives. On a background thread the dialog is shown on the UI
   * thread and the calling thread waits for the answer, so the continuation keeps running in the background (and in the
   * progress of the calling task), like after upstream {@code invokeAndWait}, but without blocking the UI thread.
   */
  public static <T> void showAndThen(@RequiredUIAccess Supplier<? extends CompletableFuture<T>> dialog,
                                     Consumer<? super @Nullable T> continuation) {
    if (UIAccess.isUIThread()) {
      dialog.get().whenComplete((answer, error) -> {
        try {
          continuation.accept(answerOrNull(answer, error));
        }
        catch (Throwable e) {
          // would be swallowed by the future otherwise
          LOG.error(e);
        }
      });
      return;
    }

    CompletableFuture<@Nullable T> answerFuture = new CompletableFuture<>();
    Application.get().invokeLater(() -> {
      try {
        dialog.get().whenComplete((answer, error) -> answerFuture.complete(answerOrNull(answer, error)));
      }
      catch (Throwable e) {
        answerFuture.complete(null);
        throw e;
      }
    });
    continuation.accept(answerFuture.join());
  }

  /**
   * Shows a unified dialog of the {@code descriptor} (it works in desktop and web, unlike Swing {@code DialogWrapper}). The result
   * carries the value of the OK action; a dismissal fails it with {@link DialogCancelledException}, like the unified input boxes do,
   * whichever frontend shows it.
   */
  @RequiredUIAccess
  public static CompletableFuture<DialogValue> showAsync(@Nullable Project project, DialogDescriptor descriptor) {
    DialogService dialogService = Application.get().getInstance(DialogService.class);
    Dialog dialog = project == null ? dialogService.build(descriptor) : dialogService.build(project, descriptor);

    CompletableFuture<DialogValue> result = new CompletableFuture<>();
    dialog.showAsync().whenComplete((value, error) -> {
      if (error == null && value != null) {
        result.complete(value);
        return;
      }
      Throwable cause = error instanceof CompletionException && error.getCause() != null ? error.getCause() : error;
      // the desktop dialog is dismissed with CancellationException, the unified one with IllegalArgumentException
      if (cause == null || cause instanceof CancellationException || cause instanceof IllegalArgumentException) {
        result.completeExceptionally(new DialogCancelledException());
      }
      else {
        result.completeExceptionally(cause);
      }
    });
    return result;
  }

  private static <T> @Nullable T answerOrNull(@Nullable T answer, @Nullable Throwable error) {
    if (error == null) {
      return answer;
    }
    Throwable cause = error instanceof CompletionException && error.getCause() != null ? error.getCause() : error;
    // input boxes are dismissed with DialogCancelledException, file choosers with CancellationException
    if (!(cause instanceof DialogCancelledException) && !(cause instanceof CancellationException)) {
      LOG.warn("Unable to show a dialog", cause);
    }
    return null;
  }
}
