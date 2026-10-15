package consulo.perl5.tt2.module;

import com.perl5.lang.tt2.idea.settings.TemplateToolkitSettings;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.TopicImpl;
import consulo.module.Module;
import consulo.perl5.core.module.PerlFrameworkSettingsListener;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;
import jakarta.inject.Inject;

import java.util.Map;

/**
 * Handles changes of the per-module Template Toolkit settings, replaces upstream {@code TemplateToolkitSettings#settingsUpdated()}: open
 * files are reparsed, their lexers (and editor highlighters) read the tags of the module settings. Templates roots caches and the file
 * types (substituted extensions) are reset by the roots change itself.
 */
@TopicImpl(ComponentScope.PROJECT)
public final class TemplateToolkitSettingsChangeListener
  extends PerlFrameworkSettingsListener<TemplateToolkitModuleExtension, TemplateToolkitSettings> {
  @Inject
  public TemplateToolkitSettingsChangeListener(Project project) {
    super(project, TemplateToolkitModuleExtension.class, TemplateToolkitModuleExtension::getSettings);
  }

  @RequiredUIAccess
  @Override
  protected void settingsChanged(Map<Module, TemplateToolkitSettings> settingsBefore, Map<Module, TemplateToolkitSettings> settingsAfter) {
    reparseOpenFiles();
  }
}
