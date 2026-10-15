/*
 * Copyright 2015-2024 Alexandr Evstigneev
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

package com.intellij.openapi.projectRoots.impl;

import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import consulo.application.Application;
import consulo.application.WriteAction;
import consulo.disposer.Disposable;
import consulo.logging.Logger;
import consulo.content.bundle.SdkTable;
import consulo.content.bundle.Sdk;
import consulo.content.bundle.SdkTypeId;
import consulo.content.bundle.event.SdkTableListener;
import consulo.disposer.Disposer;
import consulo.util.lang.StringUtil;
import consulo.language.util.IncorrectOperationException;
import com.perl5.lang.perl.idea.sdk.PerlSdkAdditionalData;
import com.perl5.lang.perl.idea.sdk.PerlSdkType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO consulo: upstream keeps its own {@code ProjectJdkTable} implementation with perl interpreters persisted to the
 *  {@code PerlInterpreters} state of {@code perl5.xml}. In Consulo perl interpreters are regular bundles of {@link PerlSdkType}
 *  stored in the platform {@link SdkTable} (so they are available for the Perl module extension), and this class is a facade over it.
 *  Interpreters from the upstream {@code perl5.xml} are not migrated.
 */
@ServiceAPI(ComponentScope.APPLICATION)
@ServiceImpl
@Singleton
public class PerlSdkTable {
  private static final Logger LOG = Logger.getInstance(PerlSdkTable.class);
  /**
   * TODO consulo: platform {@link SdkTableListener} topic is used, events are fired for all bundles, not only perl interpreters
   */
  public static final Class<SdkTableListener> PERL_TABLE_TOPIC = SdkTableListener.class;

  private final @NotNull SdkTable mySdkTable;

  @Inject
  public PerlSdkTable(@NotNull SdkTable sdkTable) {
    mySdkTable = sdkTable;
  }

  public @Nullable Sdk findJdk(@Nullable String name) {
    for (Sdk interpreter : getInterpreters()) {
      if (StringUtil.equals(name, interpreter.getName())) {
        return interpreter;
      }
    }
    return null;
  }

  public @Nullable Sdk findJdk(@NotNull String name, @NotNull String type) {
    return findJdk(name);
  }

  /**
   * @deprecated use com.intellij.openapi.projectRoots.impl.PerlSdkTable#getInterpreters() instead
   */
  @Deprecated
  public Sdk @NotNull [] getAllJdks() {
    LOG.error("Not expected to be invoked");
    throw new AbstractMethodError();
  }

  public @NotNull List<Sdk> getInterpreters() {
    List<Sdk> result = new ArrayList<>();
    for (Sdk sdk : mySdkTable.getAllSdks()) {
      if (sdk.getSdkType() instanceof PerlSdkType) {
        result.add(sdk);
      }
    }
    return result;
  }

  public @NotNull List<Sdk> getSdksOfType(@NotNull SdkTypeId type) {
    throw new IncorrectOperationException();
  }

  public void addJdk(@NotNull Sdk jdk) {
    if( PerlSdkAdditionalData.from(jdk) == null){
      LOG.error("Attempt to add corrupted sdk: " + jdk);
    }
    else{
      // platform table fires SdkTableListener#sdkAdded
      runWriteAction(() -> mySdkTable.addSdk(jdk));
    }
  }

  public void removeJdk(@NotNull Sdk jdk) {
    // platform table fires SdkTableListener#sdkRemoved
    runWriteAction(() -> mySdkTable.removeSdk(jdk));
    if (jdk instanceof Disposable disposable) {
      Disposer.dispose(disposable);
    }
  }

  public void updateJdk(@NotNull Sdk originalJdk, @NotNull Sdk modifiedJdk) {
    // platform table copies the modified sdk into the original one and fires SdkTableListener#sdkNameChanged
    runWriteAction(() -> mySdkTable.updateSdk(originalJdk, modifiedJdk));
  }

  public @NotNull SdkTypeId getDefaultSdkType() {
    return PerlSdkType.getInstance();
  }

  public @NotNull SdkTypeId getSdkTypeByName(@NotNull String name) {
    return PerlSdkType.getInstance();
  }

  @SuppressWarnings("deprecation")
  public @NotNull Sdk createSdk(@NotNull String name, @NotNull SdkTypeId sdkType) {
    return mySdkTable.createSdk(name, PerlSdkType.getInstance());
  }

  public @NotNull Sdk createSdk(@NotNull String name) {
    return createSdk(name, PerlSdkType.getInstance());
  }

  public static PerlSdkTable getInstance() {
    return Application.get().getInstance(PerlSdkTable.class);
  }

  public void saveOnDisk() {
    throw new RuntimeException("Not supposed to be used for the perl plugin, not implemented");
  }

  private static void runWriteAction(@NotNull Runnable runnable) {
    Application application = Application.get();
    if (application.isWriteAccessAllowed()) {
      runnable.run();
    }
    else {
      application.invokeAndWait(() -> WriteAction.run(runnable::run));
    }
  }
}
