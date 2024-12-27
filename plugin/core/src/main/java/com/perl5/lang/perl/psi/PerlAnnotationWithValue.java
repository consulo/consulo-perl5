/*
 * Copyright 2015-2022 Alexandr Evstigneev
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

import com.perl5.lang.perl.idea.codeInsight.typeInference.value.PerlValue;
import com.perl5.lang.perl.psi.utils.PerlAnnotations;
import consulo.language.psi.PsiElement;
import org.jetbrains.annotations.NotNull;


public interface PerlAnnotationWithValue extends PsiElement, PerlAnnotation {
  /**
   * @return a value described in this annotation
   */
  default @NotNull PerlValue getValue() {
    return PerlAnnotations.getParameterValue(this);
  }
}
