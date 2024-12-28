/*
 * Copyright 2015-2019 Alexandr Evstigneev
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

package com.perl5.lang.pod.parser.psi.mixin;

import com.perl5.lang.pod.parser.psi.PodRenderingContext;
import com.perl5.lang.pod.parser.psi.stubs.PodSectionStub;
import consulo.language.ast.ASTNode;
import consulo.language.psi.stub.IStubElementType;
import org.jetbrains.annotations.NotNull;

import static com.perl5.lang.pod.parser.psi.util.PodRenderUtil.PARAGRAPH_PREFIX;
import static com.perl5.lang.pod.parser.psi.util.PodRenderUtil.PARAGRAPH_SUFFIX;

public abstract class PodUnknownSectionMixIn extends PodTitledSectionMixin {
  public PodUnknownSectionMixIn(@NotNull ASTNode node) {
    super(node);
  }

  public PodUnknownSectionMixIn(@NotNull PodSectionStub stub,
                                @NotNull IStubElementType nodeType) {
    super(stub, nodeType);
  }

  @Override
  public void renderElementTitleAsHTML(StringBuilder builder, PodRenderingContext context) {
    builder.append(PARAGRAPH_PREFIX);
    super.renderElementTitleAsHTML(builder, context);
    builder.append(PARAGRAPH_SUFFIX);
  }
}
