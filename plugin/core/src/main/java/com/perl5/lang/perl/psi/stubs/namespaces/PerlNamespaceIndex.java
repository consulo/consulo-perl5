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

package com.perl5.lang.perl.psi.stubs.namespaces;

import com.perl5.lang.perl.psi.PerlNamespaceDefinitionElement;
import com.perl5.lang.perl.psi.stubs.PerlStubIndexBase;
import consulo.language.psi.stub.StubIndexExtension;
import consulo.language.psi.stub.StubIndexKey;
import org.jetbrains.annotations.NotNull;


/**
 * Index for namespace_name => namespace
 *
 * @see PerlLightNamespaceIndex
 */
public class PerlNamespaceIndex extends PerlStubIndexBase<PerlNamespaceDefinitionElement> {
  public static final int VERSION = 1;
  public static final StubIndexKey<String, PerlNamespaceDefinitionElement>
    NAMESPACE_KEY = StubIndexKey.createIndexKey("perl.package.definition");

  @Override
  public int getVersion() {
    return super.getVersion() + VERSION;
  }

  @Override
  public @NotNull StubIndexKey<String, PerlNamespaceDefinitionElement> getKey() {
    return NAMESPACE_KEY;
  }

  @Override
  protected @NotNull Class<PerlNamespaceDefinitionElement> getPsiClass() {
    return PerlNamespaceDefinitionElement.class;
  }

  public static @NotNull PerlNamespaceIndex getInstance() {
    return StubIndexExtension.EP_NAME.findExtensionOrFail(PerlNamespaceIndex.class);
  }
}
