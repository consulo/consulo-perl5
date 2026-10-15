/*
 * Copyright 2015-2023 Alexandr Evstigneev
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

package com.perl5.lang.tt2.idea;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.Language;
import consulo.language.editor.inspection.ProblemsHolder;
import consulo.language.editor.rawHighlight.HighlightDisplayLevel;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementVisitor;
import com.perl5.lang.perl.idea.inspections.PerlInspection;
import com.perl5.lang.tt2.TemplateToolkitBundle;
import com.perl5.lang.tt2.TemplateToolkitLanguage;
import com.perl5.lang.tt2.parser.TemplateToolkitParserUtil;
import com.perl5.lang.tt2.psi.*;
import consulo.localize.LocalizeValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@ExtensionImpl
public class TemplateToolkitOrphanDirectiveInspection extends PerlInspection {
  // consulo: metadata from upstream <localInspection> registration in perl5.lang.tt2.backend.main.xml
  @Override
  public @NotNull String getShortName() {
    return "TT2OrphanReply";
  }

  @Override
  public @Nullable Language getLanguage() {
    return TemplateToolkitLanguage.INSTANCE;
  }

  @Override
  public @NotNull LocalizeValue getDisplayName() {
    return LocalizeValue.localizeTODO(TemplateToolkitBundle.message("tt2.inspection.orphan.reply.display.name"));
  }

  @Override
  public @NotNull LocalizeValue getGroupDisplayName() {
    return LocalizeValue.localizeTODO(TemplateToolkitBundle.message("tt2.group.name"));
  }

  @Override
  public @NotNull HighlightDisplayLevel getDefaultLevel() {
    return HighlightDisplayLevel.ERROR;
  }

  @Override
  public boolean isEnabledByDefault() {
    return true;
  }

  @Override
  public @NotNull PsiElementVisitor buildVisitor(final @NotNull ProblemsHolder holder, boolean isOnTheFly) {
    return new TemplateToolkitVisitor() {
      @Override
      public void visitEndDirective(@NotNull PsiEndDirective o) {
        checkInAnyBlock(o);
      }

      @Override
      public void visitElseDirective(@NotNull PsiElseDirective o) {
        checkInIf(o);
      }

      @Override
      public void visitElsifDirective(@NotNull PsiElsifDirective o) {
        checkInIf(o);
      }

      @Override
      public void visitCatchDirective(@NotNull PsiCatchDirective o) {
        checkInTry(o);
      }

      @Override
      public void visitFinalDirective(@NotNull PsiFinalDirective o) {
        checkInTry(o);
      }

      @Override
      public void visitCaseDirective(@NotNull PsiCaseDirective o) {
        if (isInSwitch(o)) {
          return;
        }

        registerError(holder, o, TemplateToolkitBundle.message("tt2.error.case.outside.switch"));
      }

      private boolean isInSwitch(@NotNull PsiElement element) {
        PsiElement parent = element.getParent();
        if (parent == null) // branch
        {
          return false;
        }
        return parent.getParent() instanceof PsiSwitchBlock;
      }

      private void checkInIf(@NotNull PsiElement element) {
        if (isInIf(element)) {
          return;
        }

        registerError(holder, element, TemplateToolkitBundle.message("tt2.error.outside.if.block"));
      }

      private boolean isInIf(@NotNull PsiElement element) {
        PsiElement parent = element.getParent();
        if (parent == null) // branch
        {
          return false;
        }
        parent = parent.getParent();
        return parent instanceof PsiIfBlock || parent instanceof PsiUnlessBlock;
      }

      private void checkInTry(@NotNull PsiElement element) {
        if (isInTry(element)) {
          return;
        }

        registerError(holder, element, TemplateToolkitBundle.message("tt2.error.outside.try.block"));
      }

      private boolean isInTry(@NotNull PsiElement element) {
        PsiElement parent = element.getParent();
        if (parent == null) // branch
        {
          return false;
        }
        return parent.getParent() instanceof PsiTryCatchBlock;
      }


      private void checkInAnyBlock(@NotNull PsiElement element) {
        PsiElement parent = element.getParent();
        if (parent != null && TemplateToolkitParserUtil.BLOCK_CONTAINERS.contains(parent.getNode().getElementType())) {
          return;
        }
        registerError(holder, element, TemplateToolkitBundle.message("tt2.error.end.outside.block"));
      }
    };
  }
}
