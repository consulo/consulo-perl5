package consulo.perl5.mojo.newProject;

import com.perl5.lang.perl.idea.modules.newProject.PerlNewModuleBuilderProcessor;
import consulo.annotation.access.RequiredReadAction;
import consulo.module.content.layer.ContentEntry;
import consulo.module.content.layer.ModifiableRootModel;
import consulo.perl5.mojo.module.MojoMutableModuleExtension;
import consulo.project.startup.StartupManager;
import consulo.ui.ex.wizard.WizardStep;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Mojolicious template: Perl module with the Mojolicious module extension; the application or plugin is generated with
 * {@code mojo generate} when the project is opened
 */
public class MojoNewModuleBuilderProcessor extends PerlNewModuleBuilderProcessor<MojoNewModuleWizardContext> {
  private final MojoNewProjectTemplate myTemplate;

  public MojoNewModuleBuilderProcessor(@NotNull MojoNewProjectTemplate template) {
    myTemplate = template;
  }

  @Override
  public @NotNull MojoNewModuleWizardContext createContext(boolean isNewProject) {
    return new MojoNewModuleWizardContext(isNewProject);
  }

  @Override
  public void buildSteps(@NotNull Consumer<WizardStep<MojoNewModuleWizardContext>> consumer, @NotNull MojoNewModuleWizardContext context) {
    consumer.accept(new MojoNewModuleSetupStep(context, myTemplate));
  }

  @RequiredReadAction
  @Override
  protected void configureModule(@NotNull MojoNewModuleWizardContext context,
                                 @NotNull ContentEntry contentEntry,
                                 @NotNull ModifiableRootModel modifiableRootModel) {
    enableExtension(modifiableRootModel, MojoMutableModuleExtension.class);

    // the root model is committed after this, the interpreter is known to the project when it is opened
    Path root = Path.of(VirtualFileUtil.urlToPath(contentEntry.getUrl()));
    String name = context.getEntityName();
    StartupManager.getInstance(modifiableRootModel.getProject()).runAfterOpened(
      (project, uiAccess) -> new MojoNewProjectGenerator(project, uiAccess, myTemplate, name, root).generate(false));
  }
}
