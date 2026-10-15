package consulo.perl5.embedded.newProject;

import com.perl5.lang.embedded.EmbeddedPerlIcons;
import com.perl5.lang.embedded.EmbeddedPerlLanguage;
import com.perl5.lang.perl.idea.modules.newProject.PerlFrameworkNewModuleBuilderProcessor;
import com.perl5.lang.perl.idea.modules.newProject.PerlNewModuleBuilder;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.module.creation.scratch.NewModuleBuilder;
import consulo.module.creation.scratch.NewModuleContext;
import consulo.perl5.embedded.module.EmbeddedPerlMutableModuleExtension;

/**
 * Embedded Perl template of the Perl5 group of the "New Project/Module" wizard: Perl project with the Embedded Perl module extension
 */
@ExtensionImpl
public class EmbeddedPerlNewModuleBuilder implements NewModuleBuilder {
  @Override
  public void setupContext(NewModuleContext context) {
    PerlNewModuleBuilder.addGroup(context).add(
      LocalizeValue.of(EmbeddedPerlLanguage.NAME), EmbeddedPerlIcons.EMBEDDED_PERL_FILE, PerlNewModuleBuilder.FRAMEWORK_TEMPLATE_WEIGHT,
      new PerlFrameworkNewModuleBuilderProcessor(EmbeddedPerlMutableModuleExtension.class));
  }
}
