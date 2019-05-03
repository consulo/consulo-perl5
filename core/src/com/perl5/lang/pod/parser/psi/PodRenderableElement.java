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

package com.perl5.lang.pod.parser.psi;

import org.jetbrains.annotations.NotNull;

public interface PodRenderableElement {
  /**
   * Appends HTML representation of the section to the {@code builder}
   */
  void renderElementAsHTML(StringBuilder builder, PodRenderingContext context);


  /**
   * Appends text representation of the section to the {@code builder}
   */
  void renderElementAsText(StringBuilder builder, PodRenderingContext context);

  /**
   * @return html representation of element contents
   */
  @NotNull
  default String getAsHTML() {
    StringBuilder builder = new StringBuilder();
    renderElementAsHTML(builder, new PodRenderingContext());
    return builder.toString();
  }

  /**
   * @return text representation of element contents
   */
  @NotNull
  default String getAsText() {
    StringBuilder builder = new StringBuilder();
    renderElementAsText(builder, new PodRenderingContext());
    return builder.toString();
  }

}
