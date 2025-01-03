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

package com.perl5.lang.perl.idea.structureView;

import com.perl5.lang.perl.idea.structureView.elements.PerlLeafStructureViewElement;
import com.perl5.lang.perl.idea.structureView.elements.PerlStructureViewElement;
import com.perl5.lang.perl.idea.structureView.filters.*;
import com.perl5.lang.perl.idea.structureView.groupers.PerlAttributeGrouper;
import consulo.codeEditor.Editor;
import consulo.fileEditor.structureView.StructureViewModel;
import consulo.fileEditor.structureView.StructureViewTreeElement;
import consulo.fileEditor.structureView.tree.Filter;
import consulo.fileEditor.structureView.tree.Grouper;
import consulo.fileEditor.structureView.tree.Sorter;
import consulo.language.editor.structureView.StructureViewModelBase;
import consulo.language.psi.PsiFile;
import org.jetbrains.annotations.NotNull;


public class PerlStructureViewModel extends StructureViewModelBase implements StructureViewModel.ElementInfoProvider {
  private static final Filter[] FILTERS = new Filter[]{
    PerlPodFilter.INSTANCE,
    PerlVariableFilter.INSTANCE,
    PerlGlobFilter.INSTANCE,
    PerlConstantFilter.INSTANCE,
    PerlMethodFilter.INSTANCE,
    PerlDeclarationFilter.INSTANCE,
    PerlInheritedFilter.INSTANCE,
    PerlImportedFilter.INSTANCE
  };

  private static final Grouper[] GROUPERS = new Grouper[]{
    new PerlAttributeGrouper()
  };

  public PerlStructureViewModel(PsiFile psiFile, Editor editor) {
    super(psiFile, editor, new PerlStructureViewElement(psiFile) {
    });
  }

  @Override
  public Sorter @NotNull [] getSorters() {
    return new Sorter[]{Sorter.ALPHA_SORTER};
  }

  @Override
  public Grouper @NotNull [] getGroupers() {
    return GROUPERS;
  }

  @Override
  public Filter @NotNull [] getFilters() {
    return FILTERS;
  }

  @Override
  public boolean isAlwaysShowsPlus(StructureViewTreeElement structureViewTreeElement) {
    return false;
  }

  @Override
  public boolean isAlwaysLeaf(StructureViewTreeElement structureViewTreeElement) {
    return structureViewTreeElement instanceof PerlLeafStructureViewElement;
  }
}
