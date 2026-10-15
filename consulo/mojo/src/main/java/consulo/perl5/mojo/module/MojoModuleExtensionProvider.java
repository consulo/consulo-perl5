package consulo.perl5.mojo.module;

import com.perl5.lang.mojolicious.MojoIcons;
import com.perl5.lang.perl.idea.modules.PerlModuleExtensionProvider;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.module.content.layer.ModuleExtensionProvider;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.extension.ModuleExtension;
import consulo.module.extension.MutableModuleExtension;
import consulo.ui.image.Image;

/**
 * Registers the Mojolicious module extension as a child of the Perl module extension.
 * <p>
 * TODO consulo: upstream has no such extension, Mojolicious support is available in all modules.
 */
@ExtensionImpl
public class MojoModuleExtensionProvider implements ModuleExtensionProvider<MojoModuleExtension> {
  public static final String ID = "perl5-mojo";

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public String getParentId() {
    return PerlModuleExtensionProvider.ID;
  }

  @Override
  public LocalizeValue getName() {
    return LocalizeValue.localizeTODO("Mojolicious");
  }

  @Override
  public Image getIcon() {
    return MojoIcons.MOJO_LOGO;
  }

  @Override
  public ModuleExtension<MojoModuleExtension> createImmutableExtension(ModuleRootLayer moduleRootLayer) {
    return new MojoModuleExtension(getId(), moduleRootLayer);
  }

  @Override
  public MutableModuleExtension<MojoModuleExtension> createMutableExtension(ModuleRootLayer moduleRootLayer) {
    return new MojoMutableModuleExtension(getId(), moduleRootLayer);
  }
}
