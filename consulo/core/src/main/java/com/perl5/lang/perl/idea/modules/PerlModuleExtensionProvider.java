package com.perl5.lang.perl.idea.modules;

import com.intellij.openapi.projectRoots.impl.PerlModuleExtension;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.module.content.layer.ModuleExtensionProvider;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.extension.ModuleExtension;
import consulo.module.extension.MutableModuleExtension;
import consulo.ui.image.Image;
import org.jetbrains.annotations.NotNull;

/**
 * Registers the Perl module extension. Replaces upstream {@code moduleType} (PERL5_MODULE) and {@code moduleExtension} registrations.
 */
@ExtensionImpl
public class PerlModuleExtensionProvider implements ModuleExtensionProvider<PerlModuleExtension> {
  public static final String ID = "perl5";

  @Override
  public @NotNull String getId() {
    return ID;
  }

  @Override
  public @NotNull LocalizeValue getName() {
    return LocalizeValue.of(PerlModuleType.getInstance().getName());
  }

  @Override
  public @NotNull Image getIcon() {
    return PerlModuleType.getInstance().getNodeIcon(false);
  }

  @Override
  public @NotNull ModuleExtension<PerlModuleExtension> createImmutableExtension(@NotNull ModuleRootLayer moduleRootLayer) {
    return new PerlModuleExtension(getId(), moduleRootLayer);
  }

  @Override
  public @NotNull MutableModuleExtension<PerlModuleExtension> createMutableExtension(@NotNull ModuleRootLayer moduleRootLayer) {
    return new PerlMutableModuleExtension(getId(), moduleRootLayer);
  }
}
