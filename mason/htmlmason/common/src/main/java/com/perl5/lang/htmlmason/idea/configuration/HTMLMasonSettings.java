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

package com.perl5.lang.htmlmason.idea.configuration;

import com.perl5.lang.mason2.idea.configuration.VariableDescription;
import com.perl5.lang.perl.util.PerlConfigurationUtil;
import consulo.annotation.access.RequiredReadAction;
import consulo.language.file.light.LightVirtualFile;
import consulo.language.psi.PsiElement;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.perl5.mason.module.HTMLMasonModuleExtension;
import consulo.project.Project;
import consulo.util.lang.lazy.LazyValue;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileNameMatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

import static com.perl5.lang.htmlmason.HTMLMasonSyntaxElements.*;

/**
 * HTML::Mason settings of a module.
 * <p>
 * TODO consulo: upstream is a project-level persistent service. In Consulo the settings are the state of the HTML::Mason module extension
 *  ({@link HTMLMasonModuleExtension}), see {@link AbstractMasonSettings}. The settings of a file are the settings of its module if the
 *  extension is enabled there, {@link #DEFAULT} otherwise. Changes are handled by {@code HTMLMasonSettingsChangeListener}.
 */
public final class HTMLMasonSettings extends AbstractMasonSettings {
  public static final HTMLMasonSettings DEFAULT = new HTMLMasonSettings(
    "autohandler",
    "dhandler",
    List.of(),
    List.of(),
    List.of(new VariableDescription("$m", "HTML::Mason::Request"), new VariableDescription("$r", "Apache::Request")));

  private final @NotNull String myAutoHandlerName;
  private final @NotNull String myDefaultHandlerName;
  private final @NotNull List<String> mySubstitutedExtensions;
  private final @NotNull List<HTMLMasonCustomTag> myCustomTags;

  private final Supplier<List<FileNameMatcher>> myLazyMatchersProvider;
  private final Supplier<Map<String, String>> myOpenCloseMapProvider = LazyValue.atomicNotNull(this::computeOpenCloseMap);
  private final Supplier<Map<String, HTMLMasonCustomTag>> myCustomTagsMapProvider = LazyValue.atomicNotNull(this::computeCustomTagsMap);

  /**
   * @param substitutedExtensions file name matchers (presentable strings) of files which should be handled as components under components
   *                              roots
   * @param customTags            custom tags which mimic the built-in ones, they are copied
   * @param globalVariables       global variables of components, they are copied
   */
  public HTMLMasonSettings(@NotNull String autoHandlerName,
                           @NotNull String defaultHandlerName,
                           @NotNull List<String> substitutedExtensions,
                           @NotNull List<HTMLMasonCustomTag> customTags,
                           @NotNull List<VariableDescription> globalVariables) {
    super(globalVariables);
    myAutoHandlerName = autoHandlerName;
    myDefaultHandlerName = defaultHandlerName;
    mySubstitutedExtensions = List.copyOf(substitutedExtensions);
    myCustomTags = customTags.stream().map(HTMLMasonCustomTag::copy).toList();
    myLazyMatchersProvider = LazyValue.atomicNotNull(() -> PerlConfigurationUtil.getSubstitutedExtensionsMatchers(mySubstitutedExtensions));
  }

  public @NotNull String getAutoHandlerName() {
    return myAutoHandlerName;
  }

  public @NotNull String getDefaultHandlerName() {
    return myDefaultHandlerName;
  }

  public @NotNull List<String> getSubstitutedExtensions() {
    return mySubstitutedExtensions;
  }

  /**
   * @return custom tags, must not be modified
   */
  public @NotNull List<HTMLMasonCustomTag> getCustomTags() {
    return myCustomTags;
  }

  private @NotNull Map<String, HTMLMasonCustomTag> computeCustomTagsMap() {
    if (myCustomTags.isEmpty()) {
      return Collections.emptyMap();
    }

    Map<String, HTMLMasonCustomTag> result = new HashMap<>();

    for (HTMLMasonCustomTag customTag : myCustomTags) {
      result.put(customTag.getText(), customTag);
    }

    return result;
  }

  public boolean isVirtualFileNameMatches(@NotNull VirtualFile file) {
    for (FileNameMatcher matcher : myLazyMatchersProvider.get()) {
      if (matcher.acceptsCharSequence(file.getName())) {
        return true;
      }
    }
    return false;
  }

  private @NotNull Map<String, String> computeOpenCloseMap() {
    Map<String, String> openCloseMap = new HashMap<>();
    openCloseMap.put(KEYWORD_PERL_OPENER, KEYWORD_PERL_CLOSER);
    openCloseMap.put(KEYWORD_INIT_OPENER, KEYWORD_INIT_CLOSER);
    openCloseMap.put(KEYWORD_CLEANUP_OPENER, KEYWORD_CLEANUP_CLOSER);
    openCloseMap.put(KEYWORD_ONCE_OPENER, KEYWORD_ONCE_CLOSER);
    openCloseMap.put(KEYWORD_SHARED_OPENER, KEYWORD_SHARED_CLOSER);
    openCloseMap.put(KEYWORD_FLAGS_OPENER, KEYWORD_FLAGS_CLOSER);
    openCloseMap.put(KEYWORD_ATTR_OPENER, KEYWORD_ATTR_CLOSER);
    openCloseMap.put(KEYWORD_ARGS_OPENER, KEYWORD_ARGS_CLOSER);
    openCloseMap.put(KEYWORD_FILTER_OPENER, KEYWORD_FILTER_CLOSER);
    openCloseMap.put(KEYWORD_DOC_OPENER, KEYWORD_DOC_CLOSER);
    openCloseMap.put(KEYWORD_TEXT_OPENER, KEYWORD_TEXT_CLOSER);

    // parametrized
    openCloseMap.put(KEYWORD_METHOD_OPENER, KEYWORD_METHOD_CLOSER);
    openCloseMap.put(KEYWORD_DEF_OPENER, KEYWORD_DEF_CLOSER);

    // iterate custom tags
    for (HTMLMasonCustomTag customTag : myCustomTags) {
      openCloseMap.put(customTag.getOpenTagText(), customTag.getCloseTagText());
    }
    return Collections.unmodifiableMap(openCloseMap);
  }

  public @NotNull Map<String, String> getOpenCloseMap() {
    return myOpenCloseMapProvider.get();
  }

  public @NotNull Map<String, HTMLMasonCustomTag> getCustomTagsMap() {
    return myCustomTagsMapProvider.get();
  }

  @Override
  public boolean equals(Object o) {
    return this == o ||
           super.equals(o) &&
           o instanceof HTMLMasonSettings settings &&
           myAutoHandlerName.equals(settings.myAutoHandlerName) &&
           myDefaultHandlerName.equals(settings.myDefaultHandlerName) &&
           mySubstitutedExtensions.equals(settings.mySubstitutedExtensions) &&
           myCustomTags.equals(settings.myCustomTags);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), myAutoHandlerName, myDefaultHandlerName, mySubstitutedExtensions, myCustomTags);
  }

  /**
   * @return settings of the {@code module} if the HTML::Mason module extension is enabled for it, {@link #DEFAULT} otherwise
   */
  public static @NotNull HTMLMasonSettings forModule(@Nullable Module module) {
    HTMLMasonModuleExtension extension =
      module == null || module.isDisposed() ? null : module.getExtension(HTMLMasonModuleExtension.class);
    return extension == null ? DEFAULT : extension.getSettings();
  }

  /**
   * @return settings of the module of the {@code file}, {@link #DEFAULT} if the file is not in a module with the HTML::Mason module
   * extension enabled
   */
  public static @NotNull HTMLMasonSettings forFile(@Nullable Project project, @Nullable VirtualFile file) {
    if (file instanceof LightVirtualFile lightVirtualFile && lightVirtualFile.getOriginalFile() != null) {
      return forFile(project, lightVirtualFile.getOriginalFile());
    }
    if (project == null || project.isDisposed() || file == null) {
      return DEFAULT;
    }
    return forModule(ModuleUtilCore.findModuleForFile(file, project));
  }

  /**
   * @return settings of the module of the {@code element}, {@link #DEFAULT} if it is not in a module with the HTML::Mason module extension
   * enabled
   */
  @RequiredReadAction
  public static @NotNull HTMLMasonSettings forPsiElement(@Nullable PsiElement element) {
    return element == null || !element.isValid() ? DEFAULT : forModule(element.getModule());
  }
}
