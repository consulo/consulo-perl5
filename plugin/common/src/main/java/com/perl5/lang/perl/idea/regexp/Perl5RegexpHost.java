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
package com.perl5.lang.perl.idea.regexp;

import com.perl5.lang.perl.psi.impl.PsiPerlPerlRegexImpl;
import consulo.annotation.component.ExtensionImpl;
import org.intellij.lang.regexp.RegExpLanguageHost;
import org.intellij.lang.regexp.psi.RegExpChar;
import org.intellij.lang.regexp.psi.RegExpGroup;
import org.intellij.lang.regexp.psi.RegExpNamedGroupRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static org.intellij.lang.regexp.DefaultRegExpPropertiesProvider.getInstance;

@ExtensionImpl
public final class Perl5RegexpHost implements RegExpLanguageHost {
  // consulo: upstream registers this host for PsiPerlPerlRegexImpl in plugin.xml
  @Override
  public @NotNull Class getHostClass() {
    return PsiPerlPerlRegexImpl.class;
  }

  // consulo: Consulo RegExpLanguageHost has no isInClass parameter
  @Override
  public boolean characterNeedsEscaping(char c) {
    return false;
  }

  @Override
  public boolean supportsPerl5EmbeddedComments() {
    return false;
  }

  // consulo: Consulo RegExpLanguageHost has no context parameter
  @Override
  public boolean supportsPossessiveQuantifiers() {
    return false;
  }

  @Override
  public boolean supportsPythonConditionalRefs() {
    return false;
  }

  @Override
  public boolean supportsNamedGroupSyntax(@Nullable RegExpGroup group) {
    return false;
  }

  @Override
  public boolean supportsNamedGroupRefSyntax(@Nullable RegExpNamedGroupRef ref) {
    return false;
  }

  @Override
  public boolean supportsExtendedHexCharacter(@Nullable RegExpChar regExpChar) {
    return false;
  }

  @Override
  public boolean isValidCategory(@NotNull String category) {
    return getInstance().isValidCategory(category);
  }

  // todo: http://perldoc.perl.org/perluniprops.html and /Perl5/lib/unicore/
  @Override
  public String @NotNull [] @NotNull [] getAllKnownProperties() {
    return getInstance().getAllKnownProperties();
  }

  @Override
  public @Nullable String getPropertyDescription(@Nullable String name) {
    return getInstance().getPropertyDescription(name);
  }

  @Override
  public String @NotNull [] @NotNull [] getKnownCharacterClasses() {
    return getInstance().getKnownCharacterClasses();
  }
}
