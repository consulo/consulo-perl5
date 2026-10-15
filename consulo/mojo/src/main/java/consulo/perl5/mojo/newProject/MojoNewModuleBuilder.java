package consulo.perl5.mojo.newProject;

import com.perl5.lang.perl.idea.modules.newProject.PerlNewModuleBuilder;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.module.creation.scratch.NewModuleBuilder;
import consulo.module.creation.scratch.NewModuleContext;
import consulo.module.creation.scratch.NewModuleContextGroup;

/**
 * Mojolicious application, lite application and plugin templates of the Perl5 group of the "New Project/Module" wizard
 */
@ExtensionImpl
public class MojoNewModuleBuilder implements NewModuleBuilder {
  @Override
  public void setupContext(NewModuleContext context) {
    NewModuleContextGroup group = PerlNewModuleBuilder.addGroup(context);
    for (MojoNewProjectTemplate template : MojoNewProjectTemplate.values()) {
      group.add(LocalizeValue.of(template.getTitle()), template.getIcon(), PerlNewModuleBuilder.FRAMEWORK_TEMPLATE_WEIGHT,
                new MojoNewModuleBuilderProcessor(template));
    }
  }
}
