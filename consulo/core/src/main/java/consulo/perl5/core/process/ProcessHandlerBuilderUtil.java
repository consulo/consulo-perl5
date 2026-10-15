package consulo.perl5.core.process;

import consulo.application.Application;
import consulo.process.ExecutionException;
import consulo.process.ProcessHandlerBuilder;
import consulo.process.ProcessHandlerBuilderFactory;
import consulo.process.cmd.GeneralCommandLine;
import consulo.util.lang.function.ThrowableSupplier;

/**
 * Plugin-side replacement of IntelliJ process handler constructors accepting an existing {@link Process}
 * (e.g. {@code new KillableProcessHandler(process, commandLineString, charset)}): Consulo keeps the handlers in
 * {@code consulo.process.internal}, which is not exported to plugins.
 */
public final class ProcessHandlerBuilderUtil {
  private ProcessHandlerBuilderUtil() {
  }

  /**
   * @return a public process handler builder for a copy of the {@code commandLine}, which creates its process with
   * {@code processCreator} instead of starting the command line itself. The command line string and charset of the handler are taken
   * from the {@code commandLine}. The process is created on {@link ProcessHandlerBuilder#build()}.
   */
  public static ProcessHandlerBuilder newBuilder(GeneralCommandLine commandLine,
                                                 ThrowableSupplier<Process, ExecutionException> processCreator) {
    GeneralCommandLine processCommandLine = new GeneralCommandLine(commandLine) {
      @Override
      public Process createProcess() throws ExecutionException {
        return processCreator.get();
      }
    };
    return Application.get().getInstance(ProcessHandlerBuilderFactory.class).newBuilder(processCommandLine);
  }
}
