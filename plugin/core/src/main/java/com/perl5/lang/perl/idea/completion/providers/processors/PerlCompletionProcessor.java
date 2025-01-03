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

package com.perl5.lang.perl.idea.completion.providers.processors;

import consulo.application.util.matcher.PrefixMatcher;
import consulo.language.editor.completion.CompletionParameters;
import consulo.language.editor.completion.CompletionResultSet;
import consulo.language.editor.completion.lookup.LookupElementBuilder;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface PerlCompletionProcessor {
  @NotNull
  CompletionResultSet getResultSet();

  @NotNull
  CompletionParameters getCompletionParameters();

  @NotNull
  PsiElement getLeafElement();

  default @NotNull PsiElement getLeafParentElement() {
    return getLeafElement().getParent();
  }

  default @NotNull PsiFile getContainingFile() {
    return getLeafElement().getContainingFile();
  }

  default @NotNull PsiFile getOriginalFile() {
    return getContainingFile().getOriginalFile();
  }

  default @NotNull VirtualFile getVirtualFile() {
    return getOriginalFile().getVirtualFile();
  }

  default @NotNull Project getProject() {
    return getLeafElement().getProject();
  }

  /**
   * @return true iff {@code suggestedName} matches current prefix matcher
   */
  @Contract("null->false")
  boolean matches(@Nullable String suggestedName);

  /**
   * @return true iff we should go on, false if we should stop
   */
  boolean process(@Nullable LookupElementBuilder lookupElement);

  /**
   * Should be used for a single custom-created elements. For mass update, please, check texts before creation a lookup
   */
  boolean processSingle(@NotNull LookupElementBuilder lookupElement);

  @Contract(pure = true)
  @NotNull PerlCompletionProcessor withPrefixMatcher(@NotNull String prefix);

  void logStatus(@NotNull Class<?> clazz);

  /**
   * @return true iff we should go on, false if we should stop
   */
  boolean result();

  void addElement(@NotNull LookupElementBuilder lookupElement);

  default @NotNull PrefixMatcher getPrefixMatcher() {
    return getResultSet().getPrefixMatcher();
  }

  default @NotNull String getPrefix() {
    return getPrefixMatcher().getPrefix();
  }

  /**
   * @return true iff {@code elementId} was not registered with this processor before. E.g. variable fqn
   */
  boolean register(@Nullable String elementId);

  /**
   * @return true iff {@code elementId} was registered with this processor before. E.g. variable fqn
   */
  @Contract("null->false")
  boolean isRegistered(@Nullable String elementId);
}
