package consulo.perl5.tt2.module;

import com.perl5.lang.perl.idea.modules.PerlModuleExtensionProvider;
import com.perl5.lang.tt2.TemplateToolkitBundle;
import com.perl5.lang.tt2.TemplateToolkitIcons;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.module.content.layer.ModuleExtensionProvider;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.extension.ModuleExtension;
import consulo.module.extension.MutableModuleExtension;
import consulo.ui.image.Image;

/**
 * Registers the Template Toolkit module extension as a child of the Perl module extension.
 * <p>
 * TODO consulo: upstream has no such extension, Template Toolkit support is available in all modules.
 */
@ExtensionImpl
public class TemplateToolkitModuleExtensionProvider implements ModuleExtensionProvider<TemplateToolkitModuleExtension> {
  public static final String ID = "perl5-tt2";

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
    return LocalizeValue.of(TemplateToolkitBundle.message("template.toolkit.2"));
  }

  @Override
  public Image getIcon() {
    return TemplateToolkitIcons.TTK2_ICON;
  }

  @Override
  public ModuleExtension<TemplateToolkitModuleExtension> createImmutableExtension(ModuleRootLayer moduleRootLayer) {
    return new TemplateToolkitModuleExtension(getId(), moduleRootLayer);
  }

  @Override
  public MutableModuleExtension<TemplateToolkitModuleExtension> createMutableExtension(ModuleRootLayer moduleRootLayer) {
    return new TemplateToolkitMutableModuleExtension(getId(), moduleRootLayer);
  }
}
