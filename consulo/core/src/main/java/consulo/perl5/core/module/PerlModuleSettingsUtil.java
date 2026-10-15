package consulo.perl5.core.module;

import consulo.application.ReadAction;
import consulo.ide.setting.ShowSettingsUtil;
import consulo.language.util.ModuleUtilCore;
import consulo.module.Module;
import consulo.module.ModuleManager;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.virtualFileSystem.VirtualFile;
import org.jspecify.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * Opens module settings (Project Structure) where the Perl interpreter (Perl module extension) and perl source roots (content folders)
 * are configured. Replaces upstream links to the Perl5 settings page, which had the project interpreter and the per-module roots editor.
 */
public final class PerlModuleSettingsUtil {
  /**
   * TODO consulo: there is no API constant for the title of the module extensions editor tab ({@code ExtensionEditor})
   */
  private static final String EXTENSIONS_TAB = "Extensions";

  private PerlModuleSettingsUtil() {
  }

  /**
   * Opens module extensions (interpreter) settings of the module of {@code file}, or of {@link #findModuleToConfigure(Project)} if
   * the file is not in a module
   */
  @RequiredUIAccess
  public static void openInterpreterSettings(Project project, @Nullable VirtualFile file) {
    Module module = findModule(project, file);
    openInterpreterSettings(project, module == null ? findModuleToConfigure(project) : module);
  }

  /**
   * Opens module extensions (interpreter) settings of the {@code module}, or the modules list if {@code module} is null
   */
  @RequiredUIAccess
  public static void openInterpreterSettings(Project project, @Nullable Module module) {
    openModuleSettings(project, module, EXTENSIONS_TAB, true);
  }

  /**
   * Same as {@link #openInterpreterSettings(Project, Module)}, for links on pages of the settings dialog: the page of the dialog is
   * selected, nothing happens if it is not possible
   */
  @RequiredUIAccess
  public static void selectInterpreterSettings(Project project, @Nullable Module module) {
    openModuleSettings(project, module, EXTENSIONS_TAB, false);
  }

  /**
   * Opens content entries (perl source roots) settings of the module of {@code file}
   */
  @RequiredUIAccess
  public static void openRootsSettings(Project project, @Nullable VirtualFile file) {
    Module module = findModule(project, file);
    // default tab of the module editor is the content entries one
    openModuleSettings(project, module == null ? findModuleToConfigure(project) : module, null, true);
  }

  /**
   * TODO consulo: unified (web) {@code ShowSettingsUtil} does not support the project structure selection (the returned future is
   *  failed immediately), the settings dialog is opened without selection instead, if {@code openSettingsIfUnsupported}
   */
  @RequiredUIAccess
  private static void openModuleSettings(Project project,
                                         @Nullable Module module,
                                         @Nullable String tabId,
                                         boolean openSettingsIfUnsupported) {
    if (project.isDisposed() || project.isDefault()) {
      return;
    }
    String moduleName = module == null || module.isDisposed() ? null : module.getName();
    ShowSettingsUtil showSettingsUtil = ShowSettingsUtil.getInstance();
    CompletableFuture<Void> result =
      showSettingsUtil.showProjectStructureDialog(project, selector -> selector.select(moduleName, tabId, true));
    if (openSettingsIfUnsupported && result.isCompletedExceptionally() && !showSettingsUtil.isAlreadyShown(project)) {
      showSettingsUtil.showSettingsDialog(project);
    }
  }

  private static @Nullable Module findModule(Project project, @Nullable VirtualFile file) {
    if (file == null) {
      return null;
    }
    return ReadAction.compute(() -> file.isValid() && !project.isDisposed() ? ModuleUtilCore.findModuleForFile(file, project) : null);
  }

  /**
   * @return a module to configure the interpreter in: the first module with enabled Perl module extension but without interpreter,
   * the first one with enabled Perl module extension or the first module of the project
   */
  public static @Nullable Module findModuleToConfigure(Project project) {
    if (project.isDisposed()) {
      return null;
    }
    Module result = null;
    for (Module module : PerlModuleExtensionUtil.getPerlModules(project)) {
      if (PerlModuleExtensionUtil.getSdk(module) == null) {
        return module;
      }
      if (result == null) {
        result = module;
      }
    }
    if (result != null) {
      return result;
    }
    Module[] modules = ModuleManager.getInstance(project).getModules();
    return modules.length == 0 ? null : modules[0];
  }
}
