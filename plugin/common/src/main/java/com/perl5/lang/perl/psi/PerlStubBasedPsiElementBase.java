/*
 * Copyright 2015-2026 Alexandr Evstigneev
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

import consulo.language.impl.psi.stub.StubBasedPsiElementBase;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.resolve.ResolveState;
import consulo.language.psi.StubBasedPsiElement;
import consulo.language.psi.resolve.PsiScopeProcessor;
import consulo.language.psi.stub.StubElement;
import consulo.language.ast.IElementType;
import com.perl5.lang.perl.psi.utils.PerlResolveUtilCore;
import org.jetbrains.annotations.NotNull;


public class PerlStubBasedPsiElementBase<T extends StubElement<?>> extends StubBasedPsiElementBase<T> implements StubBasedPsiElement<T> {
  public PerlStubBasedPsiElementBase(@NotNull T stub, @NotNull IElementType nodeType) {
    super(stub, nodeType, null); // consulo: (stub, IElementType, node) constructor, the 2-arg one requires IStubElementType
  }

  public PerlStubBasedPsiElementBase(@NotNull ASTNode node) {
    super(node);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "(" + getIElementType() + ")";
  }

  // TODO consulo: Consulo StubBasedPsiElementBase has neither getIElementType() nor getElementTypeImpl() (element type field is private);
  //  emulating via stub type or AST node type
  public final IElementType getIElementType() {
    T stub = getStub();
    return stub != null ? stub.getStubType() : getNode().getElementType();
  }

  @Override
  public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                     @NotNull ResolveState state,
                                     PsiElement lastParent,
                                     @NotNull PsiElement place) {

    return PerlResolveUtilCore.processChildren(this, processor, state, lastParent, place) &&
           processor.execute(this, state);
  }
}
