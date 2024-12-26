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

package com.perl5.lang.pod.idea.codeInsight.navigation;

import com.perl5.lang.pod.PodLanguage;
import com.perl5.lang.pod.parser.psi.references.PodLinkToSectionReference;
import consulo.codeEditor.Editor;
import consulo.language.editor.TargetElementUtil;
import consulo.language.editor.navigation.GotoDeclarationHandler;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiReference;
import consulo.language.psi.ResolveResult;
import consulo.util.collection.ContainerUtil;
import org.jetbrains.annotations.Nullable;

public class PodGoToDeclarationHandler implements GotoDeclarationHandler {
  @Override
  public @Nullable PsiElement[] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
    if (sourceElement == null || !sourceElement.getLanguage().isKindOf(PodLanguage.INSTANCE)) {
      return null;
    }

    PsiReference reference = TargetElementUtil.findReference(editor, offset);
    if (!(reference instanceof PodLinkToSectionReference linkToSectionReference)) {
      return null;
    }

    return ContainerUtil.map(linkToSectionReference.multiResolve(false), ResolveResult::getElement)
      .toArray(PsiElement.EMPTY_ARRAY);
  }
}
