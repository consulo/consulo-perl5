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

package com.perl5.lang.tt2.psi.references.providers;

import consulo.util.lang.StringUtil;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiFileSystemItem;
import consulo.language.psi.PsiReference;
import consulo.language.psi.PsiReferenceProvider;
import consulo.language.psi.path.FileReferenceSet;
import consulo.language.ast.IElementType;
import consulo.language.ast.TokenSet;
import consulo.language.psi.PsiUtilCore;
import consulo.language.util.ProcessingContext;
import com.perl5.lang.tt2.psi.mixins.TemplateToolkitStringMixin;
import com.perl5.lang.tt2.psi.references.TemplateToolkitBlockReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.perl5.lang.tt2.parser.TemplateToolkitElementTypesGenerated.*;
import static com.perl5.lang.tt2.psi.mixins.TemplateToolkitStringMixin.BLOCK_NAME_TARGETED_CONTAINERS;


public final class TemplateToolkitStringReferenceProvider extends PsiReferenceProvider {
  private static final TokenSet FILES_TARGETED_CONTAINERS =
    TokenSet.create(INSERT_DIRECTIVE, INCLUDE_DIRECTIVE, PROCESS_DIRECTIVE, WRAPPER_DIRECTIVE);

  @Override
  public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
    if (!(element instanceof TemplateToolkitStringMixin)) {
      return PsiReference.EMPTY_ARRAY;
    }
    List<PsiReference> references = new ArrayList<>();
    @Nullable IElementType parentElementType = PsiUtilCore.getElementType(element.getParent());

    if (FILES_TARGETED_CONTAINERS.contains(parentElementType)) {
      Collections.addAll(references, new FileReferenceSet(element) {
        @Override
        public @NotNull Collection<PsiFileSystemItem> computeDefaultContexts() {
          String path = getPathString();
          PsiFile containingFile = this.getContainingFile();
          if (StringUtil.startsWith(path, ".") && containingFile != null && containingFile.getParent() != null) {
            return new ArrayList<>(List.of(containingFile.getParent()));
          }
          return super.computeDefaultContexts();
        }
      }.getAllReferences());
    }

    if (BLOCK_NAME_TARGETED_CONTAINERS.contains(parentElementType)) {
      references.add(new TemplateToolkitBlockReference(element));
    }
    return references.toArray(PsiReference.EMPTY_ARRAY);
  }
}
