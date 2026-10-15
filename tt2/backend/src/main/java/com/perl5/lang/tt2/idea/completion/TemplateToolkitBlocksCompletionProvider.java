/*
 * Copyright 2015-2024 Alexandr Evstigneev
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

package com.perl5.lang.tt2.idea.completion;

import consulo.language.editor.completion.CompletionParameters;
import consulo.language.editor.completion.CompletionProvider;
import consulo.language.editor.completion.CompletionResultSet;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import consulo.language.icon.IconDescriptorUpdaters;
import consulo.util.lang.StringUtil;
import consulo.language.psi.PsiElement;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.language.psi.PsiUtilCore;
import consulo.language.util.ProcessingContext;
import com.perl5.lang.tt2.psi.TemplateToolkitNamedBlock;
import com.perl5.lang.tt2.psi.TemplateToolkitString;
import com.perl5.lang.tt2.psi.mixins.TemplateToolkitStringMixin;
import org.jetbrains.annotations.NotNull;


public class TemplateToolkitBlocksCompletionProvider implements CompletionProvider {
  @Override
  public void addCompletions(@NotNull CompletionParameters parameters,
                                @NotNull ProcessingContext context,
                                final @NotNull CompletionResultSet result) {
    PsiElement element = parameters.getOriginalPosition();

    if (element == null) {
      return;
    }

    PsiElement stringElement = element.getParent();
    if (!(stringElement instanceof TemplateToolkitString)) {
      return;
    }

    PsiElement container = stringElement.getParent();
    if (!TemplateToolkitStringMixin.BLOCK_NAME_TARGETED_CONTAINERS.contains(PsiUtilCore.getElementType(container))) {
      return;
    }

    PsiTreeUtil.processElements(element.getContainingFile(), element1 -> {
      if (element1 instanceof TemplateToolkitNamedBlock templateToolkitNamedBlock) {
        String blockName = templateToolkitNamedBlock.getName();
        if (StringUtil.isNotEmpty(blockName)) {
          result.addElement(
            LookupElementBuilder.create(element1, blockName)
              .withTypeText("BLOCK", true)
              .withIcon(IconDescriptorUpdaters.getIcon(element1, 0))
          );
        }
      }
      return true;
    });
  }
}
