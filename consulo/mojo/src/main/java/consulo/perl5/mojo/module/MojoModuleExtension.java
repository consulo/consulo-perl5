package consulo.perl5.mojo.module;

import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.content.layer.extension.ModuleExtensionBase;
import consulo.module.extension.ModuleExtensionHelper;
import consulo.project.Project;

/**
 * Mojolicious module extension, a child of the Perl module extension: enables Mojolicious templates roots and Mojolicious
 * applications/plugins detection.
 */
public class MojoModuleExtension extends ModuleExtensionBase<MojoModuleExtension> {
  public MojoModuleExtension(String id, ModuleRootLayer moduleRootLayer) {
    super(id, moduleRootLayer);
  }

  /**
   * @return true iff the extension is enabled for some module of the {@code project}
   */
  public static boolean isEnabled(Project project) {
    return !project.isDisposed() && ModuleExtensionHelper.getInstance(project).hasModuleExtension(MojoModuleExtension.class);
  }
}
