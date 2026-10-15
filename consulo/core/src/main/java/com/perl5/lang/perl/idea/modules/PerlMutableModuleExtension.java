package com.perl5.lang.perl.idea.modules;

import com.intellij.openapi.projectRoots.impl.PerlModuleExtension;
import consulo.content.bundle.Sdk;
import consulo.disposer.Disposable;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.extension.MutableModuleExtensionWithSdk;
import consulo.module.extension.MutableModuleInheritableNamedPointer;
import consulo.perl5.core.module.PerlModuleExtensionPanel;
import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Mutable part of the Perl module extension: perl interpreter (sdk) selection
 */
public class PerlMutableModuleExtension extends PerlModuleExtension
  implements MutableModuleExtensionWithSdk<PerlModuleExtension> {
  public PerlMutableModuleExtension(@NotNull String id, @NotNull ModuleRootLayer rootLayer) {
    super(id, rootLayer);
  }

  @Override
  public @NotNull MutableModuleInheritableNamedPointer<Sdk> getInheritableSdk() {
    return (MutableModuleInheritableNamedPointer<Sdk>)super.getInheritableSdk();
  }

  @RequiredUIAccess
  @Override
  public @Nullable Component createConfigurationComponent(@NotNull Disposable uiDisposable, @NotNull Runnable updateOnCheck) {
    return PerlModuleExtensionPanel.create(this, uiDisposable, updateOnCheck);
  }

  @Override
  public void setEnabled(boolean val) {
    myIsEnabled = val;
  }

  @Override
  public boolean isModified(@NotNull PerlModuleExtension originalExtension) {
    return isModifiedImpl(originalExtension);
  }
}
