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

package com.perl5.lang.perl.parser.trycatch.idea.liveTemplates;

import com.intellij.psi.PsiElement;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.idea.livetemplates.PerlTemplateContextType;
import com.perl5.lang.perl.parser.trycatch.TryCatchElementPatterns;


public class TryCatchTemplateContextType extends PerlTemplateContextType.Prefix implements TryCatchElementPatterns {
  public TryCatchTemplateContextType() {
    super(PerlBundle.message("perl.template.context.catch"));
    }

    @Override
    public boolean isInContext(PsiElement element) {
      return super.isInContext(element) && ELEMENT_AFTER_TRY_CATCH.accepts(element);
    }
}
