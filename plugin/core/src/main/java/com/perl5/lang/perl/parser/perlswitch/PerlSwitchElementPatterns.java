/*
 * Copyright 2015-2019 Alexandr Evstigneev
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

package com.perl5.lang.perl.parser.perlswitch;

import com.perl5.lang.perl.psi.PsiPerlBlock;
import com.perl5.lang.perl.psi.PsiPerlCaseCompound;
import com.perl5.lang.perl.psi.PsiPerlStatement;
import com.perl5.lang.perl.psi.PsiPerlSwitchCompound;
import consulo.language.pattern.PsiElementPattern;
import consulo.language.psi.PsiElement;

import static com.perl5.lang.perl.idea.PerlElementPatterns.WHITE_SPACE_AND_COMMENTS;
import static consulo.language.pattern.PlatformPatterns.psiElement;


public final class PerlSwitchElementPatterns {
  private PerlSwitchElementPatterns() {
  }

  public static final PsiElementPattern.Capture<PsiPerlStatement> STATEMENT_IN_SWITCH =
    psiElement(PsiPerlStatement.class).withParent(
      psiElement(PsiPerlBlock.class).withParent(PsiPerlSwitchCompound.class)
    );

  public static final PsiElementPattern.Capture<PsiElement> SWITCH_PREFIX_PATTERN = psiElement().inside(STATEMENT_IN_SWITCH);

  public static final PsiElementPattern.Capture<PsiElement> SWITCH_PREFIX_AFTER_CASE_PATTERN = psiElement().inside(
    STATEMENT_IN_SWITCH.afterSiblingSkipping(WHITE_SPACE_AND_COMMENTS, psiElement(PsiPerlCaseCompound.class))
  );
}
