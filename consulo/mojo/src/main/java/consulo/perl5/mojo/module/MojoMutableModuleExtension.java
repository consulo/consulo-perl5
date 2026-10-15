package consulo.perl5.mojo.module;

import consulo.disposer.Disposable;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.extension.MutableModuleExtension;
import consulo.ui.Component;
import consulo.ui.annotation.RequiredUIAccess;
import org.jspecify.annotations.Nullable;

public class MojoMutableModuleExtension extends MojoModuleExtension
  implements MutableModuleExtension<MojoModuleExtension> {
  public MojoMutableModuleExtension(String id, ModuleRootLayer moduleRootLayer) {
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
  public boolean isModified(MojoModuleExtension originalExtension) {
    return myIsEnabled != originalExtension.isEnabled();
  }
}
