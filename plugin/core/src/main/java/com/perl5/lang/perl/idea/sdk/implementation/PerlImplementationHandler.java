/*
 * Copyright 2015-2020 Alexandr Evstigneev
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

package com.perl5.lang.perl.idea.sdk.implementation;

import com.perl5.lang.perl.idea.sdk.AbstractPerlHandler;
import com.perl5.lang.perl.idea.sdk.PerlHandlerBean;
import com.perl5.lang.perl.idea.sdk.PerlHandlerCollector;
import com.perl5.lang.perl.idea.sdk.host.PerlHostData;
import com.perl5.lang.perl.idea.sdk.versionManager.PerlVersionManagerData;
import consulo.content.bundle.Sdk;
import org.jdom.Element;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class PerlImplementationHandler<Data extends PerlImplementationData<Data, Handler>, Handler extends PerlImplementationHandler<Data, Handler>>
  extends AbstractPerlHandler<Data, Handler> {

  private static final String TAG_NAME = "implementation";

  private static final PerlHandlerCollector<PerlImplementationHandler<?, ?>> EP =
    new PerlHandlerCollector<>("com.perl5.implementationHandler");

  public PerlImplementationHandler(@NotNull PerlHandlerBean bean) {
    super(bean);
  }

  /**
   * Attempts to create an implementation data for {@code sdk}, {@code hostData} and {@code versionManagerData}
   *
   * @param interpreterPath    perl interpreter path
   * @param hostData           transport-level data to access an sdk host
   * @param versionManagerData information about used version manager
   * @return new implementation data or null if something went wrong. Handler should handle errors by itself.
   * @implSpec handler is free to adjust sdk with necessary data
   */
  @Contract("null, _, _ -> null; _, null, _ -> null; _, _, null -> null")
  protected abstract @Nullable Data doCreateData(@Nullable String interpreterPath,
                                                 @Nullable PerlHostData<?, ?> hostData,
                                                 @Nullable PerlVersionManagerData<?, ?> versionManagerData);

  @Override
  protected final @NotNull String getTagName() {
    return TAG_NAME;
  }

  public static @NotNull List<? extends PerlImplementationHandler<?, ?>> all() {
    return EP.getExtensionsList();
  }

  public static void forEach(@NotNull Consumer<? super PerlImplementationHandler<?, ?>> action) {
    all().forEach(action);
  }

  @Contract("null->null")
  static @Nullable PerlImplementationHandler<?, ?> from(@Nullable Sdk sdk) {
    PerlImplementationData<?, ?> perlImplementationData = PerlImplementationData.from(sdk);
    return perlImplementationData == null ? null : perlImplementationData.getHandler();
  }

  public static @NotNull Stream<? extends PerlImplementationHandler<?, ?>> stream() {
    return all().stream();
  }

  /**
   * Attempts to choose a handler and create an implementation data for {@code sdk}, {@code hostData} and {@code versionManagerData}
   *
   * @param interpreterPath    perl interpreter path
   * @param hostData           transport-level data to access an sdk host
   * @param versionManagerData information about used version manager
   * @return new implementation data or null if something went wrong. Handler should handle errors by itself.
   * @implSpec handler is free to adjust sdk with necessary data. Current implementation just uses a default handler
   */
  @Contract("null, _, _ -> null; _, null, _ -> null; _, _, null -> null")
  public static @Nullable PerlImplementationData<?, ?> createData(@Nullable String interpreterPath,
                                                                  @Nullable PerlHostData<?, ?> hostData,
                                                                  @Nullable PerlVersionManagerData<?, ?> versionManagerData) {
    return getDefaultHandler().doCreateData(interpreterPath, hostData, versionManagerData);
  }

  /**
   * Attempts to load {@link PerlImplementationData} from the {@code parentElement}
   *
   * @return data read or null if data can't be read
   */
  public static @Nullable PerlImplementationData<?, ?> load(@NotNull Element parentElement) {
    Element element = parentElement.getChild(TAG_NAME);
    if (element == null) {
      return null;
    }
    PerlImplementationHandler<?, ?> handler = EP.findSingle(element.getAttributeValue(ID_ATTRIBUTE));
    return handler != null ? handler.loadData(element) : null;
  }

  public static @NotNull PerlImplementationHandler<?, ?> getDefaultHandler() {
    return Objects.requireNonNull(EP.findSingle("porters"), "Perl porters implementation handler must always present");
  }
}
