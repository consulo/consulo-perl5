package consulo.perl5.mojo.module;

import com.perl5.lang.mojolicious.idea.modules.MojoTemplateSourceRootType;
import consulo.annotation.component.ExtensionImpl;
import consulo.perl5.core.module.PerlFrameworkContentFolderSupportPatcher;

/**
 * Allows Mojolicious templates roots in modules with the Mojolicious module extension enabled
 */
@ExtensionImpl
public class MojoContentFolderSupportPatcher extends PerlFrameworkContentFolderSupportPatcher {
  public MojoContentFolderSupportPatcher() {
    super(MojoModuleExtension.class, MojoTemplateSourceRootType.class);
  }
}
