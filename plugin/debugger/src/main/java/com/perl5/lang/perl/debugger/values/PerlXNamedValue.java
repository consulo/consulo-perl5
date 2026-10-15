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

package com.perl5.lang.perl.debugger.values;

import consulo.application.ReadAction;
import consulo.document.Document;
import consulo.project.Project;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.VirtualFile;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiManager;
import consulo.language.psi.resolve.ResolveState;
import consulo.util.lang.ThreeState;
import consulo.execution.debug.XSourcePosition;
import consulo.execution.debug.XDebuggerUtil;
import com.perl5.PerlIcons;
import com.perl5.lang.perl.debugger.PerlDebugUtil;
import com.perl5.lang.perl.debugger.PerlStackFrame;
import com.perl5.lang.perl.debugger.protocol.PerlLayersDescriptor;
import com.perl5.lang.perl.debugger.protocol.PerlValueDescriptor;
import com.perl5.lang.perl.psi.PerlVariable;
import com.perl5.lang.perl.psi.PerlVariableDeclarationElement;
import com.perl5.lang.perl.psi.impl.PerlFileImpl;
import com.perl5.lang.perl.psi.references.scopes.PerlVariableDeclarationSearcher;
import com.perl5.lang.perl.psi.utils.PerlResolveUtilCore;
import com.perl5.lang.perl.psi.utils.PerlVariableType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import consulo.ui.image.Image;
import consulo.execution.debug.frame.XCompositeNode;
import consulo.execution.debug.frame.XInlineDebuggerDataCallback;
import consulo.execution.debug.frame.XNamedValue;
import consulo.execution.debug.frame.XNavigatable;
import consulo.execution.debug.frame.XValueChildrenList;
import consulo.execution.debug.frame.XValueNode;
import consulo.execution.debug.frame.XValuePlace;


public class PerlXNamedValue extends XNamedValue implements PerlLoadableXValueContainer {
  private final PerlStackFrame myStackFrame;
  private final PerlValueDescriptor myPerlValueDescriptor;
  private int myOffset = 0;

  public PerlXNamedValue(@NotNull PerlValueDescriptor descriptor, PerlStackFrame stackFrame) {
    super(descriptor.getName());
    myPerlValueDescriptor = descriptor;
    myStackFrame = stackFrame;
  }

  @Override
  public void computeChildren(@NotNull XCompositeNode node) {
    boolean isExpandable = myPerlValueDescriptor.isExpandable();
    PerlLayersDescriptor layers = myPerlValueDescriptor.getLayers();
    PerlValueDescriptor tiedWith = myPerlValueDescriptor.getTiedWith();
    if (!isExpandable && layers == null && tiedWith == null || StringUtil.isEmpty(myPerlValueDescriptor.getKey())) {
      super.computeChildren(node);
    }

    XValueChildrenList childrenList = new XValueChildrenList();
    if (layers != null) {
      childrenList.add(new PerlXLayersNamedValue(layers));
    }

    if (tiedWith != null) {
      childrenList.addTopGroup(new PerlTiedNamedValue(tiedWith, myStackFrame));
    }

    node.addChildren(childrenList, !isExpandable);

    if (isExpandable) {
      PerlDebugUtil.requestAndComputeChildren(node, this);
    }
  }

  @Override
  public @NotNull PerlStackFrame getStackFrame() {
    return myStackFrame;
  }

  @Override
  public @NotNull String getDataKey() {
    return myPerlValueDescriptor.getKey();
  }

  @Override
  public int getCurrentOffset() {
    return myOffset;
  }

  @Override
  public int getSize() {
    return myPerlValueDescriptor.getSize();
  }

  @Override
  public void incCurrentOffset() {
    myOffset++;
  }

  @Override
  public void computePresentation(@NotNull XValueNode node, @NotNull XValuePlace place) {
    node.setPresentation(calculateIcon(), computeType(), computeValue(), myPerlValueDescriptor.isExpandableNode());
  }

  private String computeValue() {
    String value = myPerlValueDescriptor.getValue();

    String rendered = myPerlValueDescriptor.getRendered();
    return StringUtil.isEmpty(rendered) ? value : rendered + " (" + value + ")";
  }

  protected String computeType() {
    String type = myPerlValueDescriptor.getType();

    int refDepth = myPerlValueDescriptor.getRefDepth();
    if (refDepth == 1) {
      type = "REF to " + type;
    }
    else if (refDepth > 0) {
      type = "REF(" + refDepth + ") to " + type;
    }

    if (myPerlValueDescriptor.getLayers() != null) {
      type += ", IOLayers";
    }

    if (myPerlValueDescriptor.getTiedWith() != null) {
      type += ", Tied";
    }

    if (myPerlValueDescriptor.getFileno() != null) {
      type += ", fileno: " + myPerlValueDescriptor.getFileno();
    }

    return type;
  }

  protected @Nullable Image calculateIcon() {
    String type = myPerlValueDescriptor.getType();
    if (StringUtil.isEmpty(type)) {
      return null;
    }
    else if (StringUtil.startsWith(type, "SCALAR")) {
      return myPerlValueDescriptor.isUtf() ? PerlIcons.UTF_SCALAR_GUTTER_ICON : PerlIcons.SCALAR_GUTTER_ICON;
    }
    else if (StringUtil.startsWith(type, "ARRAY")) {
      return PerlIcons.ARRAY_GUTTER_ICON;
    }
    else if (StringUtil.startsWith(type, "HASH")) {
      return PerlIcons.HASH_GUTTER_ICON;
    }
    else if (StringUtil.startsWith(type, "CODE")) {
      return PerlIcons.SUB_GUTTER_ICON;
    }
    else if (StringUtil.startsWith(type, "GLOB")) {
      return PerlIcons.GLOB_GUTTER_ICON;
    }
    else if (StringUtil.startsWith(type, "FORMAT")) {
      return PerlIcons.FORMAT_GUTTER_ICON;
    }
    else if (StringUtil.startsWith(type, "IO::File")) {
      return PerlIcons.HANDLE_GUTTER_ICON;
    }
    else if (StringUtil.startsWith(type, "Regexp")) {
      return PerlIcons.REGEX_GUTTER_ICON;
    }
    else if (myPerlValueDescriptor.isBlessed()) {
      return PerlIcons.PACKAGE_GUTTER_ICON;
    }
    return null;
  }


  @Override
  public void computeSourcePosition(@NotNull XNavigatable navigatable) {
    if (!computeMySourcePosition(navigatable, null)) {
      super.computeSourcePosition(navigatable);
    }
  }

  protected boolean computeMySourcePosition(@Nullable XNavigatable navigatable, final @Nullable XInlineDebuggerDataCallback callback) {
    String name = myPerlValueDescriptor.getName();

    if (StringUtil.isEmpty(name) || name.length() < 2) {
      return false;
    }

    final PerlVariableType variableType = PerlVariableType.bySigilOrNull(name.charAt(0));
    if (variableType == null) {
      return false;
    }
    if (variableType == PerlVariableType.CODE) {
      return false;
    }

    final String variableName = name.substring(1);

    final XSourcePosition sourcePosition = myStackFrame.getSourcePosition();
    if (sourcePosition == null) {
      return false;
    }

    final Project project = myStackFrame.getPerlExecutionStack().getSuspendContext().getDebugSession().getProject();
    final VirtualFile virtualFile = sourcePosition.getFile();
    PsiFile psiFile = PsiManager.getInstance(project).findFile(virtualFile);

    if (!(psiFile instanceof PerlFileImpl)) {
      return false;
    }

    PsiElement element = psiFile.findElementAt(sourcePosition.getOffset());

    if (element == null) {
      return false;
    }

    if (navigatable != null) {
      PerlVariableDeclarationSearcher variableProcessor = new PerlVariableDeclarationSearcher(variableName, variableType, element);
      PerlResolveUtilCore.treeWalkUp(element, variableProcessor);

      PerlVariableDeclarationElement result = variableProcessor.getResult();
      if (result == null) {
        return false;
      }

      navigatable.setSourcePosition(XDebuggerUtil.getInstance().createPositionByElement(result));
    }
    else if (callback != null) {
      final Document document = psiFile.getViewProvider().getDocument();
      if (document == null) {
        return false;
      }

      final boolean[] found = new boolean[]{false};

      PerlVariableDeclarationSearcher variableProcessor = new PerlVariableDeclarationSearcher(variableName, variableType, element) {
        @Override
        public boolean execute(@NotNull PsiElement possibleElement, @NotNull ResolveState state) {
          boolean result = super.execute(possibleElement, state);

          if (!result) {
            registerElement(getResult());
          }
          else if (possibleElement instanceof PerlVariable perlVariable &&
                   perlVariable.getActualType() == variableType &&
                   StringUtil.equals(variableName, perlVariable.getName())) {
            registerElement(possibleElement);
          }

          return result;
        }

        private void registerElement(@Nullable PsiElement targetElement) {
          if (targetElement == null) {
            return;
          }

          found[0] = true;
          callback.computed(XDebuggerUtil.getInstance().createPositionByElement(targetElement));
        }
      };

      PerlResolveUtilCore.treeWalkUp(element, variableProcessor);
      return found[0];
    }
    return true;
  }


  @Override
  public @NotNull ThreeState computeInlineDebuggerData(@NotNull XInlineDebuggerDataCallback callback) {
    return ReadAction.compute(() -> computeMySourcePosition(null, callback) ? ThreeState.YES : ThreeState.NO);
  }
}
