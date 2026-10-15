package consulo.perl5.mason.module;

import com.perl5.lang.mason2.idea.configuration.Mason2SourceRootType;
import consulo.annotation.component.ExtensionImpl;
import consulo.perl5.core.module.PerlFrameworkContentFolderSupportPatcher;

/**
 * Allows Mason2 components roots in modules with the Mason2 module extension enabled
 */
@ExtensionImpl
public class Mason2ContentFolderSupportPatcher extends PerlFrameworkContentFolderSupportPatcher {
  public Mason2ContentFolderSupportPatcher() {
    super(Mason2ModuleExtension.class, Mason2SourceRootType.class);
  }
}
