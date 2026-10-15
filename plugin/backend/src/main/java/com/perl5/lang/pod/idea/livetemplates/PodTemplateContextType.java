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

package com.perl5.lang.pod.idea.livetemplates;

import consulo.application.Application;
import consulo.language.editor.template.context.BaseTemplateContextType;
import consulo.language.editor.template.context.EverywhereContextType;
import consulo.language.editor.template.context.TemplateActionContext;
import consulo.language.editor.template.context.TemplateContextType;
import consulo.localize.LocalizeValue;
import consulo.util.lang.StringUtil;
import consulo.language.file.FileViewProvider;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiWhiteSpace;
import consulo.language.ast.IElementType;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.language.psi.PsiUtilCore;
import com.perl5.PerlBundle;
import com.perl5.lang.pod.PodLanguage;
import com.perl5.lang.pod.parser.psi.mixin.PodOverSectionContent;
import com.perl5.lang.pod.parser.psi.mixin.PodSectionItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.perl5.lang.pod.lexer.PodElementTypes.POD_OUTER;
import static com.perl5.lang.pod.parser.PodElementTypesGenerated.POD_CODE;
import static com.perl5.lang.pod.parser.PodElementTypesGenerated.POD_NEWLINE;

public abstract class PodTemplateContextType extends BaseTemplateContextType {
  private final @Nullable String myBaseContextId;

  // consulo: context id and base context id are declared in upstream xml (<liveTemplateContext contextId=... baseContextId=...>)
  public PodTemplateContextType(@NotNull String contextId,
                                @Nullable String baseContextId,
                                @NotNull String presentableName) {
    super(contextId, LocalizeValue.localizeTODO(presentableName), baseContextId == null ? EverywhereContextType.class : null);
    myBaseContextId = baseContextId;
  }

  // consulo: base context is looked up by id, all POD contexts are subclasses of Generic, so lookup by class is ambiguous
  @Override
  public @Nullable TemplateContextType getBaseContextType() {
    if (myBaseContextId == null) {
      return super.getBaseContextType();
    }
    return Application.get().getExtensionPoint(TemplateContextType.class)
      .findFirstSafe(it -> myBaseContextId.equals(it.getContextId()));
  }

  @Override
  public boolean isInContext(@NotNull TemplateActionContext templateActionContext) {
    var psiFile = templateActionContext.getFile();
    var startOffset = templateActionContext.getStartOffset();
    PsiFile podFile = psiFile.getViewProvider().getPsi(PodLanguage.INSTANCE);

    if (podFile == null) {
      return false;
    }

    PsiElement element = podFile.findElementAt(startOffset);

    if (element == null) {
      element = podFile.findElementAt(startOffset - 1);
    }

    if (element == null) {
      return false;
    }

    return isInContext(element);
  }

  protected abstract boolean isInContext(PsiElement element);

  public static class Generic extends PodTemplateContextType {
    public Generic() {
      this("POD", null, PodLanguage.NAME);
    }

    public Generic(@NotNull String contextId, @Nullable String baseContextId, String presentableName) {
      super(contextId, baseContextId, presentableName);
    }

    @Override
    protected boolean isInContext(PsiElement element) {
      return false;
    }
  }

  public static class CommandPosition extends PodTemplateContextType.Generic {
    public CommandPosition() {
      this("POD_COMMAND", PerlBundle.message("label.command.position"));
    }

    public CommandPosition(@NotNull String contextId, String presentableName) {
      super(contextId, "POD", presentableName);
    }

    @Override
    protected boolean isInContext(PsiElement element) {
      int startOffset = element.getNode().getStartOffset();

      if (startOffset == 0) {
        return true;
      }
      else {
        FileViewProvider viewProvider = element.getContainingFile().getViewProvider();

        PsiElement currentElement = viewProvider.findElementAt(startOffset, PodLanguage.INSTANCE);
        if (currentElement != null && currentElement.getNode().getElementType() == POD_CODE) {
          return false;
        }

        PsiElement prevElement = viewProvider.findElementAt(startOffset - 1, PodLanguage.INSTANCE);
        IElementType prevElementType = PsiUtilCore.getElementType(prevElement);

        if (prevElementType == POD_NEWLINE || prevElementType == POD_OUTER) {
          return true;
        }

        if (prevElement instanceof PsiWhiteSpace && StringUtil.equals(prevElement.getText(), "\n")) {
          while (true) {
            if (prevElement == null || prevElement.getTextOffset() == 0) {
              return true;
            }
            else if (prevElement.getNode().getElementType() == POD_NEWLINE) {
              return true;
            }
            else if (!(prevElement instanceof PsiWhiteSpace)) {
              return false;
            }
            prevElement = viewProvider.findElementAt(prevElement.getNode().getStartOffset() - 1, PodLanguage.INSTANCE);
          }
        }
      }
      return super.isInContext(element);
    }
  }

  public static class InsideOver extends CommandPosition {
    public InsideOver(@NotNull String contextId, String presentableName) {
      super(contextId, presentableName);
    }

    @Override
    protected boolean isInContext(PsiElement element) {
      return super.isInContext(element) && PsiTreeUtil.getParentOfType(element, PodOverSectionContent.class) != null;
    }

    protected @Nullable PodSectionItem getFirstSectionItem(PsiElement element) {
      PodOverSectionContent sectionContent = PsiTreeUtil.getParentOfType(element, PodOverSectionContent.class);
      return sectionContent == null ? null : sectionContent.getFirstItem();
    }
  }

  public static class InsideOverBulleted extends InsideOver {
    public InsideOverBulleted() {
      super("POD_OVER_BULLETED", PerlBundle.message("label.inside.over.block.bulleted"));
    }

    @Override
    protected boolean isInContext(PsiElement element) {
      if (!super.isInContext(element)) {
        return false;
      }

      PodSectionItem firstItem = getFirstSectionItem(element);
      return firstItem == null || firstItem.isBulleted();
    }
  }

  public static class InsideOverNotBulleted extends InsideOver {
    public InsideOverNotBulleted() {
      super("POD_OVER_NOT_BULLETED", PerlBundle.message("label.inside.over.block.not.bulleted"));
    }

    @Override
    protected boolean isInContext(PsiElement element) {
      if (!super.isInContext(element)) {
        return false;
      }

      PodSectionItem firstItem = getFirstSectionItem(element);
      return firstItem == null || !firstItem.isBulleted();
    }
  }
}
