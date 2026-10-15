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

import consulo.content.bundle.Sdk;
import consulo.content.bundle.SdkAdditionalData;
import consulo.content.bundle.SdkModificator;
import consulo.logging.Logger;
import consulo.util.lang.ObjectUtil;
import com.perl5.lang.perl.idea.sdk.host.PerlHostData;
import com.perl5.lang.perl.idea.sdk.host.PerlHostHandler;
import com.perl5.lang.perl.idea.sdk.implementation.PerlImplementationData;
import com.perl5.lang.perl.idea.sdk.implementation.PerlImplementationHandler;
import com.perl5.lang.perl.idea.sdk.versionManager.PerlVersionManagerAdapter;
import com.perl5.lang.perl.idea.sdk.versionManager.PerlVersionManagerData;
import com.perl5.lang.perl.idea.sdk.versionManager.PerlVersionManagerHandler;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

import java.util.Objects;

// TODO consulo: SdkAdditionalDataBase is missing in Consulo, its commit tracking is inlined here. Consulo never marks additional data
//  as committed, so writability assertions are not triggered by the platform
public class PerlSdkAdditionalData implements SaveAwareSdkAdditionalData, Cloneable {
  private static final Logger LOG = Logger.getInstance(PerlSdkAdditionalData.class);
  private Throwable myCommitStackTrace;
  private final @NotNull PerlHostData<?, ?> myHostData;
  private final @NotNull PerlVersionManagerData<?, ?> myVersionManagerData;
  private final @NotNull PerlImplementationData<?, ?> myImplementationData;
  private @NotNull PerlConfig myConfig;

  public PerlSdkAdditionalData(@NotNull PerlHostData<?, ?> hostData,
                               @NotNull PerlVersionManagerData<?, ?> versionManagerData,
                               @NotNull PerlImplementationData<?, ?> implementationData,
                               @NotNull PerlConfig perlConfig) {
    myHostData = hostData;
    myVersionManagerData = versionManagerData;
    myImplementationData = implementationData;
    myConfig = perlConfig;
  }

  public @NotNull PerlHostData<?, ?> getHostData() {
    return myHostData;
  }

  public @NotNull PerlVersionManagerData<?, ?> getVersionManagerData() {
    return myVersionManagerData;
  }

  @SuppressWarnings("unused")
  public @NotNull PerlImplementationData<?, ?> getImplementationData() {
    return myImplementationData;
  }

  public @NotNull PerlConfig getConfig() {
    return myConfig;
  }

  public final void markAsCommited() {
    if (myCommitStackTrace == null) {
      myCommitStackTrace = new Throwable();
      markInternalsAsCommited(myCommitStackTrace);
    }
  }

  protected final void assertWritable() {
    if (myCommitStackTrace != null) {
      LOG.error(new Throwable("Additional sdk data can't be directly modified, see javadoc for the assertion.", myCommitStackTrace));
    }
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  protected void markInternalsAsCommited(@NotNull Throwable commitStackTrace) {
    myHostData.markAsCommited(commitStackTrace);
    myVersionManagerData.markAsCommited(commitStackTrace);
    myImplementationData.markAsCommited(commitStackTrace);
  }

  void setConfig(@NotNull PerlConfig config) {
    assertWritable();
    myConfig = config;
  }

  public @NotNull PerlVersionManagerHandler<?, ?> getVersionManagerHandler() {
    return getVersionManagerData().getHandler();
  }

  public @Nullable PerlVersionManagerAdapter getVersionManagerAdapter() {
    var versionManagerPath = getVersionManagerData().getVersionManagerPath();
    return versionManagerPath == null ? null : getVersionManagerHandler().createAdapter(versionManagerPath, getHostData());
  }

  @TestOnly
  public boolean isSystem() {
    return getVersionManagerData().isSystem();
  }

  public boolean isLocal() {
    return getHostData().getHandler().isLocal();
  }

  @Contract("null -> null")
  public static @Nullable PerlSdkAdditionalData from(@Nullable Sdk sdk) {
    return sdk == null ? null : ObjectUtil.tryCast(sdk.getSdkAdditionalData(), PerlSdkAdditionalData.class);
  }

  @Contract("null -> null")
  public static @Nullable PerlSdkAdditionalData from(@Nullable SdkModificator sdkModificator) {
    return sdkModificator == null ? null : ObjectUtil.tryCast(sdkModificator.getSdkAdditionalData(), PerlSdkAdditionalData.class);
  }

  public static @NotNull PerlSdkAdditionalData notNullFrom(@NotNull Sdk sdk) {
    return Objects.requireNonNull(from(sdk), () -> "No additional data in " + sdk + "; additionalData: " + sdk.getSdkAdditionalData());
  }

  public static @NotNull PerlSdkAdditionalData notNullFrom(@NotNull SdkModificator sdkModificator) {
    return Objects.requireNonNull(from(sdkModificator), () -> "No additional data in " + sdkModificator + "; additionalData: " + sdkModificator.getSdkAdditionalData());
  }

  @Override
  public void save(@NotNull Element target) {
    myHostData.save(target);
    myImplementationData.save(target);
    myVersionManagerData.save(target);
    myConfig.save(target);
  }

  @SuppressWarnings("StaticMethodOnlyUsedInOneClass")
  static @NotNull SdkAdditionalData load(@NotNull Element source) {
    PerlHostData<?, ?> hostData = PerlHostHandler.load(source);
    PerlVersionManagerData<?, ?> versionManagerData = PerlVersionManagerHandler.load(source);
    PerlImplementationData<?, ?> implementationData = PerlImplementationHandler.load(source);
    if (hostData != null && versionManagerData != null && implementationData != null) {
      return new PerlSdkAdditionalData(hostData, versionManagerData, implementationData, PerlConfig.load(source));
    }
    return new UnknownSdkAdditionalData(source);
  }

  private static class UnknownSdkAdditionalData implements SaveAwareSdkAdditionalData {
    @Override
    public Object clone() throws CloneNotSupportedException {
      return super.clone();
    }

    private final @NotNull Element myAdditionalElement;

    UnknownSdkAdditionalData(@NotNull Element element) {
      myAdditionalElement = element.clone();
    }

    @Override
    public void save(@NotNull Element additional) {
      // TODO consulo: JDOMUtil.copyMissingContent is missing in Consulo, inlined
      for (Attribute attribute : myAdditionalElement.getAttributes()) {
        if (additional.getAttribute(attribute.getName(), attribute.getNamespace()) == null) {
          additional.setAttribute(attribute.clone());
        }
      }
      for (Element child : myAdditionalElement.getChildren()) {
        if (additional.getChild(child.getName(), child.getNamespace()) == null) {
          additional.addContent(child.clone());
        }
      }
    }
  }
}
