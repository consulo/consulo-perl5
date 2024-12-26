/*
 * Copyright 2015-2021 Alexandr Evstigneev
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

package com.perl5.lang.pod.lexer;

import com.perl5.lang.pod.elementTypes.PodFileElementType;
import com.perl5.lang.pod.elementTypes.PodOuterTokenType;
import com.perl5.lang.pod.parser.PodElementTypesGenerated;
import consulo.language.ast.IElementType;
import consulo.language.ast.IFileElementType;

public interface PodElementTypes extends PodElementTypesGenerated {
  IElementType POD_OUTER = new PodOuterTokenType();
  IFileElementType FILE = new PodFileElementType("Plain old document");
}
