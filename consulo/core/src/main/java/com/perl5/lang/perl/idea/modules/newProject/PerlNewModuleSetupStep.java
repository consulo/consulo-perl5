package com.perl5.lang.perl.idea.modules.newProject;

import com.perl5.PerlBundle;
import com.perl5.lang.perl.idea.sdk.PerlSdkType;
import consulo.content.bundle.Sdk;
import consulo.content.bundle.SdkTable;
import consulo.disposer.Disposable;
import consulo.localize.LocalizeValue;
import consulo.module.creation.ui.UnifiedProjectOrModuleNameStep;
import consulo.module.ui.BundleBox;
import consulo.module.ui.BundleBoxBuilder;
import consulo.perl5.core.sdk.PerlAddInterpreterButton;
import consulo.ui.Button;
import consulo.ui.ComboBox;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.layout.DockLayout;
import consulo.ui.util.FormBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Name/location step with perl interpreter selection. Interpreters are created with the same host/version manager
 * handlers as in Settings | Languages | Perl5 ({@code Perl5SdkConfigurable}).
 */
public class PerlNewModuleSetupStep<C extends PerlNewModuleWizardContext> extends UnifiedProjectOrModuleNameStep<C> {
  private BundleBox myBundleBox;

  public PerlNewModuleSetupStep(@NotNull C context) {
    super(context);
  }

  @RequiredUIAccess
  @Override
  protected void extend(@NotNull FormBuilder builder, @NotNull Disposable uiDisposable) {
    super.extend(builder, uiDisposable);

    BundleBoxBuilder boxBuilder = BundleBoxBuilder.create(uiDisposable);
    boxBuilder.withSdkTypeFilterByType(PerlSdkType.getInstance());
    myBundleBox = boxBuilder.build();

    ComboBox<BundleBox.BundleBoxItem> component = myBundleBox.getComponent();
    if (component.getDataModel().getSize() > 0) {
      component.setValueByIndex(0);
    }

    Button addButton = PerlAddInterpreterButton.create(null, uiDisposable, sdk -> PerlAddInterpreterButton.selectCreatedSdk(myBundleBox, sdk));

    builder.addLabeled(LocalizeValue.of(PerlBundle.message("perl.config.interpreter.title") + ":"),
                       DockLayout.create().center(component).right(addButton));
  }

  @Override
  public void onStepLeave(@NotNull C context) {
    super.onStepLeave(context);

    context.setSdk(getSelectedSdk());
  }

  protected @Nullable Sdk getSelectedSdk() {
    String selectedBundleName = myBundleBox.getSelectedBundleName();
    return selectedBundleName == null ? null : SdkTable.getInstance().findSdk(selectedBundleName);
  }
}
