package consulo.perl5.mason.module;

import com.perl5.lang.htmlmason.idea.configuration.HTMLMasonCustomTag;
import com.perl5.lang.htmlmason.idea.configuration.HTMLMasonCustomTagRole;
import com.perl5.lang.htmlmason.idea.configuration.HTMLMasonSettings;
import consulo.annotation.access.RequiredReadAction;
import consulo.module.content.layer.ModuleRootLayer;
import consulo.module.content.layer.extension.ModuleExtensionBase;
import consulo.perl5.core.module.PerlModuleExtensionStateUtil;
import consulo.util.lang.StringUtil;
import org.jdom.Element;
import org.jspecify.annotations.Nullable;

/**
 * HTML::Mason module extension, a child of the Perl module extension: enables HTML::Mason components roots, its state is the HTML::Mason
 * settings of the module ({@link HTMLMasonSettings}).
 */
public class HTMLMasonModuleExtension extends ModuleExtensionBase<HTMLMasonModuleExtension> {
  private static final String AUTO_HANDLER = "auto-handler";
  private static final String DEFAULT_HANDLER = "default-handler";
  private static final String SUBSTITUTED_EXTENSIONS = "substituted-extensions";
  private static final String EXTENSION = "extension";
  private static final String CUSTOM_TAGS = "custom-tags";
  private static final String TAG = "tag";
  private static final String TAG_TEXT = "text";
  private static final String TAG_ROLE = "role";

  protected HTMLMasonSettings mySettings = HTMLMasonSettings.DEFAULT;

  public HTMLMasonModuleExtension(String id, ModuleRootLayer moduleRootLayer) {
    super(id, moduleRootLayer);
  }

  public HTMLMasonSettings getSettings() {
    return mySettings;
  }

  @RequiredReadAction
  @Override
  public void commit(HTMLMasonModuleExtension mutableModuleExtension) {
    super.commit(mutableModuleExtension);
    mySettings = mutableModuleExtension.getSettings();
  }

  @Override
  protected void getStateImpl(Element element) {
    HTMLMasonSettings defaults = HTMLMasonSettings.DEFAULT;
    PerlModuleExtensionStateUtil.writeAttribute(element, AUTO_HANDLER, mySettings.getAutoHandlerName(), defaults.getAutoHandlerName());
    PerlModuleExtensionStateUtil.writeAttribute(element, DEFAULT_HANDLER, mySettings.getDefaultHandlerName(),
                                                defaults.getDefaultHandlerName());
    PerlModuleExtensionStateUtil.writeStrings(element, SUBSTITUTED_EXTENSIONS, EXTENSION,
                                              mySettings.getSubstitutedExtensions(), defaults.getSubstitutedExtensions());
    PerlModuleExtensionStateUtil.writeList(element, CUSTOM_TAGS, TAG, mySettings.getCustomTags(), defaults.getCustomTags(),
                                           (tag, tagElement) -> {
                                             tagElement.setAttribute(TAG_TEXT, StringUtil.notNullize(tag.getText()));
                                             tagElement.setAttribute(TAG_ROLE, tag.getRole().name());
                                           });
    MasonModuleExtensionStateUtil.writeGlobalVariables(element, mySettings.getGlobalVariables(), defaults.getGlobalVariables());
  }

  @RequiredReadAction
  @Override
  protected void loadStateImpl(Element element) {
    HTMLMasonSettings defaults = HTMLMasonSettings.DEFAULT;
    mySettings = new HTMLMasonSettings(
      PerlModuleExtensionStateUtil.readAttribute(element, AUTO_HANDLER, defaults.getAutoHandlerName()),
      PerlModuleExtensionStateUtil.readAttribute(element, DEFAULT_HANDLER, defaults.getDefaultHandlerName()),
      PerlModuleExtensionStateUtil.readStrings(element, SUBSTITUTED_EXTENSIONS, EXTENSION, defaults.getSubstitutedExtensions()),
      PerlModuleExtensionStateUtil.readList(element, CUSTOM_TAGS, TAG, defaults.getCustomTags(), HTMLMasonModuleExtension::readCustomTag),
      MasonModuleExtensionStateUtil.readGlobalVariables(element, defaults.getGlobalVariables()));
  }

  private static @Nullable HTMLMasonCustomTag readCustomTag(Element tagElement) {
    String text = tagElement.getAttributeValue(TAG_TEXT);
    String roleName = tagElement.getAttributeValue(TAG_ROLE);
    if (StringUtil.isEmpty(text) || roleName == null) {
      return null;
    }
    try {
      return new HTMLMasonCustomTag(text, HTMLMasonCustomTagRole.valueOf(roleName));
    }
    catch (IllegalArgumentException e) {
      return null;
    }
  }
}
