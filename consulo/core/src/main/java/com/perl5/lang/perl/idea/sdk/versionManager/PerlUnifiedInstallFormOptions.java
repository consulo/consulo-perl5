package com.perl5.lang.perl.idea.sdk.versionManager;

import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;

/**
 * Unified UI (works in desktop and web) {@link PerlInstallFormOptions}: {@link PerlUnifiedInstallForm} shows the options with
 * {@link #createComponent()} instead of the Swing {@link #getRootPanel()}. Options without own settings may stay plain
 * {@link PerlInstallFormOptions}.
 */
public abstract class PerlUnifiedInstallFormOptions extends PerlInstallFormOptions {
  /**
   * Invoked once, when the install dialog is built
   */
  @RequiredUIAccess
  public abstract Component createComponent();
}
