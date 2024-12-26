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

package com.perl5.lang.perl.psi;

import com.perl5.lang.perl.psi.utils.PerlPsiUtil;
import consulo.application.progress.ProgressManager;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiElementVisitor;
import consulo.language.psi.PsiRecursiveElementVisitor;
import consulo.language.psi.stub.StubElement;
import org.jetbrains.annotations.NotNull;

/**
 * Visitor that attempts to use stubs tree if available
 */
public class PsiStubsAwareRecursiveVisitor extends PsiRecursiveElementVisitor implements SelfStoppingVisitor {
  private boolean myStopped = false;

  @Override
  public void visitElement(@NotNull PsiElement element) {
    if (isStopped()) {
      return;
    }
    if (!tryUseStubs(element, this)) {
      super.visitElement(element);
    }
  }

  @Override
  public boolean isStopped() {
    return myStopped;
  }

  public void stop() {
    myStopped = true;
  }

  public static boolean tryUseStubs(@NotNull PsiElement element, @NotNull PsiElementVisitor visitor) {
    StubElement<?> stubElement = PerlPsiUtil.getStubFromElement(element);

    if (stubElement == null) {
      return false;
    }
    ProgressManager.checkCanceled();
    for (StubElement<?> childrenStub : stubElement.getChildrenStubs()) {
      if (visitor instanceof SelfStoppingVisitor && ((SelfStoppingVisitor)visitor).isStopped()) {
        break;
      }
      PsiElement psi = childrenStub.getPsi();
      if (psi != null) {
        psi.accept(visitor);
      }
    }
    return true;
  }
}
