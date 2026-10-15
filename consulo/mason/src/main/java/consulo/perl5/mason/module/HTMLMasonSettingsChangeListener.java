package consulo.perl5.mason.module;

import com.perl5.lang.htmlmason.idea.configuration.HTMLMasonSettings;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.TopicImpl;
import consulo.application.WriteAction;
import consulo.fileEditor.EditorNotifications;
// TODO consulo: no public API to fire file types change, FileTypeManagerEx is a qualified export
import consulo.ide.impl.idea.openapi.fileTypes.impl.FileTypeManagerImpl;
import consulo.language.file.FileTypeManager;
import consulo.module.Module;
import consulo.perl5.core.module.PerlFrameworkSettingsListener;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;
import jakarta.inject.Inject;

import java.util.Map;

/**
 * Handles changes of the per-module HTML::Mason settings, replaces upstream {@code HTMLMasonSettings#settingsUpdated()} and the apply of
 * {@code HTMLMasonSettingsConfigurable}: editor notifications are updated, file types are re-detected (and files reparsed) if the
 * settings which affect them (handlers names, substituted extensions) or the structure of components (custom tags) have changed.
 */
@TopicImpl(ComponentScope.PROJECT)
public final class HTMLMasonSettingsChangeListener extends PerlFrameworkSettingsListener<HTMLMasonModuleExtension, HTMLMasonSettings> {
  @Inject
  public HTMLMasonSettingsChangeListener(Project project) {
    super(project, HTMLMasonModuleExtension.class, HTMLMasonModuleExtension::getSettings);
  }

  @RequiredUIAccess
  @Override
  protected void settingsChanged(Map<Module, HTMLMasonSettings> settingsBefore, Map<Module, HTMLMasonSettings> settingsAfter) {
    EditorNotifications.getInstance(myProject).updateAllNotifications();
    if (isChanged(settingsBefore, settingsAfter, HTMLMasonSettings::getAutoHandlerName) ||
        isChanged(settingsBefore, settingsAfter, HTMLMasonSettings::getDefaultHandlerName) ||
        isChanged(settingsBefore, settingsAfter, HTMLMasonSettings::getSubstitutedExtensions) ||
        isChanged(settingsBefore, settingsAfter, HTMLMasonSettings::getCustomTags)) {
      WriteAction.run(() -> ((FileTypeManagerImpl)FileTypeManager.getInstance()).fireFileTypesChanged());
    }
  }
}
