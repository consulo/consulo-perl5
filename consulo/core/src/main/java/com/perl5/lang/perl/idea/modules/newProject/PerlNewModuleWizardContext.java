package com.perl5.lang.perl.idea.modules.newProject;

import consulo.content.bundle.Sdk;
import consulo.module.creation.NewModuleWizardContextBase;
import org.jetbrains.annotations.Nullable;

/**
 * "New Project/Module" wizard state of the Perl5 template, mirrors upstream {@code PerlProjectGenerationSettings}
 */
public class PerlNewModuleWizardContext extends NewModuleWizardContextBase {
  private @Nullable Sdk mySdk;

  public PerlNewModuleWizardContext(boolean isNewProject) {
    super(isNewProject);
  }

  public @Nullable Sdk getSdk() {
    return mySdk;
  }

  public void setSdk(@Nullable Sdk sdk) {
    mySdk = sdk;
  }
}
