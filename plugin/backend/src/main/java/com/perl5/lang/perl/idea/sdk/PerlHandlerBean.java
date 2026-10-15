/*
 * Copyright 2015-2022 Alexandr Evstigneev
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

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.application.util.AtomicNotNullLazyValue;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * TODO consulo: was {@code KeyedLazyInstanceEP} registered in plugin.xml as {@code com.perl5.hostHandler},
 *  {@code com.perl5.implementationHandler} and {@code com.perl5.versionManagerHandler}. In Consulo each registration is a subclass
 *  marked with {@code @ExtensionImpl}, passing EP name, key and implementation class to the constructor.
 */
@ExtensionAPI(ComponentScope.APPLICATION)
public abstract class PerlHandlerBean {
  private final @NotNull String myEpName;
  private final @NotNull String myKey;
  private final @NotNull Class<? extends AbstractPerlHandler<?, ?>> myImplementationClass;
  private final AtomicNotNullLazyValue<AbstractPerlHandler<?, ?>> myInstance = AtomicNotNullLazyValue.createValue(this::createInstance);

  protected PerlHandlerBean(@NotNull String epName,
                            @NotNull String key,
                            @NotNull Class<? extends AbstractPerlHandler<?, ?>> implementationClass) {
    myEpName = epName;
    myKey = key;
    myImplementationClass = implementationClass;
  }

  public final @NotNull String getEpName() {
    return myEpName;
  }

  public final @NotNull String getKey() {
    return myKey;
  }

  public final @NotNull String getImplementationClassName() {
    return myImplementationClass.getName();
  }

  public final @NotNull AbstractPerlHandler<?, ?> getInstance() {
    return myInstance.getValue();
  }

  @SuppressWarnings("unchecked")
  public @NotNull AbstractPerlHandler<?, ?> createInstance() {
    try {
      var implementationClassName =
        Objects.requireNonNull(getImplementationClassName(), () -> "Class name is not specified for extension: " + getKey());
      Class<AbstractPerlHandler<?, ?>> extensionClass = (Class<AbstractPerlHandler<?, ?>>)myImplementationClass;
      Constructor<AbstractPerlHandler<?, ?>> constructor = extensionClass.getConstructor(PerlHandlerBean.class);
      constructor.setAccessible(true);
      return constructor.newInstance(PerlHandlerBean.this);
    }
    catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
