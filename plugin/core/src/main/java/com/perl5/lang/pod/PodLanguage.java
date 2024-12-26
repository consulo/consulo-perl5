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

package com.perl5.lang.pod;

import consulo.language.Language;

public class PodLanguage extends Language {
  public static final PodLanguage INSTANCE = new PodLanguage();
  public static final Language[] ARRAY = new Language[]{INSTANCE};
  public static final String NAME = "Perl5 POD";

  public PodLanguage() {
    super(NAME);
  }

  @Override
  public boolean isCaseSensitive() {
    return true;
  }
}
