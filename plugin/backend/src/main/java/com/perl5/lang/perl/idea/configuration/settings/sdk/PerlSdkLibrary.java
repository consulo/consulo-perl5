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

package com.perl5.lang.perl.idea.configuration.settings.sdk;

import consulo.navigation.ItemPresentation;
import consulo.content.bundle.Sdk;
import consulo.content.bundle.SdkType;
import consulo.ui.image.Image;
import consulo.virtualFileSystem.VirtualFile;
import com.perl5.lang.perl.idea.project.PerlProjectManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * TODO consulo: upstream extends IntelliJ {@code SyntheticLibrary} (missing in Consulo); it's a plain holder of library roots now.
 *  Sdk is null for the library of the external roots only (upstream {@code SyntheticLibrary.newImmutableLibrary})
 */
public class PerlSdkLibrary implements ItemPresentation {
  private final List<VirtualFile> myRoots;
  private final @Nullable Sdk mySdk;

  public PerlSdkLibrary(@Nullable Sdk sdk, @NotNull List<VirtualFile> roots) {
    mySdk = sdk;
    myRoots = roots;
  }

  @Override
  public @Nullable String getPresentableText() {
    return mySdk == null ? null : mySdk.getName();
  }

  @Override
  public @Nullable String getLocationString() {
    return PerlProjectManager.getInterpreterPath(mySdk);
  }

  @Override
  public @Nullable Image getIcon(boolean unused) {
    return mySdk == null ? null : ((SdkType)mySdk.getSdkType()).getIcon();
  }

  public @NotNull Collection<VirtualFile> getSourceRoots() {
    return myRoots;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PerlSdkLibrary library)) {
      return false;
    }

    if (!myRoots.equals(library.myRoots)) {
      return false;
    }
    return Objects.equals(mySdk, library.mySdk);
  }

  @Override
  public int hashCode() {
    int result = myRoots.hashCode();
    result = 31 * result + Objects.hashCode(mySdk);
    return result;
  }
}
