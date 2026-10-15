package consulo.perl5.tt2.module;

import com.perl5.lang.tt2.idea.settings.TemplateToolkitSettings;
import consulo.annotation.access.RequiredReadAction;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.content.layer.extension.ModuleExtensionBase;
import consulo.perl5.core.module.PerlModuleExtensionStateUtil;
import org.jdom.Element;

import java.util.List;

/**
 * Template Toolkit module extension, a child of the Perl module extension: enables Template Toolkit templates roots, its state is the
 * Template Toolkit settings of the module ({@link TemplateToolkitSettings}).
 */
public class TemplateToolkitModuleExtension extends ModuleExtensionBase<TemplateToolkitModuleExtension> {
  private static final String START_TAG = "start-tag";
  private static final String END_TAG = "end-tag";
  private static final String OUTLINE_TAG = "outline-tag";
  private static final String ANYCASE = "anycase";
  private static final String SUBSTITUTED_EXTENSIONS = "substituted-extensions";
  private static final String EXTENSION = "extension";

  protected TemplateToolkitSettings mySettings = TemplateToolkitSettings.DEFAULT;

  public TemplateToolkitModuleExtension(String id, ModuleRootLayer moduleRootLayer) {
    super(id, moduleRootLayer);
  }

  public TemplateToolkitSettings getSettings() {
    return mySettings;
  }

  @RequiredReadAction
  @Override
  public void commit(TemplateToolkitModuleExtension mutableModuleExtension) {
    super.commit(mutableModuleExtension);
    mySettings = mutableModuleExtension.getSettings();
  }

  @Override
  protected void getStateImpl(Element element) {
    TemplateToolkitSettings defaults = TemplateToolkitSettings.DEFAULT;
    PerlModuleExtensionStateUtil.writeAttribute(element, START_TAG, mySettings.getStartTag(), defaults.getStartTag());
    PerlModuleExtensionStateUtil.writeAttribute(element, END_TAG, mySettings.getEndTag(), defaults.getEndTag());
    PerlModuleExtensionStateUtil.writeAttribute(element, OUTLINE_TAG, mySettings.getOutlineTag(), defaults.getOutlineTag());
    PerlModuleExtensionStateUtil.writeAttribute(element, ANYCASE, mySettings.isAnycaseEnabled(), defaults.isAnycaseEnabled());
    PerlModuleExtensionStateUtil.writeStrings(element, SUBSTITUTED_EXTENSIONS, EXTENSION,
                                              mySettings.getSubstitutedExtensions(), defaults.getSubstitutedExtensions());
  }

  @RequiredReadAction
  @Override
  protected void loadStateImpl(Element element) {
    TemplateToolkitSettings defaults = TemplateToolkitSettings.DEFAULT;
    List<String> substitutedExtensions =
      PerlModuleExtensionStateUtil.readStrings(element, SUBSTITUTED_EXTENSIONS, EXTENSION, defaults.getSubstitutedExtensions());
    mySettings = new TemplateToolkitSettings(
      PerlModuleExtensionStateUtil.readAttribute(element, START_TAG, defaults.getStartTag()),
      PerlModuleExtensionStateUtil.readAttribute(element, END_TAG, defaults.getEndTag()),
      PerlModuleExtensionStateUtil.readAttribute(element, OUTLINE_TAG, defaults.getOutlineTag()),
      PerlModuleExtensionStateUtil.readAttribute(element, ANYCASE, defaults.isAnycaseEnabled()),
      substitutedExtensions);
  }
}
