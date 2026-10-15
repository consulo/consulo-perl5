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
import com.perl5.lang.perl.extensions.packageprocessor.PerlPackageProcessorBase;
import com.perl5.lang.perl.psi.impl.PerlUseStatementElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TestMoreProcessor extends PerlPackageProcessorBase implements PerlPackageOptionsProvider {
  private volatile Map<String, String> myOptions;

  private @NotNull Map<String, String> getMyOptions() {
    Map<String, String> result = myOptions;
    if (result == null) {
      Map<String, String> options = new LinkedHashMap<>();
      options.put("no_plan", "");
      options.put("skip_all", "Skip all tests");
      options.put("tests", "Number of tests expected");
      result = Collections.unmodifiableMap(options);
      myOptions = result;
    }
    return result;
  }

  @Override
  protected @Nullable List<String> getImportParameters(@NotNull PerlUseStatementElement useStatement) {
    return null; // this will cause descriptors to ignore parameters above
  }

  @Override
  public @NotNull Map<String, String> getOptions() {
    return getMyOptions();
  }

  @Override
  public @NotNull Map<String, String> getOptionsBundles() {
    return Collections.emptyMap();
  }
}
