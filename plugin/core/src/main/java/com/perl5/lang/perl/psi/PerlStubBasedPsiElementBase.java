/*
 * Copyright 2015-2020 Alexandr Evstigneev
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

package com.perl5.lang.perl.psi;

import com.perl5.lang.perl.psi.utils.PerlResolveUtil;
import consulo.language.ast.ASTNode;
import consulo.language.impl.psi.stub.StubBasedPsiElementBase;
import consulo.language.psi.PsiElement;
import consulo.language.psi.resolve.PsiScopeProcessor;
import consulo.language.psi.resolve.ResolveState;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class PerlStubBasedPsiElementBase<T extends StubElement<?>> extends StubBasedPsiElementBase<T> {
  public PerlStubBasedPsiElementBase(@NotNull T stub, @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  public PerlStubBasedPsiElementBase(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "(" + getElementType().toString() + ")";
  }

  @Override
  public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                     @NotNull ResolveState state,
                                     PsiElement lastParent,
                                     @NotNull PsiElement place) {

    return PerlResolveUtil.processChildren(this, processor, state, lastParent, place) &&
           processor.execute(this, state);
  }

  public @NotNull List<PsiPerlExpr> getExprList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, PsiPerlExpr.class);
  }
}
