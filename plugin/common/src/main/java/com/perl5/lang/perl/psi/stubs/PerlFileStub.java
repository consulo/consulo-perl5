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

package com.perl5.lang.perl.psi.stubs;

import consulo.language.psi.stub.PsiFileStubImpl;
import com.perl5.lang.perl.psi.PerlFile;
import com.perl5.lang.perl.psi.PerlNamespaceDefinition;
import com.perl5.lang.perl.psi.mro.PerlMroType;
import com.perl5.lang.perl.psi.stubs.namespaces.PerlNamespaceDefinitionData;
import com.perl5.lang.perl.psi.utils.PerlNamespaceAnnotations;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class PerlFileStub extends PsiFileStubImpl<PerlFile> implements PerlNamespaceDefinition {
  private final @NotNull PerlNamespaceDefinitionData data;
  private final @NotNull PerlFileElementType myElementType;

  public PerlFileStub(@NotNull PerlFile file, @NotNull PerlFileElementType elementType) {
    super(file);
    myElementType = elementType;
    this.data = new PerlNamespaceDefinitionData(file);
  }

  public PerlFileStub(@NotNull PerlNamespaceDefinitionData data, @NotNull PerlFileElementType elementType) {
    super(null);
    this.data = data;
    myElementType = elementType;
  }

  public @NotNull PerlNamespaceDefinitionData getData() {
    return data;
  }

  /**
   * @return true iff this stub is empty and contains no useful data
   * @see com.perl5.lang.perl.psi.stubs.namespaces.PerlNamespaceDefinitionData.isEmpty
   */
  public boolean isEmpty() {
    return data.isEmpty();
  }

  // consulo: Consulo PsiFileStub has getType() (the file stub serializer) instead of upstream getFileElementType()
  @Override
  public @NotNull PerlFileElementType getType() {
    return myElementType;
  }

  @Override
  public @NotNull String getNamespaceName() {
    return data.getNamespaceName();
  }

  @Override
  public @NotNull PerlMroType getMroType() {
    return data.getMroType();
  }

  @Override
  public @NotNull List<String> getParentNamespacesNames() {
    return data.getParentNamespacesNames();
  }

  @Override
  public @Nullable PerlNamespaceAnnotations getAnnotations() {
    return data.getAnnotations();
  }

  @Override
  public @NotNull List<String> getEXPORT() {
    return data.getEXPORT();
  }

  @Override
  public @NotNull List<String> getEXPORT_OK() {
    return data.getEXPORT_OK();
  }

  @Override
  public @NotNull Map<String, List<String>> getEXPORT_TAGS() {
    return data.getEXPORT_TAGS();
  }

  @Override
  public String toString() {
    return super.toString() + "\n" +
           this.data + "\n" +
           "\tType: " + myElementType;
  }
}
