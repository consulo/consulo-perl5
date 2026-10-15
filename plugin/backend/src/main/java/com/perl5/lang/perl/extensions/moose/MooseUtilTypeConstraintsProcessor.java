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
package com.perl5.lang.perl.extensions.moose;

import com.perl5.lang.perl.extensions.packageprocessor.PerlExportDescriptor;
import com.perl5.lang.perl.extensions.packageprocessor.impl.BaseStrictWarningsProvidingProcessor;
import com.perl5.lang.perl.psi.impl.PerlUseStatementElement;
import com.perl5.lang.perl.util.PerlPackageUtilCore;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class MooseUtilTypeConstraintsProcessor extends BaseStrictWarningsProvidingProcessor {
  private volatile List<PerlExportDescriptor> exports;

  private @NotNull List<PerlExportDescriptor> getExports() {
    List<PerlExportDescriptor> result = exports;
    if (result == null) {
      List<PerlExportDescriptor> descriptors = new ArrayList<>();
      for (String name : List.of(
        "as", "class_type", "coerce", "duck_type", "enum", "find_type_constraint", "from", "inline_as", "match_on_type", "maybe_type",
        "message", "register_type_constraint", "role_type", "subtype", "type", "union", "via", "where"
      )) {
        descriptors.add(PerlExportDescriptor.create(PerlPackageUtilCore.PACKAGE_MOOSE_UTIL_TYPE_CONSTRAINTS, name));
      }
      result = List.copyOf(descriptors);
      exports = result;
    }
    return result;
  }

  @Override
  public @NotNull List<PerlExportDescriptor> getImports(@NotNull PerlUseStatementElement useStatement) {
    return getExports();
  }
}
