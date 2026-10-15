package consulo.perl5.tt2.module;

import com.perl5.lang.tt2.idea.settings.TemplateToolkitSourceRootType;
import consulo.annotation.component.ExtensionImpl;
import consulo.perl5.core.module.PerlFrameworkContentFolderSupportPatcher;

/**
 * Allows Template Toolkit templates roots in modules with the Template Toolkit module extension enabled
 */
@ExtensionImpl
public class TemplateToolkitContentFolderSupportPatcher extends PerlFrameworkContentFolderSupportPatcher {
  public TemplateToolkitContentFolderSupportPatcher() {
    super(TemplateToolkitModuleExtension.class, TemplateToolkitSourceRootType.class);
  }
}
