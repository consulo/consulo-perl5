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

package com.perl5.lang.perl.psi.stubs;

import com.perl5.lang.perl.psi.PerlSub;
import com.perl5.lang.perl.psi.PerlSubElement;
import com.perl5.lang.perl.psi.utils.PerlSubAnnotations;
import com.perl5.lang.perl.util.PerlPackageUtil;
import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.StubBase;
import consulo.language.psi.stub.StubElement;
import consulo.language.util.IncorrectOperationException;
import org.jetbrains.annotations.Nullable;

public abstract class PerlSubStub<Psi extends PerlSubElement> extends StubBase<Psi> implements StubElement<Psi>, PerlSub {
  private final String myNamespaceName;
  private final String mySubName;
  private final PerlSubAnnotations myAnnotations;

  public PerlSubStub(final StubElement parent,
                     final String namespaceName,
                     final String subName,
                     PerlSubAnnotations annotations,
                     IStubElementType elementType) {
    super(parent, elementType);
    myNamespaceName = namespaceName;
    mySubName = subName;
    myAnnotations = annotations;
  }

  @Override
  public String getNamespaceName() {
    return myNamespaceName;
  }

  @Override
  public String getSubName() {
    return mySubName;
  }

  @Override
  public PerlSubAnnotations getAnnotations() {
    return myAnnotations;
  }

  @Override
  public String getCanonicalName() {
    return getNamespaceName() + PerlPackageUtil.NAMESPACE_SEPARATOR + getSubName();
  }

  @Override
  public @Nullable String getExplicitNamespaceName() {
    return myNamespaceName;
  }

  @Override
  public boolean isMethod() {
    throw new IncorrectOperationException();
  }

  @Override
  public boolean isStatic() {
    throw new IncorrectOperationException();
  }

  @Override
  public boolean isXSub() {
    throw new IncorrectOperationException();
  }

  @Override
  public String toString() {
    return super.toString() + "\n" +
           "\tNamespace name: " + myNamespaceName + "\n" +
           "\tSub name: " + mySubName + "\n" +
           "\tAnnotations: " + myAnnotations;
  }
}
