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

import com.perl5.lang.perl.extensions.packageprocessor.PerlFeaturesProvider;
import com.perl5.lang.perl.extensions.packageprocessor.PerlPackageOptionsProvider;
import com.perl5.lang.perl.extensions.packageprocessor.PerlPragmaProcessorBase;
import com.perl5.lang.perl.internals.PerlFeaturesTable;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class FeatureProcessor extends PerlPragmaProcessorBase implements PerlPackageOptionsProvider, PerlFeaturesProvider {
  private volatile Map<String, String> myOptions;
  private volatile Map<String, String> myOptionsBundles;

  private @NotNull Map<String, String> getMyOptions() {
    Map<String, String> result = myOptions;
    if (result == null) {
      result = PerlFeaturesTable.AVAILABLE_FEATURES;
      myOptions = result;
    }
    return result;
  }

  private @NotNull Map<String, String> getMyOptionsBundles() {
    Map<String, String> result = myOptionsBundles;
    if (result == null) {
      Map<String, String> optionsBundles = new LinkedHashMap<>();
      for (Map.Entry<String, List<String>> entry : PerlFeaturesTable.AVAILABLE_FEATURES_BUNDLES.entrySet()) {
        String version = entry.getKey();
        List<String> features = entry.getValue();
        optionsBundles.put(":" + version, String.join(" ", features));
      }
      result = Collections.unmodifiableMap(optionsBundles);
      myOptionsBundles = result;
    }
    return result;
  }

  @Override
  public @NotNull Map<String, String> getOptions() {
    return getMyOptions();
  }

  @Override
  public @NotNull Map<String, String> getOptionsBundles() {
    return getMyOptionsBundles();
  }

}
