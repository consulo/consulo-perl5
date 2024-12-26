/*
 * Copyright 2015-2022 Alexandr Evstigneev
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

package com.perl5.lang.perl.psi.stubs.variables;

import com.perl5.lang.perl.psi.PerlVariableDeclarationElement;
import consulo.language.psi.stub.StubIndexKey;
import org.jetbrains.annotations.NotNull;


public class PerlArrayNamespaceStubIndex extends PerlVariableStubIndex {
  public static final StubIndexKey<String, PerlVariableDeclarationElement> KEY_ARRAY_IN_NAMESPACE =
    StubIndexKey.createIndexKey("perl.global.array.in.namespace");

  @Override
  public @NotNull StubIndexKey<String, PerlVariableDeclarationElement> getKey() {
    return KEY_ARRAY_IN_NAMESPACE;
  }

}
