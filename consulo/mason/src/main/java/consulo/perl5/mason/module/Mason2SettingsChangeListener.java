package consulo.perl5.mason.module;

import com.perl5.lang.mason2.idea.configuration.MasonSettings;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.TopicImpl;
import consulo.fileEditor.EditorNotifications;
import consulo.module.Module;
import consulo.perl5.core.module.PerlFrameworkSettingsListener;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;
import jakarta.inject.Inject;

import java.util.Map;

/**
 * Handles changes of the per-module Mason2 settings, replaces upstream {@code MasonSettings#settingsUpdated()}: editor notifications are
 * updated. Caches of implicit variables are keyed by the settings instances, resolve caches are dropped by the roots change itself.
 */
@TopicImpl(ComponentScope.PROJECT)
public final class Mason2SettingsChangeListener extends PerlFrameworkSettingsListener<Mason2ModuleExtension, MasonSettings> {
  @Inject
  public Mason2SettingsChangeListener(Project project) {
    super(project, Mason2ModuleExtension.class, Mason2ModuleExtension::getSettings);
  }

  @RequiredUIAccess
  @Override
  protected void settingsChanged(Map<Module, MasonSettings> settingsBefore, Map<Module, MasonSettings> settingsAfter) {
    EditorNotifications.getInstance(myProject).updateAllNotifications();
  }
}
