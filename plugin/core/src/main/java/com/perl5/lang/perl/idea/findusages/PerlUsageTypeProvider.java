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

package com.perl5.lang.perl.idea.findusages;

import com.perl5.PerlBundle;
import com.perl5.lang.pod.PodLanguage;
import consulo.language.psi.PsiElement;
import consulo.usage.UsageType;
import consulo.usage.UsageTypeProvider;
import org.jetbrains.annotations.Nullable;


public class PerlUsageTypeProvider implements UsageTypeProvider {
  public static final UsageType DOCUMENT_USAGE = new UsageType(() -> PerlBundle.message("perl.usage.type.documentation"));

  @Override
  public @Nullable UsageType getUsageType(PsiElement element) {
    if (element.getLanguage() == PodLanguage.INSTANCE) {
      return DOCUMENT_USAGE;
    }
    return null;
  }
}
