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

package com.perl5.lang.perl.psi;

import consulo.disposer.Disposable;
import consulo.application.ApplicationManager;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import jakarta.inject.Singleton;
import consulo.util.lang.lazy.ClearableLazyValue;
import com.perl5.lang.perl.psi.PerlSubCallHandler.KeyedLazyInstance;
import org.jetbrains.annotations.NotNull;

@ServiceAPI(ComponentScope.APPLICATION)
@ServiceImpl
@Singleton
public final class PerlSubCallHandlerVersionService implements Disposable {
  private final ClearableLazyValue<Integer> myVersionProvider = ClearableLazyValue.atomicNotNull(() -> {
    int version = 0;
    for (KeyedLazyInstance instance : PerlSubCallHandler.getExtensionPoint().getExtensions()) {
      version += instance.getInstance().getVersion();
    }
    return version;
  });

  public PerlSubCallHandlerVersionService() {
    // TODO consulo: Consulo ExtensionPoint has no change listeners (extensions are not changed without restart),
    //  upstream drops the cached version on com.perl5.subCallHandler extensions change here
  }

  public static int getHandlersVersion() {
    return getInstance().myVersionProvider.get();
  }

  @Override
  public void dispose() {
    myVersionProvider.clear();
  }

  private static @NotNull PerlSubCallHandlerVersionService getInstance() {
    return ApplicationManager.getApplication().getService(PerlSubCallHandlerVersionService.class);
  }
}
