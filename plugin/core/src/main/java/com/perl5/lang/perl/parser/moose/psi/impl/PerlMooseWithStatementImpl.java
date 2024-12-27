/*
 * Copyright 2015-2023 Alexandr Evstigneev
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

package com.perl5.lang.perl.parser.moose.psi.impl;

import com.perl5.lang.perl.parser.moose.psi.PerlMooseWithStatement;
import consulo.language.ast.ASTNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class PerlMooseWithStatementImpl extends PerlMooseExtendsStatementImpl implements PerlMooseWithStatement {
  public PerlMooseWithStatementImpl(ASTNode node) {
    super(node);
  }

  @Override
  public void changeParentsList(@NotNull List<? super String> currentList) {
    currentList.addAll(getParentsList());
  }
}
