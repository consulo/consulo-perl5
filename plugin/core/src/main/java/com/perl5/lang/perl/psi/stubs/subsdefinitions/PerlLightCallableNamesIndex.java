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

package com.perl5.lang.perl.psi.stubs.subsdefinitions;

import com.intellij.psi.stubs.StubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.perl5.lang.perl.psi.PerlCallableElement;
import com.perl5.lang.perl.psi.impl.PerlPolyNamedElement;
import com.perl5.lang.perl.psi.stubs.PerlLightElementsIndex;
import org.jetbrains.annotations.NotNull;

public class PerlLightCallableNamesIndex extends PerlLightElementsIndex<PerlCallableElement> {
  public static final int VERSION = 0;
  @SuppressWarnings("rawtypes")
  public static final StubIndexKey<String, PerlPolyNamedElement> KEY = StubIndexKey.createIndexKey("perl.callable.names.light");

  @Override
  public int getVersion() {
    return super.getVersion() + VERSION;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull StubIndexKey<String, PerlPolyNamedElement> getKey() {
    return KEY;
  }

  @Override
  protected Class<PerlCallableElement> getLightPsiClass() {
    return PerlCallableElement.class;
  }

  @Override
  protected boolean matchesKey(@NotNull String key, @NotNull PerlCallableElement element) {
    return key.equals(element.getCallableName());
  }

  public static @NotNull PerlLightCallableNamesIndex getInstance() {
    return StubIndexExtension.EP_NAME.findExtensionOrFail(PerlLightCallableNamesIndex.class);
  }
}
