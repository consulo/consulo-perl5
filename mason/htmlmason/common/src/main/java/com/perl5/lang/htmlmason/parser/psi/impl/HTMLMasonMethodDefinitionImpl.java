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

package com.perl5.lang.htmlmason.parser.psi.impl;

import consulo.language.ast.ASTNode;
import consulo.language.ast.IElementType;
import com.perl5.lang.htmlmason.HTMLMasonIcons;
import com.perl5.lang.htmlmason.parser.psi.HTMLMasonMethodDefinition;
import com.perl5.lang.htmlmason.parser.stubs.HTMLMasonMethodDefinitionStub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.ui.image.Image;


public class HTMLMasonMethodDefinitionImpl extends HTMLMasonStubBasedNamedElementImpl<HTMLMasonMethodDefinitionStub>
  implements HTMLMasonMethodDefinition {
  public HTMLMasonMethodDefinitionImpl(@NotNull ASTNode node) {
    super(node);
  }

  public HTMLMasonMethodDefinitionImpl(@NotNull HTMLMasonMethodDefinitionStub stub, @NotNull IElementType nodeType) {
    super(stub, nodeType);
  }

  @Override
  protected @Nullable String getNameFromStub() {
    HTMLMasonMethodDefinitionStub stub = getGreenStub();
    return stub == null ? null : stub.getName();
  }

  // TODO consulo: PsiElement#getIcon(int) does not exist in Consulo (element icons come from IconDescriptorUpdater),
  //  so this is no longer an override and is not called by the platform
  public @Nullable Image getIcon(int flags) {
    return HTMLMasonIcons.HTML_MASON_METHOD_ICON;
  }
}
