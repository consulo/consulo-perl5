package com.perl5.lang.perl.idea.modules;

import com.intellij.openapi.projectRoots.impl.PerlModuleExtension;
import consulo.annotation.component.ExtensionImpl;
import consulo.content.ContentFolderTypeProvider;
import consulo.language.content.ProductionContentFolderTypeProvider;
import consulo.language.content.TestContentFolderTypeProvider;
import consulo.module.content.layer.ContentFolderSupportPatcher;
import consulo.module.content.layer.ModifiableRootModel;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * Allows sources, tests and perl library roots in modules with the Perl module extension. Root types of Perl frameworks (templates
 * and components roots of Template Toolkit, Mojolicious, Mason) are allowed by framework patchers when the framework module extension
 * (a child of the Perl one) is enabled, see {@code PerlFrameworkContentFolderSupportPatcher}.
 */
@ExtensionImpl
public class PerlContentFolderSupportPatcher implements ContentFolderSupportPatcher {
  @Override
  public void patch(@NotNull ModifiableRootModel model, @NotNull Set<ContentFolderTypeProvider> set) {
    if (model.getExtension(PerlModuleExtension.class) == null) {
      return;
    }
    set.add(ProductionContentFolderTypeProvider.getInstance());
    set.add(TestContentFolderTypeProvider.getInstance());
    set.addAll(ContentFolderTypeProvider.filter(it -> it instanceof PerlLibrarySourceRootType));
  }
}
