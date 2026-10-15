package consulo.perl5.tt2.newProject;

import com.perl5.lang.perl.idea.modules.newProject.PerlFrameworkNewModuleBuilderProcessor;
import com.perl5.lang.perl.idea.modules.newProject.PerlNewModuleBuilder;
import com.perl5.lang.tt2.TemplateToolkitBundle;
import com.perl5.lang.tt2.TemplateToolkitIcons;
import com.perl5.lang.tt2.idea.settings.TemplateToolkitSourceRootType;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.module.creation.scratch.NewModuleBuilder;
import consulo.module.creation.scratch.NewModuleContext;
import consulo.perl5.tt2.module.TemplateToolkitMutableModuleExtension;

/**
 * Template Toolkit template of the Perl5 group of the "New Project/Module" wizard: Perl project with the Template Toolkit module
 * extension and a templates root
 */
@ExtensionImpl
public class TemplateToolkitNewModuleBuilder implements NewModuleBuilder {
  private static final String TEMPLATES_DIRECTORY_NAME = "templates";

  @Override
  public void setupContext(NewModuleContext context) {
    PerlNewModuleBuilder.addGroup(context).add(
      LocalizeValue.of(TemplateToolkitBundle.message("template.toolkit.2")), TemplateToolkitIcons.TTK2_ICON,
      PerlNewModuleBuilder.FRAMEWORK_TEMPLATE_WEIGHT,
      new PerlFrameworkNewModuleBuilderProcessor(TemplateToolkitMutableModuleExtension.class, TEMPLATES_DIRECTORY_NAME,
                                                 TemplateToolkitSourceRootType.INSTANCE));
  }
}
