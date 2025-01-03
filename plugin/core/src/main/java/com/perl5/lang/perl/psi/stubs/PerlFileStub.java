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

import com.perl5.lang.perl.psi.PerlFile;
import com.perl5.lang.perl.psi.PerlNamespaceDefinition;
import com.perl5.lang.perl.psi.mro.PerlMroType;
import com.perl5.lang.perl.psi.stubs.namespaces.PerlNamespaceDefinitionData;
import com.perl5.lang.perl.psi.utils.PerlNamespaceAnnotations;
import consulo.language.psi.stub.IStubFileElementType;
import consulo.language.psi.stub.PsiFileStubImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class PerlFileStub extends PsiFileStubImpl<PerlFile> implements PerlNamespaceDefinition {
  private final PerlNamespaceDefinitionData myData;
  private final PerlFileElementType myElementType;

  public PerlFileStub(@NotNull PerlFile file, @NotNull PerlFileElementType elementType) {
    super(file);
    myElementType = elementType;
    myData = new PerlNamespaceDefinitionData(file);
  }

  public PerlFileStub(@NotNull PerlNamespaceDefinitionData data, @NotNull PerlFileElementType elementType) {
    super(null);
    myData = data;
    myElementType = elementType;
  }

  /**
   * @return true iff this stub is empty and contains no useful data
   * @see PerlNamespaceDefinitionData#isEmpty()
   */
  public boolean isEmpty() {
    return myData.isEmpty();
  }

  @Override
  public final @NotNull IStubFileElementType<?> getType() {
    return myElementType;
  }

  public PerlNamespaceDefinitionData getData() {
    return myData;
  }

  @Override
  public @NotNull String getNamespaceName() {
    return myData.getNamespaceName();
  }

  @Override
  public @NotNull PerlMroType getMroType() {
    return myData.getMroType();
  }

  @Override
  public @NotNull List<String> getParentNamespacesNames() {
    return myData.getParentNamespacesNames();
  }

  @Override
  public @Nullable PerlNamespaceAnnotations getAnnotations() {
    return myData.getAnnotations();
  }

  @Override
  public @NotNull List<String> getEXPORT() {
    return myData.getEXPORT();
  }

  @Override
  public @NotNull List<String> getEXPORT_OK() {
    return myData.getEXPORT_OK();
  }

  @Override
  public @NotNull Map<String, List<String>> getEXPORT_TAGS() {
    return myData.getEXPORT_TAGS();
  }

  @Override
  public String toString() {
    return super.toString() + "\n" +
           myData + "\n" +
           "\tType: " + myElementType;
  }
}
