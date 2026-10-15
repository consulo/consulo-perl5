package consulo.perl5.mason.module;

import com.perl5.lang.mason2.Mason2Bundle;
import com.perl5.lang.mason2.idea.configuration.MasonSettings;
import consulo.localize.LocalizeValue;
import consulo.perl5.core.ui.PerlUnifiedConfigurationUtil;
import consulo.project.Project;
import consulo.ui.Component;
import consulo.ui.InputBoxBuilder;
import consulo.ui.ListBox;
import consulo.ui.UIAccess;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.toolbar.AddAction;
import consulo.ui.ex.toolbar.EditAction;
import consulo.ui.ex.toolbar.ToolbarDecoratorBuilderFactory;
import consulo.ui.model.FlatDataModel;
import consulo.ui.model.MutableFlatDataModel;
import consulo.util.lang.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel of the Mason2 module extension: same options as upstream {@code MasonSettingsConfigurable}, a tab of the Perl5 settings page.
 */
final class Mason2ModuleExtensionPanel extends AbstractMasonModuleExtensionPanel {
  private final Mason2MutableModuleExtension myExtension;
  private final MutableFlatDataModel<String> myAutobaseModel;

  private Mason2ModuleExtensionPanel(Mason2MutableModuleExtension extension) {
    super(extension.getProject(), extension.getSettings().getGlobalVariables());
    myExtension = extension;
    myAutobaseModel = FlatDataModel.of(extension.getSettings().getAutobaseNames());
    myAutobaseModel.addListener(event -> updateSettings());
  }

  @RequiredUIAccess
  static Component create(Mason2MutableModuleExtension extension) {
    Mason2ModuleExtensionPanel panel = new Mason2ModuleExtensionPanel(extension);
    panel.createGlobalsComponent();
    panel.createAutobaseNamesComponent(extension.getProject());
    return panel.myRoot;
  }

  @RequiredUIAccess
  @Override
  protected void updateSettings() {
    List<String> autobaseNames = new ArrayList<>();
    myAutobaseModel.forEach(autobaseNames::add);
    myExtension.setSettings(new MasonSettings(autobaseNames, getGlobalVariables()));
  }

  /**
   * Order of the names is important: up and down actions are kept
   */
  @RequiredUIAccess
  private void createAutobaseNamesComponent(Project project) {
    ListBox<String> autobaseList = ListBox.create(myAutobaseModel);
    addLabeled(
      Mason2Bundle.message("autobase.names.autobase.names.option.order.is.important.later.components.may.be.inherited.from.early"),
      PerlUnifiedConfigurationUtil.withHeight(
        ToolbarDecoratorBuilderFactory.getInstance()
          .create(autobaseList)
          .addOrReplaceAction(new AddAction<>() {
            @RequiredUIAccess
            @Override
            protected void doAdd(AnActionEvent e) {
              UIAccess uiAccess = UIAccess.current();
              InputBoxBuilder.text()
                .title(LocalizeValue.of(Mason2Bundle.message("dialog.title.new.autobase.filename")))
                .text(LocalizeValue.of(Mason2Bundle.message("dialog.message.type.new.autobase.filename")))
                .value("")
                .showAsync(project)
                .whenComplete((fileName, error) -> {
                  if (StringUtil.isNotEmpty(fileName)) {
                    uiAccess.give(() -> {
                      if (myAutobaseModel.indexOf(fileName) < 0) {
                        myAutobaseModel.add(fileName);
                      }
                    });
                  }
                });
            }
          })
          .disableAction(EditAction.class)
          .build()));
  }
}
