package consulo.perl5.embedded.module;

import consulo.disposer.Disposable;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.extension.MutableModuleExtension;
import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;
import org.jspecify.annotations.Nullable;

public class EmbeddedPerlMutableModuleExtension extends EmbeddedPerlModuleExtension
  implements MutableModuleExtension<EmbeddedPerlModuleExtension> {
  public EmbeddedPerlMutableModuleExtension(String id, ModuleRootLayer moduleRootLayer) {
    super(id, moduleRootLayer);
  }

  @RequiredUIAccess
  @Override
  public @Nullable Component createConfigurationComponent(Disposable uiDisposable, Runnable updateOnCheck) {
    return null;
  }

  @Override
  public void setEnabled(boolean val) {
    myIsEnabled = val;
  }

  @Override
  public boolean isModified(EmbeddedPerlModuleExtension originalExtension) {
    return myIsEnabled != originalExtension.isEnabled();
  }
}
