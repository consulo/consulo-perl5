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

package com.perl5.lang.perl.parser.Class.Accessor.psi.impl;

import consulo.language.psi.PsiElement;
import consulo.language.ast.IElementType;
import java.util.function.Function;
import com.perl5.lang.perl.extensions.PerlRenameUsagesHelper;
import com.perl5.lang.perl.psi.impl.PerlSubCallElement;
import com.perl5.lang.perl.psi.light.PerlDelegatingLightNamedElement;
import com.perl5.lang.perl.psi.light.PerlLightMethodDefinitionElement;
import com.perl5.lang.perl.psi.stubs.subsdefinitions.PerlSubDefinitionStub;
import com.perl5.lang.perl.psi.utils.PerlSubAnnotations;
import com.perl5.lang.perl.psi.utils.PerlSubArgument;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class PerlClassAccessorMethod extends PerlLightMethodDefinitionElement<PerlSubCallElement> implements PerlRenameUsagesHelper {
  private static final @NonNls String GETTER_PREFIX = "get_";
  private static final @NonNls String SETTER_PREFIX = "set_";

  public PerlClassAccessorMethod(
    @NotNull PerlSubCallElement delegate,
    @NotNull String baseName,
    @NotNull Function<String, String> nameComputation,
    @NotNull IElementType elementType,
    @NotNull PsiElement nameIdentifier,
    @Nullable String packageName,
    @Nullable PerlSubAnnotations annotations
  ) {
    super(
      delegate,
      nameComputation.apply(baseName),
      elementType,
      nameIdentifier,
      packageName,
      Collections.emptyList(),
      annotations
    );
    setNameComputation(nameComputation);
    setSubArgumentsProvider(() -> {
      if (this.isFollowBestPractice()) {
        if (hasSetterName()) {
          return List.of(
            PerlSubArgument.self(),
            PerlSubArgument.mandatoryScalar(PerlSubArgument.NEW_VALUE_VALUE)
          );
        }
      }
      else {
        return List.of(
          PerlSubArgument.self(),
          PerlSubArgument.optionalScalar(PerlSubArgument.NEW_VALUE_VALUE)
        );
      }
      return Collections.emptyList();
    });
  }

  public PerlClassAccessorMethod(
    @NotNull PerlSubCallElement delegate,
    @NotNull PerlSubDefinitionStub stub
  ) {
    super(delegate, stub);
    if (hasGetterName()) {
      setNameComputation(GETTER_COMPUTATION);
    }
    else if (hasSetterName()) {
      setNameComputation(SETTER_COMPUTATION);
    }
    else {
      setNameComputation(SIMPLE_COMPUTATION);
    }
  }

  private boolean isFollowBestPractice() {
    return PerlClassAccessorHandler.isFollowBestPractice(getDelegate());
  }

  private boolean hasGetterName() {
    return this.isFollowBestPractice() && getSubName().startsWith(GETTER_PREFIX);
  }

  private boolean hasSetterName() {
    return this.isFollowBestPractice() && getSubName().startsWith(SETTER_PREFIX);
  }

  @Override
  public @NotNull String getName() {
    return this.getBaseName();
  }

  private @NotNull String getBaseName() {
    String name = getSubName();
    return this.isFollowBestPractice() ?
           hasGetterName() ? name.substring(GETTER_PREFIX.length()) : name.substring(SETTER_PREFIX.length()) :
           name;
  }

  private @Nullable String getGetterName() {
    return this.isFollowBestPractice() && hasSetterName() ? GETTER_PREFIX + this.getBaseName() : getSubName();
  }

  private @Nullable String getSetterName() {
    return this.isFollowBestPractice() && hasGetterName() ? SETTER_PREFIX + this.getBaseName() : getSubName();
  }

  public @Nullable PerlClassAccessorMethod getPairedMethod() {
    if (!this.isFollowBestPractice()) {
      return null;
    }
    String baseName = this.getBaseName();
    for (PerlDelegatingLightNamedElement<?> element : getDelegate().getLightElements()) {
      if (element instanceof PerlClassAccessorMethod perlClassAccessorMethod &&
          baseName.equals(perlClassAccessorMethod.getBaseName()) && (!element.equals(this))
      ) {
        return perlClassAccessorMethod;
      }
    }
    return null;
  }

  @Override
  public @NotNull String getSubstitutedUsageName(@NotNull String newName, @NotNull PsiElement element) {
    if (this.isFollowBestPractice()) {
      String currentValue = element.getText();
      if (Objects.equals(this.getGetterName(), currentValue)) {
        return GETTER_PREFIX + newName;
      }
      else if (Objects.equals(this.getSetterName(), currentValue)) {
        return SETTER_PREFIX + newName;
      }
    }
    return newName;
  }

  @Override
  public boolean isInplaceRefactoringAllowed() {
    return !this.isFollowBestPractice();
  }

  public static final Function<String, String> SIMPLE_COMPUTATION = (String name) -> name;

  public static final Function<String, String> GETTER_COMPUTATION = (String name) -> GETTER_PREFIX + name;

  public static final Function<String, String> SETTER_COMPUTATION = (String name) -> SETTER_PREFIX + name;
}
