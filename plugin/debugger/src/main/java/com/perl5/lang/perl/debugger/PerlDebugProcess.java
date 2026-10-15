/*
 * Copyright 2015-2025 Alexandr Evstigneev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.perl5.lang.perl.debugger;

import consulo.execution.ExecutionResult;
import consulo.process.ProcessHandler;
import consulo.execution.ui.ExecutionConsole;
import consulo.execution.ui.layout.RunnerLayoutUi;
import consulo.project.ui.notification.Notification;
import consulo.project.ui.notification.NotificationType;
import consulo.project.ui.notification.Notifications;
import consulo.ui.ex.action.DefaultActionGroup;
import consulo.application.ApplicationManager;
import consulo.logging.Logger;
import consulo.ui.ex.content.Content;
import consulo.execution.debug.XDebugProcess;
import consulo.execution.debug.XDebugSession;
import consulo.execution.debug.XDebuggerManager;
import consulo.execution.debug.XSourcePosition;
import consulo.execution.debug.breakpoint.XBreakpointHandler;
import consulo.execution.debug.XBreakpointManager;
import consulo.execution.debug.breakpoint.XLineBreakpoint;
import consulo.execution.debug.evaluation.XDebuggerEditorsProvider;
import consulo.execution.debug.frame.XSuspendContext;
import consulo.execution.debug.ui.XDebugTabLayouter;
import com.perl5.PerlIcons;
import com.perl5.lang.perl.debugger.breakpoints.PerlLineBreakPointDescriptor;
import com.perl5.lang.perl.debugger.breakpoints.PerlLineBreakpointHandler;
import com.perl5.lang.perl.debugger.breakpoints.PerlLineBreakpointProperties;
import com.perl5.lang.perl.debugger.breakpoints.PerlLineBreakpointType;
import com.perl5.lang.perl.debugger.run.run.debugger.PerlDebugProfileStateBase;
import com.perl5.lang.perl.idea.run.PerlRunProfileState;
import com.perl5.lang.perl.idea.run.debugger.PerlDebugOptions;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;


public class PerlDebugProcess extends XDebugProcess {
  private static final Logger LOG = Logger.getInstance(PerlDebugProcess.class);
  private final ExecutionResult myExecutionResult;
  private final PerlDebugThread myDebugThread;
  private final PerlRunProfileState myDebugProfileState;
  static final @NonNls String PERL_DEBUGGER_NOTIFICATION_GROUP_ID = "PERL_DEBUGGER";

  public PerlDebugProcess(@NotNull XDebugSession session, PerlDebugProfileStateBase state, ExecutionResult executionResult) {
    super(session);
    session.setPauseActionSupported(true);
    myExecutionResult = executionResult;
    myDebugThread = new PerlDebugThread(session, state, executionResult);
    myDebugProfileState = state;
    myDebugThread.start();
  }

  @Override
  public @NotNull XDebuggerEditorsProvider getEditorsProvider() {
    return PerlDebuggerEditorsProvider.INSTANCE;
  }

  @Override
  public boolean checkCanInitBreakpoints() {
    LOG.debug("Check can init breakpoints");
    return true;
  }

  @Override
  public XBreakpointHandler<?> @NotNull [] getBreakpointHandlers() {
    return new XBreakpointHandler[]{new PerlLineBreakpointHandler(myDebugThread)};
  }

  @Override
  public void sessionInitialized() {
    LOG.debug("Session initialized");
    super.sessionInitialized();
  }

  @Override
  public void registerAdditionalActions(@NotNull DefaultActionGroup leftToolbar,
                                        @NotNull DefaultActionGroup topToolbar,
                                        @NotNull DefaultActionGroup settings) {
    super.registerAdditionalActions(leftToolbar, topToolbar, settings);
  }

  @Override
  public void startStepOver(@Nullable XSuspendContext context) {
    myDebugThread.sendString("o");
  }

  @Override
  public void startStepInto(@Nullable XSuspendContext context) {
    myDebugThread.sendString("");
  }

  @Override
  public void startStepOut(@Nullable XSuspendContext context) {
    myDebugThread.sendString("u");
  }

  @Override
  public void startPausing() {
    if (((PerlDebugOptions)myDebugProfileState.getEnvironment().getRunProfile()).isNonInteractiveModeEnabled()) {
      myDebugThread.sendString("pause");
    }
    else {
      // consulo: Consulo requires a registered NotificationGroup instead of the group id, see PerlDebuggerNotificationGroupContributor
      Notifications.Bus.notify(new Notification(
        PerlDebuggerNotificationGroupContributor.PERL_DEBUGGER,
        PerlDebuggerBundle.message("perl.run.pause.unavailable.title"),
        PerlDebuggerBundle.message("perl.run.pause.unavailable.content"),
        NotificationType.INFORMATION
      ));
    }
  }

  @Override
  public void resume(@Nullable XSuspendContext context) {
    myDebugThread.sendString("g");
  }

  @Override
  public void stop() {
    myDebugThread.setStop();

    ApplicationManager.getApplication().runReadAction(
      () -> {
        XBreakpointManager breakpointManager = XDebuggerManager.getInstance(getSession().getProject()).getBreakpointManager();
        Collection<? extends XLineBreakpoint<PerlLineBreakpointProperties>> breakpoints =
          breakpointManager.getBreakpoints(PerlLineBreakpointType.class);
        for (XLineBreakpoint<PerlLineBreakpointProperties> breakpoint : breakpoints) {
          breakpointManager.updateBreakpointPresentation(breakpoint, null, null);
        }
      }
    );
  }

  @Override
  public void runToPosition(@NotNull XSourcePosition position, @Nullable XSuspendContext context) {
    PerlLineBreakPointDescriptor descriptor = PerlLineBreakPointDescriptor.createFromSourcePosition(position, myDebugThread);
    if (descriptor != null) {
      myDebugThread.sendCommand("p", descriptor);
    }
  }

  @Override
  protected @Nullable ProcessHandler doGetProcessHandler() {
    return myExecutionResult.getProcessHandler();
  }

  @Override
  public @NotNull ExecutionConsole createConsole() {
    return myExecutionResult.getExecutionConsole();
  }

  @Override
  public @NotNull XDebugTabLayouter createTabLayouter() {
    return new XDebugTabLayouter() {
      @Override
      public void registerAdditionalContent(@NotNull RunnerLayoutUi ui) {
        Content content =
          ui.createContent("PerlSourceList", myDebugThread.getScriptListPanel(), PerlDebuggerBundle.message("loaded.sources"),
                           PerlIcons.PERL_SCRIPT_FILE_ICON);
        content.setCloseable(false);
        ui.addContent(content);

        content =
          ui.createContent("PerlEvalsList", myDebugThread.getEvalsListPanel(), PerlDebuggerBundle.message("compiled.evals"),
                           PerlIcons.PERL_LANGUAGE_ICON);
        content.setCloseable(false);
        ui.addContent(content);
      }
    };
  }
}
