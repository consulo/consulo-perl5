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
package com.perl5.lang.perl.extensions.moose

import com.perl5.lang.perl.extensions.packageprocessor.PerlExportDescriptor
import com.perl5.lang.perl.extensions.packageprocessor.impl.BaseStrictWarningsProvidingProcessor
import com.perl5.lang.perl.parser.moose.MooseSyntax
import com.perl5.lang.perl.parser.moose.MooseSyntax.*
import com.perl5.lang.perl.psi.impl.PerlUseStatementElement
import com.perl5.lang.perl.util.PerlPackageUtil
import kotlinx.collections.immutable.toImmutableList

internal val MOOSE_ROLE_EXPORTS: List<PerlExportDescriptor> by lazy {
  (listOf(
    PerlExportDescriptor.create(PerlPackageUtil.PACKAGE_CARP, "confess"),
    PerlExportDescriptor.create(PerlPackageUtil.PACKAGE_SCALAR_UTIL, "blessed"),
    PerlExportDescriptor.create(PerlPackageUtil.PACKAGE_CLASS_MOP_MIXIN, MooseSyntax.MOOSE_KEYWORD_META)
  ) + listOf(
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
  ).map { keyword -> PerlExportDescriptor.create(PerlPackageUtil.PACKAGE_MOOSE_ROLE, keyword) })
    .toImmutableList()
}

open class MooseRoleProcessor : BaseStrictWarningsProvidingProcessor() {

  override fun getImports(useStatement: PerlUseStatementElement): List<PerlExportDescriptor> = MOOSE_ROLE_EXPORTS
}
