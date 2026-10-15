package consulo.perl5.mason.module;

import com.perl5.lang.htmlmason.idea.configuration.HTMLMasonSettings;
import consulo.disposer.Disposable;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.extension.MutableModuleExtension;
import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;
import org.jspecify.annotations.Nullable;

public class HTMLMasonMutableModuleExtension extends HTMLMasonModuleExtension
  implements MutableModuleExtension<HTMLMasonModuleExtension> {
  public HTMLMasonMutableModuleExtension(String id, ModuleRootLayer moduleRootLayer) {
    super(id, moduleRootLayer);
  }

  public void setSettings(HTMLMasonSettings settings) {
    mySettings = settings;
  }

  @RequiredUIAccess
  @Override
  public @Nullable Component createConfigurationComponent(Disposable uiDisposable, Runnable updateOnCheck) {
    return HTMLMasonModuleExtensionPanel.create(this);
  }

  @Override
  public void setEnabled(boolean val) {
    myIsEnabled = val;
  }

  @Override
  public boolean isModified(HTMLMasonModuleExtension originalExtension) {
    return myIsEnabled != originalExtension.isEnabled() || !mySettings.equals(originalExtension.getSettings());
  }
}
