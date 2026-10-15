package consulo.perl5.mojo.newProject;

import com.perl5.lang.perl.idea.modules.newProject.PerlNewModuleSetupStep;
import consulo.disposer.Disposable;
import consulo.localize.LocalizeValue;
import consulo.ui.TextBox;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.wizard.WizardStepValidationException;
import consulo.ui.util.FormBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/**
 * Perl step with the name of the generated application or plugin. The generation needs an interpreter with Mojolicious.
 * <p>
 * TODO consulo: the "New Project/Module" dialog does not call {@link #validateStep} yet, the generation reports and skips an invalid
 *  name
 */
public class MojoNewModuleSetupStep extends PerlNewModuleSetupStep<MojoNewModuleWizardContext> {
  private final MojoNewProjectTemplate myTemplate;
  private TextBox myNameBox;

  public MojoNewModuleSetupStep(@NotNull MojoNewModuleWizardContext context, @NotNull MojoNewProjectTemplate template) {
    super(context);
    myTemplate = template;
  }

  @RequiredUIAccess
  @Override
  protected void extend(@NotNull FormBuilder builder, @NotNull Disposable uiDisposable) {
    super.extend(builder, uiDisposable);

    myNameBox = TextBox.create(myTemplate.getDefaultName());
    builder.addLabeled(LocalizeValue.of(myTemplate.getNamePrompt() + ":"), myNameBox);
  }

  @RequiredUIAccess
  @Override
  public CompletableFuture<?> validateStep(@NotNull MojoNewModuleWizardContext context) {
    if (getSelectedSdk() == null) {
      return CompletableFuture.failedFuture(new WizardStepValidationException(
        LocalizeValue.localizeTODO("A Perl interpreter is required to generate the " + myTemplate.getTitle())));
    }
    if (!myTemplate.isValidName(getEntityName())) {
      return CompletableFuture.failedFuture(new WizardStepValidationException(myNameBox, getInvalidNameMessage(getEntityName())));
    }
    return super.validateStep(context);
  }

  @Override
  public void onStepLeave(@NotNull MojoNewModuleWizardContext context) {
    super.onStepLeave(context);

    context.setEntityName(getEntityName());
  }

  private @NotNull LocalizeValue getInvalidNameMessage(@NotNull String name) {
    return LocalizeValue.localizeTODO("Invalid " + myTemplate.getNamePrompt().toLowerCase() + ": " + name);
  }

  private @NotNull String getEntityName() {
    String name = myNameBox.getValue();
    return name == null ? "" : name.trim();
  }
}
