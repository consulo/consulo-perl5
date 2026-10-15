package consulo.perl5.mason.newProject;

import com.perl5.lang.htmlmason.HTMLMasonIcons;
import com.perl5.lang.htmlmason.HtmlMasonBundle;
import com.perl5.lang.htmlmason.idea.configuration.HTMLMasonSourceRootType;
import com.perl5.lang.mason2.Mason2Bundle;
import com.perl5.lang.mason2.Mason2Icons;
import com.perl5.lang.mason2.idea.configuration.Mason2SourceRootType;
import com.perl5.lang.perl.idea.modules.newProject.PerlFrameworkNewModuleBuilderProcessor;
import com.perl5.lang.perl.idea.modules.newProject.PerlNewModuleBuilder;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.module.creation.scratch.NewModuleBuilder;
import consulo.module.creation.scratch.NewModuleContext;
import consulo.module.creation.scratch.NewModuleContextGroup;
import consulo.perl5.mason.module.HTMLMasonMutableModuleExtension;
import consulo.perl5.mason.module.Mason2MutableModuleExtension;

/**
 * HTML::Mason and Mason2 templates of the Perl5 group of the "New Project/Module" wizard: Perl project with the Mason module extension
 * and a components root
 */
@ExtensionImpl
public class MasonNewModuleBuilder implements NewModuleBuilder {
  private static final String COMPONENTS_DIRECTORY_NAME = "comps";

  @Override
  public void setupContext(NewModuleContext context) {
    NewModuleContextGroup group = PerlNewModuleBuilder.addGroup(context);
    group.add(LocalizeValue.of(HtmlMasonBundle.message("html.mason")), HTMLMasonIcons.HTML_MASON_COMPONENT_ICON,
              PerlNewModuleBuilder.FRAMEWORK_TEMPLATE_WEIGHT,
              new PerlFrameworkNewModuleBuilderProcessor(HTMLMasonMutableModuleExtension.class, COMPONENTS_DIRECTORY_NAME,
                                                         HTMLMasonSourceRootType.INSTANCE));
    group.add(LocalizeValue.of(Mason2Bundle.message("mason2")), Mason2Icons.MASON_TOP_LEVEL_COMPONENT_ICON,
              PerlNewModuleBuilder.FRAMEWORK_TEMPLATE_WEIGHT,
              new PerlFrameworkNewModuleBuilderProcessor(Mason2MutableModuleExtension.class, COMPONENTS_DIRECTORY_NAME,
                                                         Mason2SourceRootType.INSTANCE));
  }
}
