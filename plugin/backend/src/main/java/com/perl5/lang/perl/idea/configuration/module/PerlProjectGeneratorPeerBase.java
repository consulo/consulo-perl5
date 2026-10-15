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

package com.perl5.lang.perl.idea.configuration.module;

import consulo.configurable.UnnamedConfigurable;
import consulo.ui.ex.awt.TextFieldWithBrowseButton;
import consulo.application.util.AtomicNotNullLazyValue;
import com.perl5.lang.perl.idea.configuration.settings.sdk.Perl5SdkConfigurable;
import com.perl5.lang.perl.idea.project.PerlProjectManager;
import consulo.disposer.Disposable;
import consulo.ui.Component;
import consulo.ui.ex.awtUnsafe.TargetAWT;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

// TODO consulo: Consulo has no ProjectGeneratorPeer/GeneratorPeerImpl API (see PerlProjectGeneratorBase); settings are held here
public abstract class PerlProjectGeneratorPeerBase<Settings extends PerlProjectGenerationSettings>
  implements UnnamedConfigurable {
  private final @NotNull Settings mySettings;
  private final @NotNull JComponent myMainComponent;
  private final @NotNull Perl5SdkConfigurable mySdkConfigurable;
  private final AtomicNotNullLazyValue<JComponent> myComponentProvider = AtomicNotNullLazyValue.createValue(
    this::initializeComponent);

  public PerlProjectGeneratorPeerBase(@NotNull Settings settings, @NotNull JComponent mainComponent) {
    mySettings = settings;
    myMainComponent = mainComponent;
    mySdkConfigurable = new Perl5SdkConfigurable(getSettings(), null);
  }

  public @NotNull Settings getSettings() {
    return mySettings;
  }

  public @NotNull JComponent getComponent(@NotNull TextFieldWithBrowseButton myLocationField, @NotNull Runnable checkValid) {
    // consulo: IntelliJ GeneratorPeerImpl.getComponent(field, checkValid) only returned the component, nothing to initialize
    return myComponentProvider.getValue();
  }

  public @NotNull JComponent getComponent() {
    return myComponentProvider.getValue();
  }

  @Override
  public void disposeUIResources() {
    mySdkConfigurable.disposeUIResources();
  }

  protected @NotNull JComponent initializeComponent() {
    myMainComponent.add(mySdkConfigurable.getComponent());
    mySdkConfigurable.setEnabled(PerlProjectManager.getSdk(getSettings().getProject()) == null);
    return myMainComponent;
  }

  @Override
  public final Component createUIComponent(Disposable parentDisposable) {
    return TargetAWT.wrap(myMainComponent);
  }

  @Override
  public boolean isModified() {
    throw new RuntimeException("NYI");
  }

  @Override
  public void apply() {
    mySdkConfigurable.apply();
  }

  @Override
  public void reset() {
    mySdkConfigurable.reset();
  }
}
