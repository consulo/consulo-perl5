/*
 * Copyright 2015-2026 Alexandr Evstigneev
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

package com.perl5.lang.perl.idea.sdk;

import consulo.application.Application;
import consulo.application.util.AtomicNotNullLazyValue;
import consulo.util.collection.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

// TODO consulo: KeyedExtensionCollector is missing in Consulo; handlers are collected from PerlHandlerBean extensions with matching EP name
public class PerlHandlerCollector<Handler extends AbstractPerlHandler<?, ?>> {
  private final @NotNull String myEpName;
  @SuppressWarnings("unchecked")
  private final AtomicNotNullLazyValue<@Unmodifiable List<Handler>> myHandlers = AtomicNotNullLazyValue.createValue(
    () -> ContainerUtil.map(getExtensions(), it -> (Handler)it.getInstance()));

  public PerlHandlerCollector(@NotNull String epName) {
    myEpName = epName;
  }

  private @NotNull List<PerlHandlerBean> getExtensions() {
    return Application.get().getExtensionPoint(PerlHandlerBean.class).collectFiltered(it -> myEpName.equals(it.getEpName()));
  }

  public List<Handler> getExtensionsList() {
    return myHandlers.getValue();
  }

  @SuppressWarnings("unchecked")
  public @Nullable Handler findSingle(@Nullable String key) {
    if (key == null) {
      return null;
    }
    PerlHandlerBean bean = getExtensions().stream().filter(it -> key.equals(it.getKey())).findFirst().orElse(null);
    return bean == null ? null : (Handler)bean.getInstance();
  }
}
