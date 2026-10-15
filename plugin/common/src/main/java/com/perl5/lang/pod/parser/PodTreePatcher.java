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

package com.perl5.lang.pod.parser;

import com.perl5.lang.pod.PodLanguage;
import consulo.language.Language;
import consulo.language.impl.ast.CompositeElement;
import consulo.language.impl.ast.FileElement;
import consulo.language.impl.ast.TreeElement;
import consulo.language.psi.OuterLanguageElement;
import consulo.language.impl.psi.template.TreePatcher;
import org.jetbrains.annotations.NotNull;


// consulo: SimpleTreePatcher is package-private in Consulo, so TreePatcher is implemented directly and SimpleTreePatcher#insert is inlined
public class PodTreePatcher implements TreePatcher {
  @Override
  public void insert(@NotNull CompositeElement parent, TreeElement anchorBefore, @NotNull OuterLanguageElement toInsert) {
    while (anchorBefore != null && anchorBefore.getTreePrev() == null && !(parent instanceof FileElement)) {
      anchorBefore = anchorBefore.getTreeParent();
      parent = anchorBefore.getTreeParent();
    }

    // consulo: SimpleTreePatcher#insert
    if (anchorBefore != null) {
      anchorBefore.rawInsertBeforeMe((TreeElement)toInsert);
    }
    else {
      parent.rawAddChildren((TreeElement)toInsert);
    }
  }

  @Override
  public @NotNull Language getLanguage() {
    return PodLanguage.INSTANCE;
  }
}
