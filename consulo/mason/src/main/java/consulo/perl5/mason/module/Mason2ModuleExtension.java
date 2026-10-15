package consulo.perl5.mason.module;

import com.perl5.lang.mason2.idea.configuration.MasonSettings;
import consulo.annotation.access.RequiredReadAction;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.content.layer.extension.ModuleExtensionBase;
import consulo.perl5.core.module.PerlModuleExtensionStateUtil;
import org.jdom.Element;

/**
 * Mason2 module extension, a child of the Perl module extension: enables Mason2 components roots, its state is the Mason2 settings of the
 * module ({@link MasonSettings}).
 */
public class Mason2ModuleExtension extends ModuleExtensionBase<Mason2ModuleExtension> {
  private static final String AUTOBASE_NAMES = "autobase-names";
  private static final String NAME = "name";

  protected MasonSettings mySettings = MasonSettings.DEFAULT;

  public Mason2ModuleExtension(String id, ModuleRootLayer moduleRootLayer) {
    super(id, moduleRootLayer);
  }

  public MasonSettings getSettings() {
    return mySettings;
  }

  @RequiredReadAction
  @Override
  public void commit(Mason2ModuleExtension mutableModuleExtension) {
    super.commit(mutableModuleExtension);
    mySettings = mutableModuleExtension.getSettings();
  }

  @Override
  protected void getStateImpl(Element element) {
    MasonSettings defaults = MasonSettings.DEFAULT;
    PerlModuleExtensionStateUtil.writeStrings(element, AUTOBASE_NAMES, NAME, mySettings.getAutobaseNames(), defaults.getAutobaseNames());
    MasonModuleExtensionStateUtil.writeGlobalVariables(element, mySettings.getGlobalVariables(), defaults.getGlobalVariables());
  }

  @RequiredReadAction
  @Override
  protected void loadStateImpl(Element element) {
    MasonSettings defaults = MasonSettings.DEFAULT;
    mySettings = new MasonSettings(
      PerlModuleExtensionStateUtil.readStrings(element, AUTOBASE_NAMES, NAME, defaults.getAutobaseNames()),
      MasonModuleExtensionStateUtil.readGlobalVariables(element, defaults.getGlobalVariables()));
  }
}
