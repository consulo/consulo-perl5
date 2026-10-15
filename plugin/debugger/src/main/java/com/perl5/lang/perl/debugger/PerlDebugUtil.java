/*
 * Copyright 2015-2021 Alexandr Evstigneev
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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import consulo.application.ApplicationManager;
import consulo.project.Project;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.execution.debug.XDebuggerManager;
import consulo.execution.debug.breakpoint.XLineBreakpoint;
import consulo.execution.debug.frame.XCompositeNode;
import consulo.execution.debug.frame.XValueChildrenList;
import com.perl5.lang.perl.debugger.breakpoints.PerlLineBreakpointProperties;
import com.perl5.lang.perl.debugger.breakpoints.PerlLineBreakpointType;
import com.perl5.lang.perl.debugger.protocol.PerlDebuggingEventBreakpoint;
import com.perl5.lang.perl.debugger.protocol.PerlDebuggingTransactionHandler;
import com.perl5.lang.perl.debugger.protocol.PerlValueDescriptor;
import com.perl5.lang.perl.debugger.protocol.PerlValueRequestDescriptor;
import com.perl5.lang.perl.debugger.values.PerlLoadableXValueContainer;
import com.perl5.lang.perl.debugger.values.PerlXNamedValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;


public class PerlDebugUtil {
  public static @Nullable XLineBreakpoint<?> findBreakpoint(final Project project, final PerlDebuggingEventBreakpoint breakpointBase) {
    final XLineBreakpoint<?>[] result = new XLineBreakpoint<?>[]{null};

    ApplicationManager.getApplication().runReadAction(() -> {
      String path = breakpointBase.getPath();

      VirtualFile virtualFile;
      String virtualFileUrl;

      virtualFile = VirtualFileUtil.findFileByIoFile(new File(breakpointBase.getDebugThread().getDebugProfileState().mapPathToLocal(path)), true);

      if (virtualFile == null) {
        virtualFileUrl = PerlRemoteFileSystem.PROTOCOL_PREFIX + path;
      }
      else {
        virtualFileUrl = virtualFile.getUrl();
      }

      Collection<? extends XLineBreakpoint<PerlLineBreakpointProperties>> breakpoints =
        XDebuggerManager.getInstance(project).getBreakpointManager().getBreakpoints(PerlLineBreakpointType.class);
      for (XLineBreakpoint<PerlLineBreakpointProperties> breakpoint : breakpoints) {
        if (StringUtil.equals(breakpoint.getFileUrl(), virtualFileUrl) && breakpoint.getLine() == breakpointBase.getLine()) {
          result[0] = breakpoint;
          return;
        }
      }
    });
    return result[0];
  }

  public static void requestAndComputeChildren(@NotNull XCompositeNode node,
                                               @NotNull PerlLoadableXValueContainer container) {
    PerlStackFrame perlStackFrame = container.getStackFrame();
    PerlDebugThread thread = perlStackFrame.getPerlExecutionStack().getSuspendContext().getDebugThread();

    final int frameSize = XCompositeNode.MAX_CHILDREN_TO_SHOW;
    thread.sendCommandAndGetResponse(
      "getchildren",
      new PerlValueRequestDescriptor(container.getCurrentOffset(), frameSize, container.getDataKey()),
      new PerlDebuggingTransactionHandler() {
        @Override
        public void run(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
          PerlValueDescriptor[] descriptors = jsonDeserializationContext.deserialize(
            jsonObject.getAsJsonArray("data"), PerlValueDescriptor[].class
          );

          XValueChildrenList list = new XValueChildrenList();
          for (PerlValueDescriptor descriptor : descriptors) {
            list.add(new PerlXNamedValue(descriptor, perlStackFrame));
            container.incCurrentOffset();
          }
          boolean isLast = container.isLast();
          node.addChildren(list, isLast);
          if (!isLast) {
            // TODO consulo: Consulo XCompositeNode#tooManyChildren has no "add next children" callback, the "more" node
            //  calls computeChildren of the container again, which requests the next portion from the current offset
            //  (PerlXNamedValue#computeChildren also re-adds its IO layers/tied children on this call)
            node.tooManyChildren(container.getRemaining());
          }
        }
      });
  }
}
