/*
 * Copyright 2015-2017 Alexandr Evstigneev
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

package com.perl5.lang.perl.idea.manipulators;

import com.perl5.lang.perl.psi.mixins.PerlStringBareMixin;
import consulo.document.util.TextRange;
import consulo.language.impl.psi.LeafPsiElement;
import consulo.language.psi.AbstractElementManipulator;
import consulo.language.psi.PsiElement;
import consulo.language.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class PerlBareStringManipulator extends AbstractElementManipulator<PerlStringBareMixin> {
  @Override
  public PerlStringBareMixin handleContentChange(@NotNull PerlStringBareMixin element, @NotNull TextRange range, String newContent)
    throws IncorrectOperationException {
    PsiElement leafElement = element.getFirstChild();
    assert leafElement instanceof LeafPsiElement : "Leaf is null for: " + element.getText();
    ((LeafPsiElement)leafElement).replaceWithText(range.replace(leafElement.getText(), newContent));
    return element;
  }
}
