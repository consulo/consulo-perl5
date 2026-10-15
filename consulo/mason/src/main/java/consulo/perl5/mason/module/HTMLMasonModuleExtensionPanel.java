package consulo.perl5.mason.module;

import com.perl5.lang.htmlmason.HtmlMasonBundle;
import com.perl5.lang.htmlmason.idea.configuration.HTMLMasonCustomTag;
import com.perl5.lang.htmlmason.idea.configuration.HTMLMasonCustomTagRole;
import com.perl5.lang.htmlmason.idea.configuration.HTMLMasonSettings;
import com.perl5.lang.perl.parser.PerlParserUtil;
import consulo.localize.LocalizeValue;
import consulo.perl5.core.ui.PerlUnifiedConfigurationUtil;
import consulo.ui.ComboBox;
import consulo.ui.Component;
import consulo.ui.Table;
import consulo.ui.TableItemEditor;
import consulo.ui.TextBox;
import consulo.ui.ValueComponent;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.toolbar.AddAction;
import consulo.ui.ex.toolbar.DownMoveAction;
import consulo.ui.ex.toolbar.EditAction;
import consulo.ui.ex.toolbar.ToolbarDecoratorBuilderFactory;
import consulo.ui.ex.toolbar.UpMoveAction;
import consulo.ui.model.FlatDataModel;
import consulo.ui.model.MutableFlatDataModel;
import consulo.ui.util.FormBuilder;
import consulo.util.lang.StringUtil;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.perl5.lang.htmlmason.HTMLMasonSyntaxElements.BUILTIN_TAGS_COMPLEX;
import static com.perl5.lang.htmlmason.HTMLMasonSyntaxElements.BUILTIN_TAGS_SIMPLE;

/**
 * Panel of the HTML::Mason module extension: same options as upstream {@code HTMLMasonSettingsConfigurable}, a tab of the Perl5 settings
 * page.
 */
final class HTMLMasonModuleExtensionPanel extends AbstractMasonModuleExtensionPanel {
  private static final Set<String> BUILTIN_TAGS = new HashSet<>();

  static {
    BUILTIN_TAGS.addAll(BUILTIN_TAGS_SIMPLE);
    BUILTIN_TAGS.addAll(BUILTIN_TAGS_COMPLEX);
  }

  private final HTMLMasonMutableModuleExtension myExtension;

  private final TextBox myAutohandlerNameBox;
  private final TextBox myDefaulthandlerNameBox;
  private final MutableFlatDataModel<String> mySubstitutedExtensionsModel;
  private final MutableFlatDataModel<CustomTagRow> myCustomTagsModel;

  @RequiredUIAccess
  private HTMLMasonModuleExtensionPanel(HTMLMasonMutableModuleExtension extension) {
    super(extension.getProject(), extension.getSettings().getGlobalVariables());
    myExtension = extension;

    HTMLMasonSettings settings = extension.getSettings();
    myAutohandlerNameBox = TextBox.create(settings.getAutoHandlerName());
    myDefaulthandlerNameBox = TextBox.create(settings.getDefaultHandlerName());
    mySubstitutedExtensionsModel = FlatDataModel.of(settings.getSubstitutedExtensions());

    List<CustomTagRow> customTags = new ArrayList<>();
    for (HTMLMasonCustomTag customTag : settings.getCustomTags()) {
      customTags.add(new CustomTagRow(customTag.getText(), customTag.getRole()));
    }
    myCustomTagsModel = FlatDataModel.of(customTags);
  }

  @RequiredUIAccess
  static Component create(HTMLMasonMutableModuleExtension extension) {
    return new HTMLMasonModuleExtensionPanel(extension).createComponent();
  }

  @RequiredUIAccess
  private Component createComponent() {
    myRoot.add(FormBuilder.create()
                 .addLabeled(LocalizeValue.of(HtmlMasonBundle.message("default.handler.name")), myDefaulthandlerNameBox)
                 .addLabeled(LocalizeValue.of(HtmlMasonBundle.message("auto.handler.name")), myAutohandlerNameBox)
                 .build());
    createGlobalsComponent();
    addLabeled(HtmlMasonBundle.message("extensions.that.should.be.handled.as.html.mason.components.except.mas.only.in.component.roots"),
               PerlUnifiedConfigurationUtil.createSubstitutedExtensionsComponent(mySubstitutedExtensionsModel));
    createCustomTagsComponent();

    myAutohandlerNameBox.addValueListener(event -> updateSettings());
    myDefaulthandlerNameBox.addValueListener(event -> updateSettings());
    mySubstitutedExtensionsModel.addListener(event -> updateSettings());
    myCustomTagsModel.addListener(event -> updateSettings());
    return myRoot;
  }

  @RequiredUIAccess
  @Override
  protected void updateSettings() {
    List<String> substitutedExtensions = new ArrayList<>();
    mySubstitutedExtensionsModel.forEach(substitutedExtensions::add);
    List<HTMLMasonCustomTag> customTags = new ArrayList<>();
    for (CustomTagRow row : myCustomTagsModel) {
      customTags.add(new HTMLMasonCustomTag(row.myText, row.myRole));
    }
    myExtension.setSettings(new HTMLMasonSettings(StringUtil.notNullize(myAutohandlerNameBox.getValue()),
                                                  StringUtil.notNullize(myDefaulthandlerNameBox.getValue()),
                                                  substitutedExtensions,
                                                  customTags,
                                                  getGlobalVariables()));
  }

  @RequiredUIAccess
  private void createCustomTagsComponent() {
    Table<CustomTagRow> table = Table.create(myCustomTagsModel);
    table.addColumn(LocalizeValue.of(HtmlMasonBundle.message("column.name.tag.text.without.surrounding")), row -> row.myText);
    table.addColumn(LocalizeValue.of(HtmlMasonBundle.message("column.name.parse")), row -> row.myRole)
      .setRenderer((presentation, item) -> {
        HTMLMasonCustomTagRole role = item.getValue();
        presentation.append(role == null ? "" : role.getTitle());
      })
      .setEditor(new TableItemEditor<>() {
        @RequiredUIAccess
        @Override
        public ValueComponent<HTMLMasonCustomTagRole> createComponent(CustomTagRow row) {
          ComboBox<HTMLMasonCustomTagRole> comboBox = ComboBox.create(HTMLMasonCustomTagRole.values());
          comboBox.setTextRenderer(role -> LocalizeValue.of(role == null ? "" : role.getTitle()));
          comboBox.setValue(row.myRole);
          return comboBox;
        }

        @RequiredUIAccess
        @Override
        public void commit(CustomTagRow row, @Nullable HTMLMasonCustomTagRole value) {
          if (value != null) {
            row.myRole = value;
            myCustomTagsModel.update(row);
          }
        }
      });

    // the role is edited in the table (a double click would also start the role editor), the text with the edit action
    addLabeled(HtmlMasonBundle.message("custom.tags.that.mimics.built.in.html.mason.tags"),
               PerlUnifiedConfigurationUtil.withHeight(
                 ToolbarDecoratorBuilderFactory.getInstance()
                   .create(table)
                   .addOrReplaceAction(new AddAction<>() {
                     @RequiredUIAccess
                     @Override
                     protected void doAdd(AnActionEvent e) {
                       editTagText(table, null);
                     }
                   })
                   .addOrReplaceAction(new EditAction<>() {
                     @RequiredUIAccess
                     @Override
                     protected void doEdit(CustomTagRow row, AnActionEvent e) {
                       editTagText(table, row);
                     }
                   })
                   .disableAction(UpMoveAction.class)
                   .disableAction(DownMoveAction.class)
                   .build()));
  }

  /**
   * Asks for the text of the tag, a new tag is parsed as {@code <%perl>}
   *
   * @param row edited row, null for a new one
   */
  @RequiredUIAccess
  private void editTagText(Table<CustomTagRow> table, @Nullable CustomTagRow row) {
    String oldText = row == null ? "customTag" + myCustomTagsModel.getSize() : row.myText;
    showInputBox(HtmlMasonBundle.message("column.name.tag.text.without.surrounding"),
                 oldText,
                 text -> getTagTextError(text, row),
                 text -> {
                   if (row == null) {
                     CustomTagRow newRow = new CustomTagRow(text, HTMLMasonCustomTagRole.PERL);
                     myCustomTagsModel.add(newRow);
                     table.select(newRow);
                   }
                   else {
                     row.myText = text;
                     myCustomTagsModel.update(row);
                   }
                 });
  }

  private @Nullable String getTagTextError(String text, @Nullable CustomTagRow row) {
    if (!PerlParserUtil.isIdentifier(text)) {
      return HtmlMasonBundle.message("dialog.message.tag.text.should.be.valid.identifier");
    }
    if (BUILTIN_TAGS.contains(text)) {
      return HtmlMasonBundle.message("dialog.message.tag.already.defined.in.html.mason", text);
    }
    for (CustomTagRow otherRow : myCustomTagsModel) {
      if (otherRow != row && text.equals(otherRow.myText)) {
        return HtmlMasonBundle.message("dialog.message.tag.with.such.text.already.exists");
      }
    }
    return null;
  }

  /**
   * Row of the custom tags table: rows are edited in place, while {@link HTMLMasonCustomTag} has value equality
   */
  private static final class CustomTagRow {
    private String myText;
    private HTMLMasonCustomTagRole myRole;

    private CustomTagRow(@Nullable String text, @Nullable HTMLMasonCustomTagRole role) {
      myText = StringUtil.notNullize(text);
      myRole = role == null ? HTMLMasonCustomTagRole.PERL : role;
    }
  }
}
