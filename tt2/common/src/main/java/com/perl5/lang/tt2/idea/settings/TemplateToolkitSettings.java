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

package com.perl5.lang.tt2.idea.settings;

import com.perl5.lang.perl.util.PerlConfigurationUtil;
import consulo.annotation.access.RequiredReadAction;
import consulo.codeEditor.Editor;
import consulo.document.FileDocumentManager;
import consulo.language.file.light.LightVirtualFile;
import consulo.language.psi.PsiElement;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.perl5.tt2.module.TemplateToolkitModuleExtension;
import consulo.project.Project;
import consulo.util.lang.lazy.LazyValue;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.fileType.FileNameMatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Template Toolkit settings of a module.
 * <p>
 * TODO consulo: upstream is a project-level persistent service, edited on a tab of the Perl5 settings page, which fires
 *  {@code TT2_SETTINGS_TOPIC} on change. In Consulo the settings are per module: the state of the Template Toolkit module extension
 *  ({@link TemplateToolkitModuleExtension}, a child of the Perl module extension), edited in the module settings. Instances are immutable;
 *  the settings of a file are the settings of its module if the extension is enabled there, {@link #DEFAULT} otherwise. Changes are
 *  handled by {@code TemplateToolkitSettingsChangeListener}.
 */
public final class TemplateToolkitSettings {
  public static final String DEFAULT_START_TAG = "[%";
  public static final String DEFAULT_END_TAG = "%]";
  public static final String DEFAULT_OUTLINE_TAG = "%%";
  public static final TemplateToolkitSettings DEFAULT =
    new TemplateToolkitSettings(DEFAULT_START_TAG, DEFAULT_END_TAG, DEFAULT_OUTLINE_TAG, false, List.of());

  private final @NotNull String myStartTag;
  private final @NotNull String myEndTag;
  private final @NotNull String myOutlineTag;
  private final boolean myAnycaseEnabled;
  private final @NotNull List<String> mySubstitutedExtensions;
  private final Supplier<List<FileNameMatcher>> myMatchers;

  /**
   * @param outlineTag            empty string disables outline tags
   * @param substitutedExtensions file name matchers (presentable strings) of files which should be handled as templates under templates
   *                              roots
   */
  public TemplateToolkitSettings(@NotNull String startTag,
                                 @NotNull String endTag,
                                 @NotNull String outlineTag,
                                 boolean anycaseEnabled,
                                 @NotNull List<String> substitutedExtensions) {
    myStartTag = startTag;
    myEndTag = endTag;
    myOutlineTag = outlineTag;
    myAnycaseEnabled = anycaseEnabled;
    mySubstitutedExtensions = List.copyOf(substitutedExtensions);
    myMatchers = LazyValue.atomicNotNull(() -> PerlConfigurationUtil.getSubstitutedExtensionsMatchers(mySubstitutedExtensions));
  }

  public @NotNull String getStartTag() {
    return myStartTag;
  }

  public @NotNull String getEndTag() {
    return myEndTag;
  }

  public @NotNull String getOutlineTag() {
    return myOutlineTag;
  }

  public boolean isAnycaseEnabled() {
    return myAnycaseEnabled;
  }

  public @NotNull List<String> getSubstitutedExtensions() {
    return mySubstitutedExtensions;
  }

  /**
   * @return true iff the {@code file} name matches one of the {@link #getSubstitutedExtensions() substituted extensions}
   */
  public boolean isVirtualFileNameMatches(@NotNull VirtualFile file) {
    for (FileNameMatcher matcher : myMatchers.get()) {
      if (matcher.acceptsCharSequence(file.getName())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean equals(Object o) {
    return this == o ||
           o instanceof TemplateToolkitSettings settings &&
           myAnycaseEnabled == settings.myAnycaseEnabled &&
           myStartTag.equals(settings.myStartTag) &&
           myEndTag.equals(settings.myEndTag) &&
           myOutlineTag.equals(settings.myOutlineTag) &&
           mySubstitutedExtensions.equals(settings.mySubstitutedExtensions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(myStartTag, myEndTag, myOutlineTag, myAnycaseEnabled, mySubstitutedExtensions);
  }

  /**
   * @return settings of the {@code module} if the Template Toolkit module extension is enabled for it, {@link #DEFAULT} otherwise
   */
  public static @NotNull TemplateToolkitSettings forModule(@Nullable Module module) {
    TemplateToolkitModuleExtension extension =
      module == null || module.isDisposed() ? null : module.getExtension(TemplateToolkitModuleExtension.class);
    return extension == null ? DEFAULT : extension.getSettings();
  }

  /**
   * @return settings of the module of the {@code file}, {@link #DEFAULT} if the file is not in a module with the Template Toolkit module
   * extension enabled
   */
  public static @NotNull TemplateToolkitSettings forFile(@Nullable Project project, @Nullable VirtualFile file) {
    if (file instanceof LightVirtualFile lightVirtualFile && lightVirtualFile.getOriginalFile() != null) {
      return forFile(project, lightVirtualFile.getOriginalFile());
    }
    if (project == null || project.isDisposed() || file == null) {
      return DEFAULT;
    }
    return forModule(ModuleUtilCore.findModuleForFile(file, project));
  }

  /**
   * @return settings of the module of the {@code element}, {@link #DEFAULT} if it is not in a module with the Template Toolkit module
   * extension enabled
   */
  @RequiredReadAction
  public static @NotNull TemplateToolkitSettings forPsiElement(@Nullable PsiElement element) {
    return element == null || !element.isValid() ? DEFAULT : forModule(element.getModule());
  }

  /**
   * @return settings of the module of the file edited in the {@code editor}
   */
  public static @NotNull TemplateToolkitSettings forEditor(@Nullable Project project, @Nullable Editor editor) {
    return editor == null ? DEFAULT : forFile(project, FileDocumentManager.getInstance().getFile(editor.getDocument()));
  }
}
