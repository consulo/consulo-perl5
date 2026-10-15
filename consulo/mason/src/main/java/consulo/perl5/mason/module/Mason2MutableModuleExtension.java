package consulo.perl5.mason.module;

import com.perl5.lang.mason2.idea.configuration.MasonSettings;
import consulo.disposer.Disposable;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.extension.MutableModuleExtension;
import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;
import org.jspecify.annotations.Nullable;

public class Mason2MutableModuleExtension extends Mason2ModuleExtension
  implements MutableModuleExtension<Mason2ModuleExtension> {
  public Mason2MutableModuleExtension(String id, ModuleRootLayer moduleRootLayer) {
    super(id, moduleRootLayer);
  }

  public void setSettings(MasonSettings settings) {
    mySettings = settings;
  }

  @RequiredUIAccess
  @Override
  public @Nullable Component createConfigurationComponent(Disposable uiDisposable, Runnable updateOnCheck) {
    return Mason2ModuleExtensionPanel.create(this);
  }

  @Override
  public void setEnabled(boolean val) {
    myIsEnabled = val;
  }

  @Override
  public boolean isModified(Mason2ModuleExtension originalExtension) {
    return myIsEnabled != originalExtension.isEnabled() || !mySettings.equals(originalExtension.getSettings());
  }
}
