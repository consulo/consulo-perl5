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

package com.perl5.lang.perl.extensions.mojo;

import com.perl5.lang.perl.extensions.packageprocessor.*;
import com.perl5.lang.perl.extensions.packageprocessor.impl.BaseStrictWarningsProvidingProcessor;
import com.perl5.lang.perl.psi.impl.PerlUseStatementElement;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MojoBaseProcessor extends BaseStrictWarningsProvidingProcessor
  implements PerlUtfProvider, PerlFeaturesProvider, PerlPackageOptionsProvider, PerlPackageParentsProvider, PerlPackageLoader {
  private static final String MOJO_BASE = "Mojo::Base";
  private static final String IO_HANDLE = "IO::Handle";

  private Map<String, String> myOptions;

  private synchronized @NotNull Map<String, String> getMyOptions() {
    if (myOptions == null) {
      Map<String, String> options = new LinkedHashMap<>();
      options.put("-strict", "strict,warnings,utf8,v5.10,IO::Handle");
      options.put("-base", "strict,warnings,utf8,v5.10,IO::Handle,acts as parent");
      myOptions = Collections.unmodifiableMap(options);
    }
    return myOptions;
  }

  @Override
  public @NotNull List<String> getLoadedPackageNames(PerlUseStatementElement useStatement) {
    List<String> loadedPackages = new ArrayList<>();
    loadedPackages.add(IO_HANDLE);
    List<String> allOptions = useStatement.getImportParameters();
    if (allOptions == null) {
      return loadedPackages;
    }

    allOptions.removeAll(getOptions().keySet());

    if (!allOptions.isEmpty() && !MOJO_BASE.equals(allOptions.get(0))) {
      loadedPackages.add(allOptions.get(0));
    }

    return loadedPackages;
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
  public void changeParentsList(@NotNull PerlUseStatementElement useStatement, @NotNull List<? super String> currentList) {
    List<String> allOptions = useStatement.getImportParameters();
    if (allOptions == null) {
      return;
    }

    if (allOptions.contains("-base")) {
      currentList.add(MOJO_BASE);
    }
    else {
      allOptions.removeAll(getOptions().keySet());
      if (!allOptions.isEmpty()) {
        currentList.add(allOptions.get(0));
      }
    }
  }

  @Override
  public boolean hasPackageFilesOptions() {
    return true;
  }
}
