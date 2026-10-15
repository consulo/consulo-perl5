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

package com.perl5.lang.perl.psi.references;

import consulo.language.editor.TargetElementUtilExtender;
import consulo.language.editor.TargetElementUtil;
import consulo.codeEditor.Editor;

import com.perl5.lang.perl.psi.PerlVariableNameElement;
import com.perl5.lang.perl.psi.utils.PerlPsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiPolyVariantReference;
import consulo.language.psi.PsiReference;
import consulo.language.psi.ResolveResult;

import java.util.Set;


// consulo: TargetElementUtilExtender is application-wide (upstream evaluator is registered for Perl5 language only)
public class PerlTargetElementEvaluatorEx2 implements TargetElementUtilExtender {
  // consulo: adapter for TargetElementEvaluatorEx2.adjustReferenceOrReferencedElement; first non-null result wins, so unchanged
  // element is reported as null
  @Override
  public @Nullable PsiElement modifyReferenceOrReferencedElement(@Nullable PsiElement refElement,
                                                                 PsiFile file,
                                                                 Editor editor,
                                                                 Set<String> flags,
                                                                 int offset) {
    PsiElement result = adjustReferenceOrReferencedElement(file, editor, offset, 0, refElement);
    return result == refElement ? null : result;
  }

  public @Nullable PsiElement adjustReferenceOrReferencedElement(@NotNull PsiFile file,
                                                                 @NotNull Editor editor,
                                                                 int offset,
                                                                 int flags,
                                                                 @Nullable PsiElement refElement) {
    if (refElement == null) {
      PsiReference ref = TargetElementUtil.findReference(editor, offset);
      if (ref instanceof PerlCachingReference) {
        ResolveResult[] resolveResults = ((PsiPolyVariantReference)ref).multiResolve(false);
        if (resolveResults.length > 0) {
          return resolveResults[0].getElement();
        }
      }
    }
    return refElement;
  }


  @Override
  public @Nullable PsiElement getNamedElement(@NotNull PsiElement element) {
    return PerlPsiUtil.getLightNameIdentifierOwner(element);
  }


  // consulo: adapter for TargetElementEvaluator.getElementByReference
  @Override
  public @Nullable PsiElement getReferenceOrReferencedElement(PsiReference reference, Set<String> flags) {
    return getElementByReference(reference, 0);
  }

  // fixme WTF?
  public @Nullable PsiElement getElementByReference(@NotNull PsiReference ref, int flags) {
    return null;
  }

  @Override
  public boolean isIdentifierPart(PsiFile file, @NotNull CharSequence text, int offset) {
    return file.findElementAt(offset) instanceof PerlVariableNameElement;
  }
}
