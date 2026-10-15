package consulo.perl5.core.module;

import com.perl5.lang.perl.idea.modules.PerlSourceRootType;
import consulo.application.Application;
import consulo.content.ContentFolderTypeProvider;
import consulo.module.Module;
import consulo.module.content.ModuleRootManager;
import consulo.module.content.layer.ContentFolderSupportPatcher;
import consulo.module.content.layer.ModifiableRootModel;
import consulo.module.extension.ModuleExtension;
import org.jspecify.annotations.Nullable;

import java.util.Set;

/**
 * Base of content folder support patchers of Perl frameworks (Template Toolkit, Mojolicious, Mason): the perl source root type of the
 * framework is offered only in modules with the framework module extension (a child of the Perl module extension) enabled.
 * <p>
 * The pairs (module extension, root type) of registered patchers also gate other framework features, see
 * {@link #isRootTypeEnabled(Module, PerlSourceRootType)}: e.g. project-wide framework roots are collected from the modules with the
 * framework module extension enabled only.
 */
public abstract class PerlFrameworkContentFolderSupportPatcher implements ContentFolderSupportPatcher {
  private final Class<? extends ModuleExtension> myExtensionClass;
  private final Class<? extends PerlSourceRootType> myRootTypeClass;

  protected PerlFrameworkContentFolderSupportPatcher(Class<? extends ModuleExtension> extensionClass,
                                                     Class<? extends PerlSourceRootType> rootTypeClass) {
    myExtensionClass = extensionClass;
    myRootTypeClass = rootTypeClass;
  }

  @Override
  public void patch(ModifiableRootModel model, Set<ContentFolderTypeProvider> set) {
    if (model.getExtension(myExtensionClass) != null) {
      // registered instances of the root type (content folder type extensions)
      set.addAll(ContentFolderTypeProvider.filter(myRootTypeClass::isInstance));
    }
  }

  /**
   * @return true iff the {@code rootType} may be used in the {@code module}: it is not a framework root type or the framework module
   * extension is enabled for the module
   */
  public static boolean isRootTypeEnabled(Module module, PerlSourceRootType rootType) {
    PerlFrameworkContentFolderSupportPatcher patcher = findPatcher(rootType);
    if (patcher == null) {
      return true;
    }
    return !module.isDisposed() && ModuleRootManager.getInstance(module).getExtension(patcher.myExtensionClass) != null;
  }

  private static @Nullable PerlFrameworkContentFolderSupportPatcher findPatcher(PerlSourceRootType rootType) {
    for (ContentFolderSupportPatcher patcher : Application.get().getExtensionList(ContentFolderSupportPatcher.class)) {
      if (patcher instanceof PerlFrameworkContentFolderSupportPatcher frameworkPatcher &&
          frameworkPatcher.myRootTypeClass.isInstance(rootType)) {
        return frameworkPatcher;
      }
    }
    return null;
  }
}
