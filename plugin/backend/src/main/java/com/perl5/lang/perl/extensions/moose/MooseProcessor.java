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
package com.perl5.lang.perl.extensions.moose;

import com.perl5.lang.perl.extensions.packageprocessor.PerlExportDescriptor;
import com.perl5.lang.perl.extensions.packageprocessor.PerlPackageLoader;
import com.perl5.lang.perl.extensions.packageprocessor.PerlPackageParentsProvider;
import com.perl5.lang.perl.extensions.packageprocessor.impl.BaseStrictWarningsProvidingProcessor;
import com.perl5.lang.perl.psi.impl.PerlUseStatementElement;
import com.perl5.lang.perl.util.PerlPackageUtilCore;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_AFTER;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_AROUND;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_AUGMENT;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_BEFORE;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_EXTENDS;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_HAS;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_INNER;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_META;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_OVERRIDE;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_SUPER;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_WITH;

public class MooseProcessor extends BaseStrictWarningsProvidingProcessor implements PerlPackageParentsProvider, PerlPackageLoader {
  private volatile List<String> myLoadedClasses;

  private final List<String> myParentClasses = List.of(PerlPackageUtilCore.PACKAGE_MOOSE_OBJECT);

  private volatile List<PerlExportDescriptor> myExports;

  private @NotNull List<String> getMyLoadedClasses() {
    List<String> result = myLoadedClasses;
    if (result == null) {
      result = List.of(PerlPackageUtilCore.PACKAGE_MOOSE_OBJECT, PerlPackageUtilCore.PACKAGE_CARP, PerlPackageUtilCore.PACKAGE_SCALAR_UTIL);
      myLoadedClasses = result;
    }
    return result;
  }

  private @NotNull List<PerlExportDescriptor> getMyExports() {
    List<PerlExportDescriptor> result = myExports;
    if (result == null) {
      List<PerlExportDescriptor> exports = new ArrayList<>(List.of(
        PerlExportDescriptor.create(PerlPackageUtilCore.PACKAGE_CARP, "confess"),
        PerlExportDescriptor.create(PerlPackageUtilCore.PACKAGE_SCALAR_UTIL, "blessed"),
        PerlExportDescriptor.create(PerlPackageUtilCore.PACKAGE_CLASS_MOP_MIXIN, MOOSE_KEYWORD_META)
      ));
      for (String keyword : List.of(
        MOOSE_KEYWORD_AFTER,
        MOOSE_KEYWORD_AROUND,
        MOOSE_KEYWORD_AUGMENT,
        MOOSE_KEYWORD_BEFORE,
        MOOSE_KEYWORD_EXTENDS,
        MOOSE_KEYWORD_HAS,
        MOOSE_KEYWORD_OVERRIDE,
        MOOSE_KEYWORD_WITH,
        MOOSE_KEYWORD_INNER,
        MOOSE_KEYWORD_SUPER
      )) {
        exports.add(PerlExportDescriptor.create(PerlPackageUtilCore.PACKAGE_MOOSE, keyword));
      }
      result = List.copyOf(exports);
      myExports = result;
    }
    return result;
  }

  @Override
  public @NotNull List<String> getLoadedPackageNames(@NotNull PerlUseStatementElement useStatement) {
    return getMyLoadedClasses();
  }

  @Override
  public void changeParentsList(@NotNull PerlUseStatementElement useStatement, @NotNull List<? super String> currentList) {
    currentList.clear();
    currentList.addAll(myParentClasses);
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
