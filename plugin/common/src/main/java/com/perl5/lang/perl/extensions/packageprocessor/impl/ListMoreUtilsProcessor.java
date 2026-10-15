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

import com.perl5.lang.perl.extensions.packageprocessor.PerlExportDescriptor;
import com.perl5.lang.perl.extensions.packageprocessor.PerlPackageProcessorBase;
import com.perl5.lang.perl.psi.impl.PerlUseStatementElement;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public final class ListMoreUtilsProcessor extends PerlPackageProcessorBase {
  private volatile Map<String, PerlExportDescriptor> exportOkDescriptors;

  private @NotNull Map<String, PerlExportDescriptor> getExportOkDescriptors() {
    Map<String, PerlExportDescriptor> result = exportOkDescriptors;
    if (result == null) {
      result = new LinkedHashMap<>();
      for (String it : ListMoreUtilsExports.EXPORT_OK) {
        result.put(it, PerlExportDescriptor.create("List::MoreUtils::PP", it));
      }
      exportOkDescriptors = result;
    }
    return result;
  }

  @Override
  public @NotNull List<PerlExportDescriptor> getImports(@NotNull PerlUseStatementElement useStatement) {
    List<String> importParameters = useStatement.getImportParameters();
    if (importParameters == null) {
      return Collections.emptyList();
    }
    Map<String, PerlExportDescriptor> exportOkDescriptors = getExportOkDescriptors();
    return importParameters.stream()
      .distinct()
      .map(parameter -> exportOkDescriptors.get(parameter))
      .filter(Objects::nonNull)
      .toList();
  }

  @Override
  public void addExports(@NotNull PerlUseStatementElement useStatement,
                         @NotNull Set<? super String> export,
                         @NotNull Set<? super String> exportOk) {
    super.addExports(useStatement, export, exportOk);
    exportOk.addAll(ListMoreUtilsExports.EXPORT_OK);
  }
}
