package consulo.perl5.embedded.module;

import com.perl5.lang.embedded.EmbeddedPerlIcons;
import com.perl5.lang.embedded.EmbeddedPerlLanguage;
import com.perl5.lang.perl.idea.modules.PerlModuleExtensionProvider;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.module.content.layer.ModuleExtensionProvider;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.extension.ModuleExtension;
import consulo.module.extension.MutableModuleExtension;
import consulo.ui.image.Image;

/**
 * Registers the Embedded Perl module extension as a child of the Perl module extension.
 * <p>
 * TODO consulo: upstream has no such extension, Embedded Perl support is available in all modules.
 */
@ExtensionImpl
public class EmbeddedPerlModuleExtensionProvider implements ModuleExtensionProvider<EmbeddedPerlModuleExtension> {
  public static final String ID = "perl5-embedded";

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
    return LocalizeValue.of(EmbeddedPerlLanguage.NAME);
  }

  @Override
  public Image getIcon() {
    return EmbeddedPerlIcons.EMBEDDED_PERL_FILE;
  }

  @Override
  public ModuleExtension<EmbeddedPerlModuleExtension> createImmutableExtension(ModuleRootLayer moduleRootLayer) {
    return new EmbeddedPerlModuleExtension(getId(), moduleRootLayer);
  }

  @Override
  public MutableModuleExtension<EmbeddedPerlModuleExtension> createMutableExtension(ModuleRootLayer moduleRootLayer) {
    return new EmbeddedPerlMutableModuleExtension(getId(), moduleRootLayer);
  }
}
