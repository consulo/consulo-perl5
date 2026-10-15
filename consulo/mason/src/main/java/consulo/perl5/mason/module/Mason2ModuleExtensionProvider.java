package consulo.perl5.mason.module;

import com.perl5.lang.mason2.Mason2Bundle;
import com.perl5.lang.mason2.Mason2Icons;
import com.perl5.lang.perl.idea.modules.PerlModuleExtensionProvider;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.module.content.layer.ModuleExtensionProvider;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.extension.ModuleExtension;
import consulo.module.extension.MutableModuleExtension;
import consulo.ui.image.Image;

/**
 * Registers the Mason2 module extension as a child of the Perl module extension.
 * <p>
 * TODO consulo: upstream has no such extension, Mason2 support is available in all modules.
 */
@ExtensionImpl
public class Mason2ModuleExtensionProvider implements ModuleExtensionProvider<Mason2ModuleExtension> {
  public static final String ID = "perl5-mason2";

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
    return LocalizeValue.of(Mason2Bundle.message("mason2"));
  }

  @Override
  public Image getIcon() {
    return Mason2Icons.MASON_TOP_LEVEL_COMPONENT_ICON;
  }

  @Override
  public ModuleExtension<Mason2ModuleExtension> createImmutableExtension(ModuleRootLayer moduleRootLayer) {
    return new Mason2ModuleExtension(getId(), moduleRootLayer);
  }

  @Override
  public MutableModuleExtension<Mason2ModuleExtension> createMutableExtension(ModuleRootLayer moduleRootLayer) {
    return new Mason2MutableModuleExtension(getId(), moduleRootLayer);
  }
}
