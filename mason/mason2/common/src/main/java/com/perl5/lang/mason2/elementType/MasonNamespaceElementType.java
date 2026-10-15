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

package com.perl5.lang.mason2.elementType;

import com.perl5.lang.htmlmason.MasonCoreUtil;
import com.perl5.lang.mason2.Mason2Language;
import com.perl5.lang.mason2.Mason2UtilCore;
import com.perl5.lang.mason2.psi.MasonNamespaceDefinition;
import com.perl5.lang.mason2.psi.impl.MasonNamespaceDefinitionImpl;
import com.perl5.lang.mason2.psi.stubs.MasonNamespaceDefitnitionsStubIndex;
import com.perl5.lang.mason2.psi.stubs.MasonParentNamespacesStubIndex;
import com.perl5.lang.perl.parser.elementTypes.PsiElementProvider;
import com.perl5.lang.perl.psi.PerlNamespaceDefinitionElement;
import com.perl5.lang.perl.psi.stubs.namespaces.PerlNamespaceDefinitionData;
import com.perl5.lang.perl.psi.stubs.namespaces.PerlNamespaceDefinitionElementType;
import com.perl5.lang.perl.psi.stubs.namespaces.PerlNamespaceDefinitionStub;
import com.perl5.lang.perl.util.PerlFileUtil;
import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.stub.IndexSink;
import consulo.language.psi.stub.StubElement;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.perl5.lang.perl.psi.stubs.namespaces.PerlNamespaceIndex.NAMESPACE_KEY;

// consulo: stubs are handled by the element type (upstream: MasonNamespaceStubSerializingFactory registered in
//  Mason2StubRegistryExtension)
public final class MasonNamespaceElementType extends PerlNamespaceDefinitionElementType implements PsiElementProvider {
  public MasonNamespaceElementType(@NotNull String name) {
    super(name, Mason2Language.INSTANCE);
  }

  @Override
  public @NotNull PsiElement getPsiElement(@NotNull ASTNode node) {
    return new MasonNamespaceDefinitionImpl(node);
  }

  @Override
  public PerlNamespaceDefinitionElement createPsi(@NotNull PerlNamespaceDefinitionStub stub) {
    return new MasonNamespaceDefinitionImpl(stub, this);
  }

  @SuppressWarnings("rawtypes")
  @Override
  public @NotNull PerlNamespaceDefinitionStub createStub(@NotNull PerlNamespaceDefinitionElement psi, @Nullable StubElement parentStub) {
    return new PerlNamespaceDefinitionStub(
      parentStub, this, new PerlNamespaceDefinitionData(
        StringUtil.notNullize(getAbsoluteComponentPath((MasonNamespaceDefinition)psi)), psi
      )
    );
  }

  @Override
  public void indexStub(@NotNull PerlNamespaceDefinitionStub stub, @NotNull IndexSink sink) {
    String name = stub.getNamespaceName();
    sink.occurrence(MasonNamespaceDefitnitionsStubIndex.KEY, name);

    // fixme this is kinda hack to make MRO work. But, it should be smarter
    sink.occurrence(NAMESPACE_KEY, Mason2UtilCore.getClassnameFromPath(name));

    for (String parent : stub.getParentNamespacesNames()) {
      if (parent != null && !parent.isEmpty()) {
        sink.occurrence(MasonParentNamespacesStubIndex.KEY, parent);
      }
    }
  }

  @Override
  public boolean shouldCreateStub(@NotNull ASTNode node) {
    PsiElement psi = node.getPsi();
    return psi instanceof MasonNamespaceDefinition namespaceDefinition &&
           namespaceDefinition.isValid() &&
           StringUtil.isNotEmpty(getAbsoluteComponentPath(namespaceDefinition));
  }

  /**
   * Returns file path relative to project root
   *
   * @return path, relative to root, null if it's LightVirtualFile without original
   */
  public static @Nullable String getAbsoluteComponentPath(@NotNull MasonNamespaceDefinition namespaceDefinition) {
    VirtualFile containingFile = MasonCoreUtil.getContainingVirtualFile(namespaceDefinition.getContainingFile());
    if (containingFile != null) {
      return PerlFileUtil.getPathRelativeToContentRoot(containingFile, namespaceDefinition.getProject());
    }
    return null;
  }
}
