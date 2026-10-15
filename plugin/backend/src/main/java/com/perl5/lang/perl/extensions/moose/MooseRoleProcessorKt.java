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
import com.perl5.lang.perl.util.PerlPackageUtilCore;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_AFTER;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_AROUND;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_AUGMENT;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_BEFORE;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_EXCLUDES;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_EXTENDS;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_HAS;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_INNER;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_META;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_OVERRIDE;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_REQUIRES;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_SUPER;
import static com.perl5.lang.perl.parser.moose.MooseSyntax.MOOSE_KEYWORD_WITH;

/**
 * Top-level declarations of upstream MooseRoleProcessor.kt (Kotlin compiles them into this class).
 */
public final class MooseRoleProcessorKt {
  private static volatile List<PerlExportDescriptor> MOOSE_ROLE_EXPORTS;

  private MooseRoleProcessorKt() {
  }

  public static @NotNull List<PerlExportDescriptor> getMOOSE_ROLE_EXPORTS() {
    List<PerlExportDescriptor> result = MOOSE_ROLE_EXPORTS;
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
        MOOSE_KEYWORD_EXCLUDES,
        MOOSE_KEYWORD_EXTENDS,
        MOOSE_KEYWORD_HAS,
        MOOSE_KEYWORD_INNER,
        MOOSE_KEYWORD_OVERRIDE,
        MOOSE_KEYWORD_REQUIRES,
        MOOSE_KEYWORD_SUPER,
        MOOSE_KEYWORD_WITH
      )) {
        exports.add(PerlExportDescriptor.create(PerlPackageUtilCore.PACKAGE_MOOSE_ROLE, keyword));
      }
      result = List.copyOf(exports);
      MOOSE_ROLE_EXPORTS = result;
    }
    return result;
  }
}
