package com.perl5.lang.perl.idea.modules.newProject;

import com.intellij.openapi.projectRoots.impl.PerlModuleExtension;
import com.perl5.lang.perl.idea.modules.PerlLibrarySourceRootType;
import com.perl5.lang.perl.idea.modules.PerlMutableModuleExtension;
import consulo.annotation.access.RequiredReadAction;
import consulo.content.ContentFolderTypeProvider;
import consulo.content.bundle.Sdk;
import consulo.logging.Logger;
import consulo.module.content.layer.ContentEntry;
import consulo.module.content.layer.ModifiableRootModel;
import consulo.module.content.layer.orderEntry.ModuleExtensionWithSdkOrderEntry;
import consulo.module.content.layer.orderEntry.OrderEntry;
import consulo.module.creation.scratch.NewModuleBuilderProcessor;
import consulo.module.extension.MutableModuleExtension;
import consulo.ui.ex.wizard.WizardStep;
import consulo.virtualFileSystem.util.VirtualFileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

import static com.perl5.lang.perl.util.PerlPackageUtilCore.DEFAULT_LIB_DIR;
import static com.perl5.lang.perl.util.PerlPackageUtilCore.DEFAULT_TEST_DIR;

/**
 * Template of the Perl5 group of the "New Project/Module" wizard: name, location and interpreter step; enables
 * {@link PerlModuleExtension} with the selected interpreter and creates {@code lib} (marked as perl library root) and {@code t}
 * directories, as upstream {@code PerlProjectGenerator} does. Framework templates configure the rest in
 * {@link #configureModule}.
 */
public abstract class PerlNewModuleBuilderProcessor<C extends PerlNewModuleWizardContext> implements NewModuleBuilderProcessor<C> {
  private static final Logger LOG = Logger.getInstance(PerlNewModuleBuilderProcessor.class);

  @Override
  public void buildSteps(@NotNull Consumer<WizardStep<C>> consumer, @NotNull C context) {
    consumer.accept(new PerlNewModuleSetupStep<>(context));
  }

  @RequiredReadAction
  @Override
  public void process(@NotNull C context, @NotNull ContentEntry contentEntry, @NotNull ModifiableRootModel modifiableRootModel) {
    PerlMutableModuleExtension extension = enableExtension(modifiableRootModel, PerlMutableModuleExtension.class);

    Sdk sdk = context.getSdk();
    if (sdk != null) {
      extension.getInheritableSdk().set(null, sdk);
      // the module of a chosen directory may exist already
      if (!hasSdkEntry(modifiableRootModel, extension.getId())) {
        modifiableRootModel.addModuleExtensionSdkEntry(extension);
      }
    }

    // upstream PerlProjectGenerator#configureModule
    createDirectory(contentEntry, DEFAULT_TEST_DIR);
    addRootDirectory(contentEntry, DEFAULT_LIB_DIR, PerlLibrarySourceRootType.INSTANCE);

    configureModule(context, contentEntry, modifiableRootModel);
  }

  /**
   * Configures the module after the Perl part: enables framework (child) module extensions, creates framework directories, etc.
   */
  @RequiredReadAction
  protected void configureModule(@NotNull C context, @NotNull ContentEntry contentEntry, @NotNull ModifiableRootModel modifiableRootModel) {
  }

  /**
   * Enables the module extension of the {@code extensionClass}. A framework extension is a child of the Perl one, which is enabled
   * before {@link #configureModule}.
   */
  protected static <T extends MutableModuleExtension<?>> @NotNull T enableExtension(@NotNull ModifiableRootModel modifiableRootModel,
                                                                                     @NotNull Class<T> extensionClass) {
    T extension = Objects.requireNonNull(modifiableRootModel.getExtensionWithoutCheck(extensionClass), extensionClass::getName);
    extension.setEnabled(true);
    return extension;
  }

  /**
   * Creates the {@code name} directory in the content root and marks it as a {@code rootType} root
   */
  protected static void addRootDirectory(@NotNull ContentEntry contentEntry, @NotNull String name, @NotNull ContentFolderTypeProvider rootType) {
    createDirectory(contentEntry, name);
    contentEntry.addFolder(contentEntry.getUrl() + "/" + name, rootType);
  }

  private static boolean hasSdkEntry(@NotNull ModifiableRootModel modifiableRootModel, @NotNull String extensionId) {
    for (OrderEntry orderEntry : modifiableRootModel.getOrderEntries()) {
      if (orderEntry instanceof ModuleExtensionWithSdkOrderEntry sdkOrderEntry && extensionId.equals(sdkOrderEntry.getModuleExtensionId())) {
        return true;
      }
    }
    return false;
  }

  protected static void createDirectory(@NotNull ContentEntry contentEntry, @NotNull String name) {
    Path directory = Path.of(VirtualFileUtil.urlToPath(contentEntry.getUrl()), name);
    try {
      Files.createDirectories(directory);
    }
    catch (IOException e) {
      LOG.warn("Error creating " + directory, e);
    }
  }
}
