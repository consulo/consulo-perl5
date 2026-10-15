package consulo.perl5.mason.module;

import com.perl5.lang.htmlmason.idea.configuration.HTMLMasonSourceRootType;
import consulo.annotation.component.ExtensionImpl;
import consulo.perl5.core.module.PerlFrameworkContentFolderSupportPatcher;

/**
 * Allows HTML::Mason components roots in modules with the HTML::Mason module extension enabled
 */
@ExtensionImpl
public class HTMLMasonContentFolderSupportPatcher extends PerlFrameworkContentFolderSupportPatcher {
  public HTMLMasonContentFolderSupportPatcher() {
    super(HTMLMasonModuleExtension.class, HTMLMasonSourceRootType.class);
  }
}
