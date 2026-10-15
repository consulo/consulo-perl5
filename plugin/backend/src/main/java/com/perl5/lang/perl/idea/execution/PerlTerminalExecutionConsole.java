/*
 * Copyright 2015-2026 Alexandr Evstigneev
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

package com.perl5.lang.perl.idea.execution;

import com.perl5.lang.perl.idea.sdk.host.PerlConsoleView;
import com.perl5.lang.perl.idea.sdk.host.PerlHostData;
import consulo.application.ReadAction;
import consulo.application.util.concurrent.AppExecutorUtil;
import consulo.codeEditor.Editor;
import consulo.disposer.Disposer;
import consulo.execution.ui.console.ConsoleView;
import consulo.execution.ui.console.ConsoleViewContentType;
import consulo.execution.ui.console.ConsoleViewUtil;
import consulo.execution.ui.console.Filter;
import consulo.execution.ui.console.HyperlinkInfo;
import consulo.execution.ui.console.TextConsoleBuilderFactory;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.process.ProcessHandler;
import consulo.process.event.ProcessEvent;
import consulo.project.Project;
import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.action.AnAction;
import consulo.util.dataholder.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.function.BiPredicate;

/**
 * TODO consulo: no TerminalExecutionConsole (terminal-emulating console) in Consulo API. The console is created by the platform
 *  console builder (unified console in web, AWT console on desktop) and wrapped to carry the host data; ANSI escape sequences are
 *  not interpreted.
 */
public class PerlTerminalExecutionConsole implements PerlConsoleView {
  private final @NotNull ConsoleView myDelegate;
  private @Nullable PerlHostData<?, ?> myHostData;

  public PerlTerminalExecutionConsole(@NotNull Project project) {
    // predefined filters are computed by the platform console builder
    myDelegate = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
    Disposer.register(this, myDelegate);
  }

  @Override
  public @Nullable PerlHostData<?, ?> getHostData() {
    return myHostData;
  }

  @Override
  public @NotNull PerlTerminalExecutionConsole withHostData(@Nullable PerlHostData<?, ?> hostData) {
    myHostData = hostData;
    return this;
  }

  @Override
  public void print(@NotNull String s, @NotNull ConsoleViewContentType contentType) {
    myDelegate.print(s, contentType);
  }

  @Override
  public void clear() {
    myDelegate.clear();
  }

  @Override
  public void scrollTo(int offset) {
    myDelegate.scrollTo(offset);
  }

  @Override
  public void attachToProcess(@NotNull ProcessHandler processHandler) {
    myDelegate.attachToProcess(processHandler);
    // PTY output has \r (line ends and progress lines), the platform console documents accept \n separators only
    BiPredicate<ProcessEvent, Key> previousFilter = myDelegate.getProcessTextFilter();
    myDelegate.setProcessTextFilter((event, outputType) -> {
      if (previousFilter != null && previousFilter.test(event, outputType)) {
        return true;
      }
      String text = event.getText();
      if (text == null || text.indexOf('\r') < 0) {
        return false;
      }
      myDelegate.print(text.replace("\r\n", "\n").replace('\r', '\n'), ConsoleViewContentType.getConsoleViewType(outputType));
      return true;
    });
  }

  @Override
  public void setOutputPaused(boolean value) {
    myDelegate.setOutputPaused(value);
  }

  @Override
  public boolean isOutputPaused() {
    return myDelegate.isOutputPaused();
  }

  @Override
  public boolean hasDeferredOutput() {
    return myDelegate.hasDeferredOutput();
  }

  @Override
  public void performWhenNoDeferredOutput(@NotNull Runnable runnable) {
    myDelegate.performWhenNoDeferredOutput(runnable);
  }

  @Override
  public void setHelpId(@NotNull String helpId) {
    myDelegate.setHelpId(helpId);
  }

  @Override
  public void addMessageFilter(@NotNull Filter filter) {
    myDelegate.addMessageFilter(filter);
  }

  @Override
  public void setProcessTextFilter(@Nullable BiPredicate<ProcessEvent, Key> filter) {
    myDelegate.setProcessTextFilter(filter);
  }

  @Override
  public @Nullable BiPredicate<ProcessEvent, Key> getProcessTextFilter() {
    return myDelegate.getProcessTextFilter();
  }

  @Override
  public void printHyperlink(@NotNull String hyperlinkText, @Nullable HyperlinkInfo info) {
    myDelegate.printHyperlink(hyperlinkText, info);
  }

  @Override
  public int getContentSize() {
    return myDelegate.getContentSize();
  }

  @Override
  public boolean canPause() {
    return myDelegate.canPause();
  }

  @Override
  public AnAction @NotNull [] createConsoleActions() {
    return myDelegate.createConsoleActions();
  }

  @Override
  public void allowHeavyFilters() {
    myDelegate.allowHeavyFilters();
  }

  @Override
  public void requestScrollingToEnd() {
    myDelegate.requestScrollingToEnd();
  }

  @Override
  public @Nullable Editor getEditor() {
    return myDelegate.getEditor();
  }

  @RequiredUIAccess
  @Override
  public void foldImmediately() {
    myDelegate.foldImmediately();
  }

  @Override
  public @Nullable Component getUIComponent() {
    return myDelegate.getUIComponent();
  }

  @Override
  public @Nullable Component getUIPreferredFocusableComponent() {
    return myDelegate.getUIPreferredFocusableComponent();
  }

  @Override
  public JComponent getComponent() {
    return myDelegate.getComponent();
  }

  @Override
  public JComponent getPreferredFocusableComponent() {
    return myDelegate.getPreferredFocusableComponent();
  }

  @Override
  public void dispose() {
  }

  /**
   * Copy-paste of IntelliJ {@code ConsoleViewImpl#updatePredefinedFiltersLater()}
   */
  public static void updatePredefinedFiltersLater(@NotNull Project project, @NotNull ConsoleView consoleView) {
    ReadAction
      .nonBlocking(() -> ConsoleViewUtil.computeConsoleFilters(project, consoleView, GlobalSearchScope.allScope(project)))
      .expireWith(consoleView)
      // consulo: the modality getter is evaluated on the calling (pooled) thread, and the component (console editor) may only be
      //  created on the UI thread, so the default modality is used instead of the component one
      .finishOnUiThread(
        app -> app.getDefaultModalityState(), filters -> filters.forEach(consoleView::addMessageFilter)
      ).submit(AppExecutorUtil.getAppExecutorService());
  }
}
