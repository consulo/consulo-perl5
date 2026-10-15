package consulo.perl5.embedded.module;

import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.content.layer.extension.ModuleExtensionBase;

/**
 * Embedded Perl module extension, a child of the Perl module extension: marks modules using Embedded Perl templates.
 */
public class EmbeddedPerlModuleExtension extends ModuleExtensionBase<EmbeddedPerlModuleExtension> {
  public EmbeddedPerlModuleExtension(String id, ModuleRootLayer moduleRootLayer) {
    super(id, moduleRootLayer);
  }
}
