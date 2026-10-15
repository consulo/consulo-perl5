package consulo.perl5.mason.module;

import com.perl5.lang.MasonFrameworkBundle;
import com.perl5.lang.mason2.idea.configuration.VariableDescription;
import com.perl5.lang.perl.parser.PerlParserUtil;
import consulo.localize.LocalizeValue;
import consulo.perl5.core.ui.PerlUnifiedConfigurationUtil;
import consulo.project.Project;
import consulo.ui.Component;
import consulo.ui.InputBoxBuilder;
import consulo.ui.InputProblem;
import consulo.ui.InputValidator;
import consulo.ui.Table;
import consulo.ui.UIAccess;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.toolbar.AddAction;
import consulo.ui.ex.toolbar.DownMoveAction;
import consulo.ui.ex.toolbar.EditAction;
import consulo.ui.ex.toolbar.ToolbarDecoratorBuilderFactory;
import consulo.ui.ex.toolbar.UpMoveAction;
import consulo.ui.layout.VerticalLayout;
import consulo.ui.model.FlatDataModel;
import consulo.ui.model.MutableFlatDataModel;
import consulo.util.lang.StringUtil;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Base of panels of the Mason module extensions: the settings of the module, every change is written to the mutable extension. Adapted
 * from upstream {@code AbstractMasonSettingsConfigurable}, a base of tabs of the Perl5 settings page.
 * <p>
 * Texts of table rows are entered in input boxes, which validate them as upstream table cell editors do: text cell editors of the
 * unified table do not commit on desktop.
 */
abstract class AbstractMasonModuleExtensionPanel {
  protected final VerticalLayout myRoot = VerticalLayout.create();

  private final Project myProject;
  private final MutableFlatDataModel<GlobalVariableRow> myGlobalsModel;

  protected AbstractMasonModuleExtensionPanel(Project project, List<VariableDescription> globalVariables) {
    myProject = project;
    List<GlobalVariableRow> rows = new ArrayList<>();
    for (VariableDescription variableDescription : globalVariables) {
      rows.add(new GlobalVariableRow(variableDescription.variableName, variableDescription.variableType));
    }
    myGlobalsModel = FlatDataModel.of(rows);
    myGlobalsModel.addListener(event -> updateSettings());
  }

  /**
   * Writes the settings shown by the panel to the extension
   */
  @RequiredUIAccess
  protected abstract void updateSettings();

  /**
   * Adds a label and a component below it
   */
  @RequiredUIAccess
  protected void addLabeled(String label, Component component) {
    myRoot.add(PerlUnifiedConfigurationUtil.createLabel(label));
    myRoot.add(component);
  }

  /**
   * @return global variables of the table
   */
  protected List<VariableDescription> getGlobalVariables() {
    List<VariableDescription> result = new ArrayList<>();
    for (GlobalVariableRow row : myGlobalsModel) {
      result.add(new VariableDescription(row.myName, row.myType));
    }
    return result;
  }

  @RequiredUIAccess
  protected void createGlobalsComponent() {
    Table<GlobalVariableRow> table = Table.create(myGlobalsModel);
    table.addColumn(LocalizeValue.of(MasonFrameworkBundle.message("column.name.variable.name")), row -> row.myName);
    table.addColumn(LocalizeValue.of(MasonFrameworkBundle.message("column.name.variable.type")), row -> row.myType);
    table.addDoubleClickListener(event -> {
      GlobalVariableRow row = table.getSelectedItem();
      if (row != null) {
        editGlobalVariable(table, row);
      }
    });

    addLabeled(MasonFrameworkBundle.message("components.global.variables.allow.globals.option"),
               PerlUnifiedConfigurationUtil.withHeight(
                 ToolbarDecoratorBuilderFactory.getInstance()
                   .create(table)
                   .addOrReplaceAction(new AddAction<>() {
                     @RequiredUIAccess
                     @Override
                     protected void doAdd(AnActionEvent e) {
                       editGlobalVariable(table, null);
                     }
                   })
                   .addOrReplaceAction(new EditAction<>() {
                     @RequiredUIAccess
                     @Override
                     protected void doEdit(GlobalVariableRow row, AnActionEvent e) {
                       editGlobalVariable(table, row);
                     }
                   })
                   .disableAction(UpMoveAction.class)
                   .disableAction(DownMoveAction.class)
                   .build()));
  }

  /**
   * Asks for the name of the variable and, for a scalar, its type
   *
   * @param row edited row, null for a new one
   */
  @RequiredUIAccess
  private void editGlobalVariable(Table<GlobalVariableRow> table, @Nullable GlobalVariableRow row) {
    String oldName = row == null ? "" : row.myName;
    showInputBox(MasonFrameworkBundle.message("column.name.variable.name"),
                 oldName,
                 name -> !PerlParserUtil.isVariableWithSigil(name) || !name.equals(oldName) && containsVariableName(name)
                         ? MasonFrameworkBundle.message("dialog.message.incorrect.variable.name", name)
                         : null,
                 name -> {
                   if (name.charAt(0) != '$') {
                     applyGlobalVariable(table, row, name, "");
                     return;
                   }
                   showInputBox(MasonFrameworkBundle.message("column.name.variable.type"),
                                row == null ? "" : row.myType,
                                type -> type.isEmpty() || PerlParserUtil.isAmbiguousPackage(type)
                                        ? null
                                        : MasonFrameworkBundle.message("dialog.message.incorrect.package.name", type),
                                type -> applyGlobalVariable(table, row, name, type));
                 });
  }

  @RequiredUIAccess
  private void applyGlobalVariable(Table<GlobalVariableRow> table, @Nullable GlobalVariableRow row, String name, String type) {
    if (row == null) {
      GlobalVariableRow newRow = new GlobalVariableRow(name, type);
      myGlobalsModel.add(newRow);
      table.select(newRow);
    }
    else {
      row.myName = name;
      row.myType = type;
      myGlobalsModel.update(row);
    }
  }

  private boolean containsVariableName(String variableName) {
    for (GlobalVariableRow row : myGlobalsModel) {
      if (variableName.equals(row.myName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Shows an input box for a text of a table row, the trimmed text is passed to the {@code consumer} in the UI thread. The project is the
   * owner: a unified component of the module settings has no unified window ancestor, an input box without an owner is not shown over the
   * settings dialog.
   *
   * @param errorProvider returns the error for an invalid text, null for a valid one
   */
  @RequiredUIAccess
  protected void showInputBox(String title, String value, ErrorProvider errorProvider, @RequiredUIAccess Consumer<String> consumer) {
    UIAccess uiAccess = UIAccess.current();
    InputValidator<String> validator = text -> {
      String error = errorProvider.getError(StringUtil.trim(StringUtil.notNullize(text)));
      return error == null ? null : InputProblem.error(LocalizeValue.of(error));
    };
    InputBoxBuilder.text()
      .title(LocalizeValue.of(title))
      .value(value)
      .validator(validator)
      .showAsync(myProject)
      .whenComplete((text, error) -> {
        if (text != null) {
          uiAccess.give(() -> consumer.accept(StringUtil.trim(text)));
        }
      });
  }

  @FunctionalInterface
  protected interface ErrorProvider {
    @Nullable
    String getError(String text);
  }

  /**
   * Row of the global variables table: rows are edited in place, while {@link VariableDescription} has value equality
   */
  private static final class GlobalVariableRow {
    private String myName;
    private String myType;

    private GlobalVariableRow(@Nullable String name, @Nullable String type) {
      myName = StringUtil.notNullize(name);
      myType = StringUtil.notNullize(type);
    }
  }
}
