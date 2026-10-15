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

package com.perl5.lang.perl.idea.configuration.settings;

import consulo.language.editor.DaemonCodeAnalyzer;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ServiceAPI;
import consulo.annotation.component.ServiceImpl;
import consulo.component.persist.PersistentStateComponent;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import consulo.component.persist.State;
import consulo.component.persist.Storage;
import consulo.project.Project;
import consulo.util.lang.lazy.ClearableLazyValue;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiManager;
import consulo.util.collection.ContainerUtil;
import consulo.util.xml.serializer.XmlSerializerUtil;
import consulo.util.xml.serializer.annotation.Tag;
import consulo.util.xml.serializer.annotation.Transient;
import com.perl5.lang.perl.idea.PerlPathMacros;
import com.perl5.lang.perl.internals.PerlVersion;
import com.perl5.lang.perl.util.PerlUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.perl5.lang.perl.util.PerlScalarUtilCore.DEFAULT_SELF_NAME;


@State(
  name = "Perl5Settings",
  storages = @Storage(PerlPathMacros.PERL5_PROJECT_SHARED_SETTINGS_FILE)
)
@ServiceAPI(ComponentScope.PROJECT)
@ServiceImpl
@Singleton
public final class PerlSharedSettings implements PersistentStateComponent<PerlSharedSettings> {
  public List<String> selfNames = PerlUtil.mutableList(DEFAULT_SELF_NAME, "this", "class", "proto");

  private final List<String> myWarningsProviders = new ArrayList<>();

  private final List<String> myStrictProviders = new ArrayList<>();

  public boolean SIMPLE_MAIN_RESOLUTION = true;
  public boolean AUTOMATIC_HEREDOC_INJECTIONS = true;
  public boolean ALLOW_INJECTIONS_WITH_INTERPOLATION = false;
  public boolean PERL_CRITIC_ENABLED = false;
  public boolean PERL_ANNOTATOR_ENABLED = false;
  public String PERL_DEPARSE_ARGUMENTS = "";
  public String PERL_TIDY_ARGS = "";
  public String PERL_CRITIC_ARGS = "";
  public boolean PERL_SWITCH_ENABLED = false;
  private PerlVersion myTargetPerlVersion = PerlVersion.V5_10;

  @Transient
  private Set<String> SELF_NAMES_SET = null;

  private final ClearableLazyValue<Set<String>> myWarningsProvidersSet = ClearableLazyValue.notNull(() -> Set.copyOf(myWarningsProviders));
  private final ClearableLazyValue<Set<String>> myStrictProvidersSet = ClearableLazyValue.notNull(() -> Set.copyOf(myStrictProviders));

  @Transient
  private final Project myProject;

  @SuppressWarnings("unused")
  private PerlSharedSettings() {
    this(null);
  }

  @Inject
  public PerlSharedSettings(Project project) {
    myProject = project;
    setWarningsProviders(ContainerUtil.concat(PerlStrictWarningsDefaults.DEFAULT_STRICT_AND_WARNINGS_PROVIDERS,
                                              PerlStrictWarningsDefaults.DEFAULT_WARNINGS_ONLY_PROVIDERS));
    setStrictProviders(ContainerUtil.concat(PerlStrictWarningsDefaults.DEFAULT_STRICT_AND_WARNINGS_PROVIDERS,
                                            PerlStrictWarningsDefaults.DEFAULT_STRICT_ONLY_PROVIDERS));
  }

  @Override
  public @NotNull PerlSharedSettings getState() {
    return this;
  }

  @Override
  public void loadState(@NotNull PerlSharedSettings state) {
    XmlSerializerUtil.copyBean(state, this);
  }

  public @NotNull PerlVersion getTargetPerlVersion() {
    return myTargetPerlVersion;
  }

  public @NotNull PerlSharedSettings setTargetPerlVersion(@NotNull PerlVersion targetPerlVersion) {
    myTargetPerlVersion = targetPerlVersion;
    return this;
  }

  public void settingsUpdated() {
    SELF_NAMES_SET = null;
    PsiManager.getInstance(myProject).dropResolveCaches();
    DaemonCodeAnalyzer.getInstance(myProject).restart("Perl shared settings update");
  }

  public boolean isSelfName(String name) {
    if (SELF_NAMES_SET == null) {
      SELF_NAMES_SET = new HashSet<>(selfNames);
    }
    return SELF_NAMES_SET.contains(name);
  }

  public void setStrictProviders(@NotNull List<String> strictProviders) {
    myStrictProviders.clear();
    myStrictProviders.addAll(ContainerUtil.sorted(strictProviders));
    myStrictProvidersSet.clear();
  }

  @Contract("null->false")
  public boolean isStrictProvider(@Nullable String packageName) {
    return packageName != null && myStrictProvidersSet.get().contains(packageName);
  }

  public void setWarningsProviders(@NotNull List<String> warningsProviders) {
    myWarningsProviders.clear();
    myWarningsProviders.addAll(ContainerUtil.sorted(warningsProviders));
    myWarningsProvidersSet.clear();
  }

  @Contract("null->false")
  public boolean isWarningsProvider(@Nullable String packageName) {
    return packageName != null && myWarningsProvidersSet.get().contains(packageName);
  }

  public void setDeparseOptions(String optionsString) {
    while (!optionsString.isEmpty() && optionsString.charAt(0) != '-') {
      optionsString = optionsString.substring(1);
    }
    PERL_DEPARSE_ARGUMENTS = optionsString;
  }

  // TODO consulo: PersistentStateComponent has no noStateLoaded() callback, so this is never called
  public void noStateLoaded() {
    loadState(new PerlSharedSettings());
  }

  public static @NotNull PerlSharedSettings getInstance(@NotNull Project project) {
    return project.getService(PerlSharedSettings.class);
  }

  public static @NotNull PerlSharedSettings getInstance(@NotNull PsiElement psiElement) {
    return getInstance(psiElement.getProject());
  }

  @Tag("WARNING_PROVIDERS")
  public @NotNull List<String> getWarningsProviders() {
    return List.copyOf(myWarningsProviders);
  }

  @Tag("STRICT_PROVIDERS")
  public @NotNull List<String> getStrictProviders() {
    return List.copyOf(myStrictProviders);
  }
}
