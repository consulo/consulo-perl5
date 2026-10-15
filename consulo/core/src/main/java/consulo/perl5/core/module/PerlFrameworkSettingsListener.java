package consulo.perl5.core.module;

import consulo.application.Application;
import consulo.fileEditor.util.FileContentUtil;
import consulo.module.Module;
import consulo.module.ModuleManager;
import consulo.module.content.ModuleRootManager;
import consulo.module.content.layer.event.ModuleRootEvent;
import consulo.module.content.layer.event.ModuleRootListener;
import consulo.module.extension.ModuleExtension;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Base of project listeners of the per-module settings of a Perl framework (Template Toolkit, Mason): the settings are the state of the
 * framework module extension, a child of the Perl module extension. Module extensions are a part of module root models, so their changes
 * are committed with a roots change: the settings of the modules with the extension enabled are captured before a roots change and
 * compared after it.
 * <p>
 * Replaces notifications of upstream project-level settings services, which were fired when their page of the Perl5 settings was applied.
 *
 * @param <S> settings, compared with {@link Object#equals(Object)}
 */
public abstract class PerlFrameworkSettingsListener<E extends ModuleExtension<E>, S> implements ModuleRootListener {
  protected final Project myProject;
  private final Class<E> myExtensionClass;
  private final Function<E, S> mySettingsGetter;
  private @Nullable Map<Module, S> mySettingsBeforeChange;

  protected PerlFrameworkSettingsListener(Project project, Class<E> extensionClass, Function<E, S> settingsGetter) {
    myProject = project;
    myExtensionClass = extensionClass;
    mySettingsGetter = settingsGetter;
  }

  @Override
  public void beforeRootsChange(ModuleRootEvent event) {
    if (mySettingsBeforeChange == null && !myProject.isDisposed()) {
      mySettingsBeforeChange = collectSettings();
    }
  }

  @Override
  public void rootsChanged(ModuleRootEvent event) {
    Map<Module, S> settingsBefore = mySettingsBeforeChange;
    mySettingsBeforeChange = null;
    if (settingsBefore == null || myProject.isDisposed()) {
      return;
    }
    Map<Module, S> settingsAfter = collectSettings();
    if (!settingsBefore.equals(settingsAfter)) {
      // the event is fired in a write action, in the middle of the commit of the modules models
      Application.get().invokeLater(() -> settingsChanged(settingsBefore, settingsAfter), myProject.getDisposed());
    }
  }

  /**
   * Invoked when the settings of some module have changed or the framework module extension has been enabled or disabled for a module
   *
   * @param settingsBefore settings of the modules with the extension enabled before the change
   * @param settingsAfter  settings of the modules with the extension enabled after the change
   */
  @RequiredUIAccess
  protected abstract void settingsChanged(Map<Module, S> settingsBefore, Map<Module, S> settingsAfter);

  /**
   * Recreates psi and editor highlighters of the files opened in editors: lexers of the framework files read the settings when created
   */
  @RequiredUIAccess
  protected void reparseOpenFiles() {
    FileContentUtil.reparseFiles(myProject, List.of(), true);
  }

  /**
   * @return true iff the {@code part} of the settings differs for some module
   */
  protected static <S> boolean isChanged(Map<Module, S> settingsBefore, Map<Module, S> settingsAfter, Function<S, ?> part) {
    if (!settingsBefore.keySet().equals(settingsAfter.keySet())) {
      return true;
    }
    for (Map.Entry<Module, S> entry : settingsBefore.entrySet()) {
      S settingAfter = settingsAfter.get(entry.getKey());
      if (settingAfter == null || !Objects.equals(part.apply(entry.getValue()), part.apply(settingAfter))) {
        return true;
      }
    }
    return false;
  }

  private Map<Module, S> collectSettings() {
    Map<Module, S> result = new HashMap<>();
    for (Module module : ModuleManager.getInstance(myProject).getModules()) {
      if (module.isDisposed()) {
        continue;
      }
      E extension = ModuleRootManager.getInstance(module).getExtension(myExtensionClass);
      if (extension != null) {
        result.put(module, mySettingsGetter.apply(extension));
      }
    }
    return result;
  }
}
