package consulo.perl5.tt2.module;

import com.perl5.lang.tt2.idea.settings.TemplateToolkitSettings;
import consulo.disposer.Disposable;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.extension.MutableModuleExtension;
import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;
import org.jspecify.annotations.Nullable;

public class TemplateToolkitMutableModuleExtension extends TemplateToolkitModuleExtension
  implements MutableModuleExtension<TemplateToolkitModuleExtension> {
  public TemplateToolkitMutableModuleExtension(String id, ModuleRootLayer moduleRootLayer) {
    super(id, moduleRootLayer);
  }

  public void setSettings(TemplateToolkitSettings settings) {
    mySettings = settings;
  }

  @RequiredUIAccess
  @Override
  public @Nullable Component createConfigurationComponent(Disposable uiDisposable, Runnable updateOnCheck) {
    return TemplateToolkitModuleExtensionPanel.create(this);
  }

  @Override
  public void setEnabled(boolean val) {
    myIsEnabled = val;
  }

  @Override
  public boolean isModified(TemplateToolkitModuleExtension originalExtension) {
    return myIsEnabled != originalExtension.isEnabled() || !mySettings.equals(originalExtension.getSettings());
  }
}
