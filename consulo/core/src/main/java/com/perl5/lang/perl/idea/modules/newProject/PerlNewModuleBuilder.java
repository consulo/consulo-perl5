package com.perl5.lang.perl.idea.modules.newProject;

import com.perl5.PerlBundle;
import com.perl5.PerlIcons;
import consulo.annotation.component.ExtensionImpl;
import consulo.localize.LocalizeValue;
import consulo.module.creation.scratch.NewModuleBuilder;
import consulo.module.creation.scratch.NewModuleContext;
import consulo.module.creation.scratch.NewModuleContextGroup;
import org.jetbrains.annotations.NotNull;

/**
 * Perl5 group of the "New Project/Module" wizard with the empty Perl project template. Framework plugins add their templates to the
 * group with {@link #addGroup} and {@link #FRAMEWORK_TEMPLATE_WEIGHT}.
 */
@ExtensionImpl(id = "perl5")
public class PerlNewModuleBuilder implements NewModuleBuilder {
  /**
   * Weight of framework templates: they follow the empty project
   */
  public static final int FRAMEWORK_TEMPLATE_WEIGHT = 100;

  private static final String GROUP_ID = "perl5";

  @Override
  public void setupContext(@NotNull NewModuleContext context) {
    addGroup(context).add(LocalizeValue.localizeTODO("Empty Project"), PerlIcons.PERL_LANGUAGE_ICON,
                          new PerlNewModuleBuilderProcessor<PerlNewModuleWizardContext>() {
                            @Override
                            public @NotNull PerlNewModuleWizardContext createContext(boolean isNewProject) {
                              return new PerlNewModuleWizardContext(isNewProject);
                            }
                          });
  }

  /**
   * @return the Perl5 group of the wizard, created by the first builder adding templates to it
   */
  public static @NotNull NewModuleContextGroup addGroup(@NotNull NewModuleContext context) {
    return context.addGroup(GROUP_ID, LocalizeValue.of(PerlBundle.message("perl.perl5")), PerlIcons.PERL_LANGUAGE_ICON);
  }
}
