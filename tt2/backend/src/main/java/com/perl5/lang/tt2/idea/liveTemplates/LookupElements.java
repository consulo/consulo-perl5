/*
 * Copyright 2015-2024 Alexandr Evstigneev
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

package com.perl5.lang.tt2.idea.liveTemplates;

import consulo.language.editor.completion.lookup.LookupElement;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import com.perl5.lang.tt2.TemplateToolkitFilterNames;
import org.jetbrains.annotations.NotNull;

/**
 * Kotlin `object LookupElements` declared in TemplateToolkitFilterNameMacro.kt upstream
 */
public final class LookupElements {
  public static final LookupElements INSTANCE = new LookupElements();

  private LookupElements() {
  }

  public static LookupElement @NotNull [] getLOOKUP_ELEMENTS() {
    return LookupElementsHolder.LOOKUP_ELEMENTS;
  }

  /**
   * Lazy initialization holder, upstream uses Kotlin {@code by lazy}
   */
  private static final class LookupElementsHolder {
    private static final LookupElement[] LOOKUP_ELEMENTS = TemplateToolkitFilterNames.FILTER_NAMES.stream()
      .map(it -> LookupElementBuilder.create(it))
      .toList()
      .toArray(LookupElement.EMPTY_ARRAY);
  }
}
