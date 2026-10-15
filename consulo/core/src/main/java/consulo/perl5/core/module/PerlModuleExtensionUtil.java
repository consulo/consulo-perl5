package consulo.perl5.core.module;

import com.intellij.openapi.projectRoots.impl.PerlModuleExtension;
import com.perl5.lang.perl.idea.modules.PerlMutableModuleExtension;
import consulo.annotation.access.RequiredReadAction;
import consulo.application.WriteAction;
import consulo.content.bundle.Sdk;
import consulo.module.Module;
import consulo.module.ModuleManager;
import consulo.module.content.ModifiableModelCommitter;
import consulo.module.content.ModuleRootManager;
import consulo.module.content.layer.ModifiableRootModel;
import consulo.module.content.layer.orderEntry.ModuleExtensionWithSdkOrderEntry;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.util.lang.StringUtil;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Perl interpreter of the project in terms of the platform module model: every module has its own interpreter, the SDK of its enabled
 * Perl module extension ({@link PerlModuleExtension}). Upstream keeps a single project interpreter in {@code PerlLocalSettings}.
 */
public final class PerlModuleExtensionUtil {
  private PerlModuleExtensionUtil() {
  }

  /**
   * @return enabled Perl module extension of the {@code module} or null if Perl is not enabled for the module
   */
  public static @Nullable PerlModuleExtension getEnabledExtension(Module module) {
    if (module.isDisposed()) {
      return null;
    }
    return ModuleRootManager.getInstance(module).getExtension(PerlModuleExtension.class);
  }

  /**
   * @return interpreter of the enabled Perl module extension of the {@code module}
   */
  public static @Nullable Sdk getSdk(Module module) {
    PerlModuleExtension extension = getEnabledExtension(module);
    return extension == null ? null : extension.getSdk();
  }

  /**
   * @return modules with enabled Perl module extension, in the {@link ModuleManager#getModules()} order
   */
  public static List<Module> getPerlModules(Project project) {
    List<Module> result = new ArrayList<>();
    if (project.isDisposed()) {
      return result;
    }
    for (Module module : ModuleManager.getInstance(project).getModules()) {
      if (getEnabledExtension(module) != null) {
        result.add(module);
      }
    }
    return result;
  }

  /**
   * @return interpreter of the first module with enabled Perl module extension and configured interpreter
   */
  public static @Nullable Sdk findFirstSdk(Project project) {
    for (Module module : getPerlModules(project)) {
      Sdk sdk = getSdk(module);
      if (sdk != null) {
        return sdk;
      }
    }
    return null;
  }

  /**
   * @return true iff some enabled Perl module extension of the {@code project} refers to the interpreter with {@code sdkName}
   */
  public static boolean isUsed(Project project, String sdkName) {
    for (Module module : getPerlModules(project)) {
      PerlModuleExtension extension = getEnabledExtension(module);
      if (extension != null && StringUtil.equals(sdkName, extension.getSdkName())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Sets the {@code sdk} for all modules with enabled Perl module extension. If there are no such modules and {@code sdk} is not null,
   * Perl module extension is enabled for all modules of the project.
   */
  @RequiredUIAccess
  @RequiredReadAction
  public static void setSdk(Project project, @Nullable Sdk sdk) {
    if (project.isDisposed() || project.isDefault()) {
      return;
    }
    List<Module> modules = getPerlModules(project);
    boolean enable = false;
    if (modules.isEmpty()) {
      if (sdk == null) {
        return;
      }
      modules = List.of(ModuleManager.getInstance(project).getModules());
      enable = true;
    }

    List<ModifiableRootModel> models = new ArrayList<>();
    for (Module module : modules) {
      ModifiableRootModel model = ModuleRootManager.getInstance(module).getModifiableModel();
      PerlMutableModuleExtension extension = model.getExtensionWithoutCheck(PerlMutableModuleExtension.class);
      if (extension == null) {
        model.dispose();
        continue;
      }
      if (enable) {
        extension.setEnabled(true);
      }
      extension.getInheritableSdk().set(null, sdk);
      updateSdkOrderEntry(model, extension);
      models.add(model);
    }
    if (models.isEmpty()) {
      return;
    }
    // unchanged models are disposed by the committer
    WriteAction.run(() -> ModifiableModelCommitter.getInstance(project).multiCommit(
      models.toArray(new ModifiableRootModel[0]), ModuleManager.getInstance(project).getModifiableModel()));
  }

  /**
   * Same as the module extensions editor of the platform does: the sdk order entry exists iff the interpreter is set
   */
  private static void updateSdkOrderEntry(ModifiableRootModel model, PerlMutableModuleExtension extension) {
    ModuleExtensionWithSdkOrderEntry sdkOrderEntry = model.findModuleExtensionSdkEntry(extension);
    if (extension.getInheritableSdk().isNull()) {
      if (sdkOrderEntry != null) {
        model.removeOrderEntry(sdkOrderEntry);
      }
    }
    else if (sdkOrderEntry == null) {
      model.addModuleExtensionSdkEntry(extension);
    }
  }
}
