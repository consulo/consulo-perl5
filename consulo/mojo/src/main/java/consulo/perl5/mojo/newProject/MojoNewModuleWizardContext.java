package consulo.perl5.mojo.newProject;

import com.perl5.lang.perl.idea.modules.newProject.PerlNewModuleWizardContext;
import org.jetbrains.annotations.NotNull;

/**
 * Wizard state of Mojolicious templates: the Perl one and the name of the generated application or plugin
 */
public class MojoNewModuleWizardContext extends PerlNewModuleWizardContext {
  private @NotNull String myEntityName = "";

  public MojoNewModuleWizardContext(boolean isNewProject) {
    super(isNewProject);
  }

  public @NotNull String getEntityName() {
    return myEntityName;
  }

  public void setEntityName(@NotNull String entityName) {
    myEntityName = entityName;
  }
}
