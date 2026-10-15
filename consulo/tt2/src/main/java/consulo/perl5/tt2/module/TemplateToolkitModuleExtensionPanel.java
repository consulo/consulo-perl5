package consulo.perl5.tt2.module;

import com.perl5.lang.tt2.TemplateToolkitBundle;
import com.perl5.lang.tt2.idea.settings.TemplateToolkitSettings;
import consulo.localize.LocalizeValue;
import consulo.perl5.core.ui.PerlUnifiedConfigurationUtil;
import consulo.ui.CheckBox;
import consulo.ui.Component;
import consulo.ui.Label;
import consulo.ui.TextBox;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.layout.VerticalLayout;
import consulo.ui.model.FlatDataModel;
import consulo.ui.model.MutableFlatDataModel;
import consulo.ui.style.StandardColors;
import consulo.ui.util.FormBuilder;
import consulo.util.lang.StringUtil;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel of the Template Toolkit module extension: the Template Toolkit settings of the module, every change is written to the mutable
 * extension. Same options as upstream {@code TemplateToolkitSettingsConfigurable}, a tab of the Perl5 settings page.
 */
final class TemplateToolkitModuleExtensionPanel {
  private final TemplateToolkitMutableModuleExtension myExtension;

  private final TextBox myStartTagBox;
  private final TextBox myEndTagBox;
  private final TextBox myOutlineTagBox;
  private final Label myErrorLabel = Label.create();
  private final CheckBox myEnableAnycaseCheckBox;
  private final MutableFlatDataModel<String> mySubstitutedExtensionsModel;

  @RequiredUIAccess
  private TemplateToolkitModuleExtensionPanel(TemplateToolkitMutableModuleExtension extension) {
    myExtension = extension;

    TemplateToolkitSettings settings = extension.getSettings();
    myStartTagBox = TextBox.create(settings.getStartTag());
    myEndTagBox = TextBox.create(settings.getEndTag());
    myOutlineTagBox = TextBox.create(settings.getOutlineTag());
    myEnableAnycaseCheckBox = CheckBox.create(LocalizeValue.of(TemplateToolkitBundle.message("ttk2.label.enableanycase")),
                                              settings.isAnycaseEnabled());
    mySubstitutedExtensionsModel = FlatDataModel.of(settings.getSubstitutedExtensions());

    myErrorLabel.setForegroundColor(StandardColors.RED);
    myErrorLabel.setVisible(false);
  }

  @RequiredUIAccess
  static Component create(TemplateToolkitMutableModuleExtension extension) {
    return new TemplateToolkitModuleExtensionPanel(extension).createComponent();
  }

  @RequiredUIAccess
  private Component createComponent() {
    VerticalLayout root = VerticalLayout.create();
    root.add(FormBuilder.create()
               .addLabeled(LocalizeValue.of(TemplateToolkitBundle.message("ttk2.label.opentag")), myStartTagBox)
               .addLabeled(LocalizeValue.of(TemplateToolkitBundle.message("ttk2.label.closetag")), myEndTagBox)
               .addLabeled(LocalizeValue.of(TemplateToolkitBundle.message("ttk2.label.outlinetag")), myOutlineTagBox)
               .build());
    root.add(myErrorLabel);
    root.add(myEnableAnycaseCheckBox);
    root.add(PerlUnifiedConfigurationUtil.createLabel(TemplateToolkitBundle.message("ttk2.configuration.extension")));
    root.add(PerlUnifiedConfigurationUtil.createSubstitutedExtensionsComponent(mySubstitutedExtensionsModel));

    myStartTagBox.addValueListener(event -> updateSettings());
    myEndTagBox.addValueListener(event -> updateSettings());
    myOutlineTagBox.addValueListener(event -> updateSettings());
    myEnableAnycaseCheckBox.addValueListener(event -> updateSettings());
    mySubstitutedExtensionsModel.addListener(event -> updateSettings());
    return root;
  }

  /**
   * Upstream rejects settings with an empty start or end tag on apply: here they are not written to the extension (the last valid
   * settings are kept) and the error is shown
   */
  @RequiredUIAccess
  private void updateSettings() {
    String startTag = StringUtil.notNullize(myStartTagBox.getValue());
    String endTag = StringUtil.notNullize(myEndTagBox.getValue());
    @Nullable String error = StringUtil.isEmpty(startTag) ? TemplateToolkitBundle.message("ttk2.error.emptyopentag") :
                             StringUtil.isEmpty(endTag) ? TemplateToolkitBundle.message("ttk2.error.emptyclosetag") :
                             null;
    myErrorLabel.setText(LocalizeValue.of(StringUtil.notNullize(error)));
    myErrorLabel.setVisible(error != null);
    if (error != null) {
      return;
    }
    List<String> substitutedExtensions = new ArrayList<>();
    mySubstitutedExtensionsModel.forEach(substitutedExtensions::add);
    myExtension.setSettings(new TemplateToolkitSettings(startTag,
                                                        endTag,
                                                        StringUtil.notNullize(myOutlineTagBox.getValue()),
                                                        Boolean.TRUE.equals(myEnableAnycaseCheckBox.getValue()),
                                                        substitutedExtensions));
  }
}
