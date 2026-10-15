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

package com.perl5.lang.tt2.idea.liveTemplates;

import consulo.language.editor.template.context.BaseTemplateContextType;
import consulo.language.editor.template.context.EverywhereContextType;
import consulo.language.editor.template.context.TemplateActionContext;
import consulo.language.editor.template.context.TemplateContextType;
import consulo.localize.LocalizeValue;
import consulo.language.file.FileViewProvider;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiErrorElement;
import consulo.language.psi.PsiFile;
import consulo.language.ast.IElementType;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.language.psi.PsiUtilCore;
import com.perl5.lang.tt2.TemplateToolkitBundle;
import com.perl5.lang.tt2.TemplateToolkitLanguage;
import com.perl5.lang.tt2.lexer.TemplateToolkitSyntaxElements;
import com.perl5.lang.tt2.psi.PsiElsifBranch;
import com.perl5.lang.tt2.psi.impl.*;
import com.perl5.lang.tt2.utils.TemplateToolkitPsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.perl5.lang.tt2.parser.TemplateToolkitElementTypesGenerated.TT2_HTML;
import static com.perl5.lang.tt2.parser.TemplateToolkitElementTypesGenerated.TT2_IDENTIFIER;


// consulo: context ids and base contexts are declared in upstream xml (<liveTemplateContext contextId=... baseContextId=...>),
//  Consulo takes them in the constructor
// TODO consulo: Consulo resolves context classes with isInstance (ExtensionPoint#findExtension), CommandPosition* and Postfix
//  extend Generic, so Generic.class (base context) and CommandPosition.class (templates) may resolve to a subclass registration
public abstract class TemplateToolkitTemplateContextType extends BaseTemplateContextType {
  public TemplateToolkitTemplateContextType(@NotNull String contextId,
                                            @NotNull String presentableName,
                                            @Nullable Class<? extends TemplateContextType> baseContextType) {
    super(contextId, LocalizeValue.localizeTODO(presentableName), baseContextType);
  }

  protected abstract boolean isInContext(@NotNull PsiElement element);

  @Override
  public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
    var file = templateActionContext.getFile();
    var offset = templateActionContext.getStartOffset();
    FileViewProvider viewProvider = file.getViewProvider();
    PsiFile ttFile = viewProvider.getPsi(TemplateToolkitLanguage.INSTANCE);

    if (ttFile == null) {
      return false;
    }
    PsiElement element = viewProvider.findElementAt(offset, TemplateToolkitLanguage.INSTANCE);
    if (element == null && offset > 0) {
      element = viewProvider.findElementAt(offset - 1, TemplateToolkitLanguage.INSTANCE);
    }

    return element != null && element.getLanguage() == TemplateToolkitLanguage.INSTANCE && isInContext(element);
  }

  public static class Generic extends TemplateToolkitTemplateContextType {
    public Generic() {
      this("Template Toolkit 2", TemplateToolkitLanguage.NAME, EverywhereContextType.class);
    }

    public Generic(@NotNull String contextId,
                   String presentableName,
                   @Nullable Class<? extends TemplateContextType> baseContextType) {
      super(contextId, presentableName, baseContextType);
    }

    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
      return false;
    }
  }

  public static class Postfix extends TemplateToolkitTemplateContextType.Generic {
    public Postfix() {
      this("Template Toolkit 2.postfix", TemplateToolkitBundle.message("label.postfix"));
    }

    public Postfix(@NotNull String contextId, String presentableName) {
      super(contextId, presentableName, Generic.class);
    }

    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
      IElementType tokenType = element.getNode().getElementType();
      if (tokenType != TT2_IDENTIFIER) {
        return false;
      }

      PsiElement parent = element.getParent();
      if (!(parent instanceof PsiErrorElement)) {
        return false;
      }

      PsiElement prevElement = TemplateToolkitPsiUtil.getPrevSignificantSibling(parent);
      return prevElement != null && TemplateToolkitSyntaxElements.ATOMIC_EXPRESSIONS.contains(prevElement.getNode().getElementType());
    }
  }

  public static class CommandPosition extends TemplateToolkitTemplateContextType.Generic {
    public CommandPosition() {
      this("Template Toolkit 2.command", TemplateToolkitBundle.message("label.directive"));
    }

    public CommandPosition(@NotNull String contextId, String presentableName) {
      super(contextId, presentableName, Generic.class);
    }

    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
      return PsiUtilCore.getElementType(element) == TT2_HTML;
    }
  }

  public static class CommandPositionElsif extends TemplateToolkitTemplateContextType.CommandPosition {
    public CommandPositionElsif() {
      this("Template Toolkit 2.command.elsif", TemplateToolkitBundle.message("label.elsif.else.branch"));
    }

    public CommandPositionElsif(@NotNull String contextId, String presentableName) {
      super(contextId, presentableName);
    }

    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
      return super.isInContext(element) &&
             (PsiTreeUtil.getParentOfType(element, PsiIfBranchImpl.class) != null ||
              PsiTreeUtil.getParentOfType(element, PsiUnlessBranchImpl.class) != null ||
              PsiTreeUtil.getParentOfType(element, PsiElsifBranch.class) != null);
    }
  }

  public static class CommandPositionCase extends TemplateToolkitTemplateContextType.CommandPosition {
    public CommandPositionCase() {
      this("Template Toolkit 2.command.case", TemplateToolkitBundle.message("label.case.branch"));
    }

    public CommandPositionCase(@NotNull String contextId, String presentableName) {
      super(contextId, presentableName);
    }

    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
      return super.isInContext(element) && PsiTreeUtil.getParentOfType(element, PsiSwitchBlockImpl.class) != null;
    }
  }

  public static class CommandPositionCatch extends TemplateToolkitTemplateContextType.CommandPosition {
    public CommandPositionCatch() {
      this("Template Toolkit 2.command.catch", TemplateToolkitBundle.message("label.catch.final.branch"));
    }

    public CommandPositionCatch(@NotNull String contextId, String presentableName) {
      super(contextId, presentableName);
    }

    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
      return super.isInContext(element) &&
             (PsiTreeUtil.getParentOfType(element, PsiTryBranchImpl.class) != null ||
              PsiTreeUtil.getParentOfType(element, PsiCatchBranchImpl.class) != null);
    }
  }
}
