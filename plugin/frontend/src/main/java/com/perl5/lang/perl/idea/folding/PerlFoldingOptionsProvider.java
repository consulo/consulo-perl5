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

package com.perl5.lang.perl.idea.folding;

import consulo.annotation.component.ExtensionImpl;
import consulo.configurable.ApplicationConfigurable;
import consulo.configurable.SimpleConfigurableByProperties;
import consulo.disposer.Disposable;
import consulo.localize.LocalizeValue;
import consulo.ui.CheckBox;
import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.layout.VerticalLayout;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.PerlLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;


// consulo: IntelliJ BeanConfigurable + CodeFoldingOptionsProvider -> uni UI child configurable of the "Code Folding" settings page
@ExtensionImpl
public class PerlFoldingOptionsProvider extends SimpleConfigurableByProperties implements ApplicationConfigurable {
  private final List<CheckBoxDescriptor> myCheckBoxes = new ArrayList<>();

  public PerlFoldingOptionsProvider() {
    PerlFoldingSettingsImpl foldingSettings = PerlFoldingSettingsImpl.getInstance();
    checkBox(PerlBundle.message("perl.options.fold.comments"),
             () -> foldingSettings.COLLAPSE_COMMENTS, value -> foldingSettings.COLLAPSE_COMMENTS = value);
    checkBox(PerlBundle.message("perl.options.fold.constants"),
             () -> foldingSettings.COLLAPSE_CONSTANT_BLOCKS, value -> foldingSettings.COLLAPSE_CONSTANT_BLOCKS = value);
    checkBox(PerlBundle.message("perl.options.fold.arrays"),
             () -> foldingSettings.COLLAPSE_ANON_ARRAYS, value -> foldingSettings.COLLAPSE_ANON_ARRAYS = value);
    checkBox(PerlBundle.message("perl.options.fold.hashes"),
             () -> foldingSettings.COLLAPSE_ANON_HASHES, value -> foldingSettings.COLLAPSE_ANON_HASHES = value);
    checkBox(PerlBundle.message("perl.options.fold.parens"),
             () -> foldingSettings.COLLAPSE_PARENTHESISED, value -> foldingSettings.COLLAPSE_PARENTHESISED = value);
    checkBox(PerlBundle.message("perl.options.fold.heredocs"),
             () -> foldingSettings.COLLAPSE_HEREDOCS, value -> foldingSettings.COLLAPSE_HEREDOCS = value);
    checkBox(PerlBundle.message("perl.options.fold.templates"),
             () -> foldingSettings.COLLAPSE_TEMPLATES, value -> foldingSettings.COLLAPSE_TEMPLATES = value);
    checkBox(PerlBundle.message("perl.options.fold.qw"),
             () -> foldingSettings.COLLAPSE_QW, value -> foldingSettings.COLLAPSE_QW = value);
    checkBox(PerlBundle.message("perl.options.fold.char.substitutions"),
             () -> foldingSettings.COLLAPSE_CHAR_SUBSTITUTIONS, value -> foldingSettings.COLLAPSE_CHAR_SUBSTITUTIONS = value);
  }

  private void checkBox(@NotNull String title, @NotNull Supplier<Boolean> getter, @NotNull Consumer<Boolean> setter) {
    myCheckBoxes.add(new CheckBoxDescriptor(title, getter, setter));
  }

  @RequiredUIAccess
  @Override
  protected @NotNull Component createLayout(@NotNull PropertyBuilder propertyBuilder, @NotNull Disposable uiDisposable) {
    VerticalLayout layout = VerticalLayout.create();
    for (CheckBoxDescriptor descriptor : myCheckBoxes) {
      CheckBox checkBox = CheckBox.create(LocalizeValue.localizeTODO(descriptor.title()));
      layout.add(checkBox);
      propertyBuilder.add(checkBox, descriptor.getter(), descriptor.setter());
    }
    return layout;
  }

  @Override
  public @NotNull String getId() {
    return "editor.preferences.folding.perl5";
  }

  @Override
  public @Nullable String getParentId() {
    return "editor.preferences.folding";
  }

  @Override
  public @NotNull LocalizeValue getDisplayName() {
    return PerlLanguage.INSTANCE.getDisplayName();
  }

  private record CheckBoxDescriptor(@NotNull String title, @NotNull Supplier<Boolean> getter, @NotNull Consumer<Boolean> setter) {
  }
}
