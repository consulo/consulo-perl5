package consulo.perl5.tt2;

import com.perl5.lang.tt2.psi.mixins.TemplateToolkitNamedBlockMixin;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.language.icon.IconDescriptor;
import consulo.language.icon.IconDescriptorUpdater;
import consulo.language.psi.PsiElement;
import consulo.ui.image.Image;

/**
 * Consulo has no PsiElement#getIcon(int): element icons come from IconDescriptorUpdater,
 * so delegate to the upstream icon methods of Template Toolkit PSI.
 */
@ExtensionImpl
public class TemplateToolkitIconDescriptorUpdater implements IconDescriptorUpdater {
  @RequiredReadAction
  @Override
  public void updateIcon(IconDescriptor iconDescriptor, PsiElement element, int flags) {
    if (element instanceof TemplateToolkitNamedBlockMixin namedBlock) {
      Image icon = namedBlock.getIcon(flags);
      if (icon != null) {
        iconDescriptor.setMainIcon(icon);
      }
    }
  }
}
