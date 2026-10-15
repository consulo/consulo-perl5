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
package com.perl5.lang.perl.extensions.packageprocessor.impl;

import com.perl5.lang.perl.extensions.packageprocessor.PerlExportDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class Dancer2PackageProcessor extends DancerPackageProcessor {
  private volatile List<PerlExportDescriptor> myExportDescriptors;

  @Override
  protected @NotNull List<PerlExportDescriptor> getMyExportDescriptors() {
    List<PerlExportDescriptor> result = myExportDescriptors;
    if (result == null) {
      result = PerlDancer2DSL.DSL_KEYWORDS.stream().map(it -> PerlExportDescriptor.create("Dancer2::Core::DSL", it)).toList();
      myExportDescriptors = result;
    }
    return result;
  }
}
