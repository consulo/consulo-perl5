/*
 * Copyright 2015-2020 Alexandr Evstigneev
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

package com.perl5.lang.perl.psi.stubs.calls;

import com.perl5.lang.perl.psi.impl.PerlSubCallElement;
import com.perl5.lang.perl.psi.stubs.PerlPolyNamedElementStub;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.StubElement;
import consulo.util.lang.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PerlSubCallElementStub extends PerlPolyNamedElementStub<PerlSubCallElement> {
  private final @NotNull String mySubName;
  private final @NotNull PerlSubCallElementData myCallData;

  public PerlSubCallElementStub(StubElement<?> parent,
                                IStubElementType<?, ?> elementType,
                                @NotNull List<StubElement<?>> lightNamedElementsStubs,
                                @NotNull String subName,
                                @NotNull PerlSubCallElementData callData) {
    super(parent, elementType, lightNamedElementsStubs);
    mySubName = subName;
    myCallData = callData;
  }

  public @NotNull String getSubName() {
    return mySubName;
  }

  public @NotNull PerlSubCallElementData getCallData() {
    return myCallData;
  }

  @Override
  public String toString() {
    List<String> chunks = new ArrayList<>();
    chunks.add(super.toString());
    chunks.add("\tHandler sub name: " + mySubName);
    chunks.add("\tCall data: " + myCallData);
    return StringUtil.join(chunks, "\n");
  }
}
