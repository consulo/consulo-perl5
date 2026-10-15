/*
 * Copyright 2015-2026 Alexandr Evstigneev
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
import com.perl5.lang.perl.internals.warnings.PerlWarningTree;
import com.perl5.lang.perl.internals.warnings.PerlWarningTreeLeaf;
import com.perl5.lang.perl.internals.warnings.PerlWarningTreeNode;
import com.perl5.lang.perl.psi.impl.PerlUseStatementElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class WarningsProcessor extends PerlPragmaProcessorBase implements PerlPackageOptionsProvider {
  private volatile Map<String, String> myOptions;
  private volatile Map<String, String> myOptionsBundles;

  private @NotNull Map<String, String> getMyOptions() {
    Map<String, String> result = myOptions;
    if (result == null) {
      Map<String, String> options = new LinkedHashMap<>();
      options.put("FATAL", "FATALITY!");
      for (Map.Entry<String, PerlWarningTreeLeaf> entry : PerlWarningTree.LEAF_OPTIONS.entrySet()) {
        options.put(entry.getKey(), entry.getValue().getMinVersion().getStrictDottedVersion());
      }
      result = Collections.unmodifiableMap(options);
      myOptions = result;
    }
    return result;
  }

  private @NotNull Map<String, String> getMyOptionsBundles() {
    Map<String, String> result = myOptionsBundles;
    if (result == null) {
      Map<String, String> optionsBundles = new LinkedHashMap<>();
      for (Map.Entry<String, PerlWarningTreeNode> entry : PerlWarningTree.NODE_OPTIONS.entrySet()) {
        String joinedOptions = entry.getValue().collectChildLeafs().stream()
          .map(leaf -> leaf.getStringIdentifier() + "(" + leaf.getMinVersion().getStrictDottedVersion() + ")")
          .collect(Collectors.joining(" "));

        optionsBundles.put(entry.getKey(), entry.getValue().getMinVersion().getStrictDottedVersion() + ", " + joinedOptions);
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

  @Override
  public boolean isWarningsEnabled(@NotNull PerlUseStatementElement useStatement) {
    return true;
  }
}
