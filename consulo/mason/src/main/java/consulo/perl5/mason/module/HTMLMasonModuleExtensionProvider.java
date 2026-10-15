package consulo.perl5.mason.module;

import com.perl5.lang.htmlmason.HTMLMasonIcons;
import com.perl5.lang.htmlmason.HtmlMasonBundle;
import com.perl5.lang.perl.idea.modules.PerlModuleExtensionProvider;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.module.content.layer.ModuleExtensionProvider;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.extension.ModuleExtension;
import consulo.module.extension.MutableModuleExtension;
import consulo.ui.image.Image;

/**
 * Registers the HTML::Mason module extension as a child of the Perl module extension.
 * <p>
 * TODO consulo: upstream has no such extension, HTML::Mason support is available in all modules.
 */
@ExtensionImpl
public class HTMLMasonModuleExtensionProvider implements ModuleExtensionProvider<HTMLMasonModuleExtension> {
  public static final String ID = "perl5-htmlmason";

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
    return LocalizeValue.of(HtmlMasonBundle.message("html.mason"));
  }

  @Override
  public Image getIcon() {
    return HTMLMasonIcons.HTML_MASON_COMPONENT_ICON;
  }

  @Override
  public ModuleExtension<HTMLMasonModuleExtension> createImmutableExtension(ModuleRootLayer moduleRootLayer) {
    return new HTMLMasonModuleExtension(getId(), moduleRootLayer);
  }

  @Override
  public MutableModuleExtension<HTMLMasonModuleExtension> createMutableExtension(ModuleRootLayer moduleRootLayer) {
    return new HTMLMasonMutableModuleExtension(getId(), moduleRootLayer);
  }
}
