package consulo.perl5.core.module;

import com.perl5.PerlBundle;
import com.perl5.lang.perl.idea.modules.PerlMutableModuleExtension;
import consulo.content.bundle.Sdk;
import consulo.disposer.Disposable;
import consulo.localize.LocalizeValue;
import consulo.module.Module;
import consulo.module.extension.MutableModuleExtensionWithSdk;
import consulo.module.extension.MutableModuleInheritableNamedPointer;
import consulo.module.ui.BundleBox;
import consulo.perl5.core.sdk.PerlAddInterpreterButton;
import consulo.project.localize.ProjectLocalize;
import consulo.ui.Component;
import consulo.ui.UIAccess;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.layout.DockLayout;
import consulo.ui.util.FormBuilder;

/**
 * Panel of the Perl module extension: perl interpreter selection, as {@code ModuleExtensionBundleBoxBuilder#createAndDefine} builds it,
 * with the "Add..." button creating interpreters like in the "New Project" wizard.
 */
public final class PerlModuleExtensionPanel {
  private PerlModuleExtensionPanel() {
  }

  @RequiredUIAccess
  public static Component create(PerlMutableModuleExtension extension, Disposable uiDisposable, Runnable updateOnCheck) {
    MutableModuleInheritableNamedPointer<Sdk> inheritableSdk = extension.getInheritableSdk();

    BundleBox box = BundleBox.builder(uiDisposable)
      .withNoneItem(ProjectLocalize.sdkComboBoxItem())
      .withSdkTypeFilterByClass(extension.getSdkTypeClass())
      .build();
    // Perl module extensions of other modules to inherit the interpreter from
    box.addModuleExtensionItems(extension, MutableModuleExtensionWithSdk::getInheritableSdk);

    if (inheritableSdk.isNull()) {
      box.setSelectedNoneBundle();
    }
    else {
      String sdkInheritModuleName = inheritableSdk.getModuleName();
      if (sdkInheritModuleName != null) {
        Module sdkInheritModule = inheritableSdk.getModule();
        if (sdkInheritModule == null) {
          box.addInvalidModuleItem(sdkInheritModuleName);
        }
        box.setSelectedModule(sdkInheritModuleName);
      }
      else {
        box.setSelectedBundle(inheritableSdk.getName());
      }
    }

    box.getComponent().addValueListener(event -> {
      inheritableSdk.set(box.getSelectedModuleName(), box.getSelectedBundleName());
      UIAccess.current().give(updateOnCheck);
    });

    Component addButton =
      PerlAddInterpreterButton.create(extension.getProject(), uiDisposable, sdk -> PerlAddInterpreterButton.selectCreatedSdk(box, sdk));

    return FormBuilder.create()
      .addLabeled(LocalizeValue.of(PerlBundle.message("perl.config.interpreter.title") + ":"),
                  DockLayout.create().center(box.getComponent()).right(addButton))
      .build();
  }
}
