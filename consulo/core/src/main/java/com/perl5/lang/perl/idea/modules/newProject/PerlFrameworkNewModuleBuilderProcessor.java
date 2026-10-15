package com.perl5.lang.perl.idea.modules.newProject;

import consulo.annotation.access.RequiredReadAction;
import consulo.content.ContentFolderTypeProvider;
import consulo.module.content.layer.ContentEntry;
import consulo.module.content.layer.ModifiableRootModel;
import consulo.module.extension.MutableModuleExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Perl5 template of a framework (Template Toolkit, Mason, ...): a Perl module with the framework module extension enabled and,
 * if the framework has one, a directory marked as the framework root (templates, components)
 */
public class PerlFrameworkNewModuleBuilderProcessor extends PerlNewModuleBuilderProcessor<PerlNewModuleWizardContext> {
  private final Class<? extends MutableModuleExtension<?>> myExtensionClass;
  private final @Nullable String myRootDirectoryName;
  private final @Nullable ContentFolderTypeProvider myRootType;

  public PerlFrameworkNewModuleBuilderProcessor(@NotNull Class<? extends MutableModuleExtension<?>> extensionClass) {
    this(extensionClass, null, null);
  }

  public PerlFrameworkNewModuleBuilderProcessor(@NotNull Class<? extends MutableModuleExtension<?>> extensionClass,
                                                @Nullable String rootDirectoryName,
                                                @Nullable ContentFolderTypeProvider rootType) {
    myExtensionClass = extensionClass;
    myRootDirectoryName = rootDirectoryName;
    myRootType = rootType;
  }

  @Override
  public @NotNull PerlNewModuleWizardContext createContext(boolean isNewProject) {
    return new PerlNewModuleWizardContext(isNewProject);
  }

  @RequiredReadAction
  @Override
  protected void configureModule(@NotNull PerlNewModuleWizardContext context,
                                 @NotNull ContentEntry contentEntry,
                                 @NotNull ModifiableRootModel modifiableRootModel) {
    enableExtension(modifiableRootModel, myExtensionClass);
    if (myRootDirectoryName != null && myRootType != null) {
      addRootDirectory(contentEntry, myRootDirectoryName, myRootType);
    }
  }
}
