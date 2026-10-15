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
package com.perl5.lang.perl.extensions.moo;

import com.perl5.lang.perl.extensions.packageprocessor.PerlExportDescriptor;
import com.perl5.lang.perl.extensions.packageprocessor.PerlPackageLoader;
import com.perl5.lang.perl.extensions.packageprocessor.PerlPackageParentsProvider;
import com.perl5.lang.perl.extensions.packageprocessor.impl.BaseStrictWarningsProvidingProcessor;
import com.perl5.lang.perl.psi.impl.PerlUseStatementElement;
import com.perl5.lang.perl.util.PerlPackageUtilCore;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_AFTER;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_AROUND;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_BEFORE;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_EXTENDS;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_HAS;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_WITH;

public final class MooProcessor extends BaseStrictWarningsProvidingProcessor implements PerlPackageParentsProvider, PerlPackageLoader {
  private final @NonNls String mooObject = PerlPackageUtilCore.join(PerlPackageUtilCore.PACKAGE_MOO, "Object");
  private final List<String> loadedClasses = List.of(mooObject);
  private final List<String> parentClasses = loadedClasses;

  private volatile List<PerlExportDescriptor> myExports;

  private @NotNull List<PerlExportDescriptor> getMyExports() {
    List<PerlExportDescriptor> result = myExports;
    if (result == null) {
      List<PerlExportDescriptor> exports = new ArrayList<>();
      for (String keyword : List.of(
        MOOSE_KEYWORD_AFTER,
        MOOSE_KEYWORD_AROUND,
        MOOSE_KEYWORD_BEFORE,
        MOOSE_KEYWORD_EXTENDS,
        MOOSE_KEYWORD_HAS,
        MOOSE_KEYWORD_WITH
      )) {
        exports.add(PerlExportDescriptor.create(PerlPackageUtilCore.PACKAGE_MOO, keyword));
      }
      result = List.copyOf(exports);
      myExports = result;
    }
    return result;
  }

  @Override
  public @NotNull List<String> getLoadedPackageNames(@Nullable PerlUseStatementElement useStatement) {
    return loadedClasses;
  }

  @Override
  public void changeParentsList(@NotNull PerlUseStatementElement useStatement, @NotNull List<? super String> currentList) {
    currentList.clear();
    currentList.addAll(parentClasses);
  }

  @Override
  public boolean hasPackageFilesOptions() {
    return false;
  }

  @Override
  public @NotNull List<PerlExportDescriptor> getImports(@NotNull PerlUseStatementElement useStatement) {
    return getMyExports();
  }
}
