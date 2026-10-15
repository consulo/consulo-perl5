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

package com.perl5.lang.perl.extensions.packageprocessor.impl;

import com.perl5.lang.perl.extensions.packageprocessor.PerlPackageOptionsProvider;
import com.perl5.lang.perl.extensions.packageprocessor.PerlPragmaProcessorBase;
import com.perl5.lang.perl.psi.impl.PerlUseStatementElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class StrictProcessor extends PerlPragmaProcessorBase implements PerlPackageOptionsProvider {
  private volatile Map<String, String> myOptions;

  private @NotNull Map<String, String> getMyOptions() {
    Map<String, String> result = myOptions;
    if (result == null) {
      Map<String, String> options = new LinkedHashMap<>();
      options.put("vars", "generates a compile-time error if you access a variable that was neither explicitly declared");
      options.put("refs", "generates a runtime error if you use symbolic references");
      options.put("subs", "generates a compile-time error if you try to use a bareword identifier that's not a subroutine");
      result = Collections.unmodifiableMap(options);
      myOptions = result;
    }
    return result;
  }

  @Override
  public @NotNull Map<String, String> getOptions() {
    return getMyOptions();
  }

  @Override
  public @NotNull Map<String, String> getOptionsBundles() {
    return Collections.emptyMap();
  }

  @Override
  public boolean isStrictEnabled(@NotNull PerlUseStatementElement useStatement) {
    return true;
  }
}
