package com.perl5.lang.perl.debugger;

import consulo.configurable.SearchableConfigurable;
import consulo.disposer.Disposable;
import consulo.localize.LocalizeValue;
import consulo.ui.Component;
import consulo.ui.Label;
import consulo.ui.Table;
import consulo.ui.TableItemEditor;
import consulo.ui.TextBox;
import consulo.ui.ValueComponent;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.toolbar.AddAction;
import consulo.ui.ex.toolbar.DownMoveAction;
import consulo.ui.ex.toolbar.RemoveAction;
import consulo.ui.ex.toolbar.ToolbarDecoratorBuilderFactory;
import consulo.ui.ex.toolbar.UpMoveAction;
import consulo.ui.layout.DockLayout;
import consulo.ui.model.FlatDataModel;
import consulo.ui.model.MutableFlatDataModel;
import consulo.util.collection.ContainerUtil;
import consulo.util.lang.StringUtil;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Unified UI version of upstream {@code PerlDebuggerSettings.DataViewsConfigurable} (a Swing table): type renderers of the perl debugger
 */
class PerlDebuggerDataViewsConfigurable implements SearchableConfigurable {
  private static final int PACKAGE_COLUMN_WIDTH = 200;
  private static final int RENDERER_COLUMN_WIDTH = 600;

  private final PerlDebuggerSettings mySettings;

  private @Nullable MutableFlatDataModel<PerlDebuggerSettings.Item> myModel;

  PerlDebuggerDataViewsConfigurable(PerlDebuggerSettings settings) {
    mySettings = settings;
  }

  @Override
  public String getId() {
    return "perl5.debugger.type.renderers";
  }

  @Override
  public LocalizeValue getDisplayName() {
    return LocalizeValue.localizeTODO(PerlDebuggerBundle.message("perl.debugger.settings.type.renderers.title"));
  }

  @RequiredUIAccess
  @Override
  public @Nullable Component createUIComponent(Disposable parentDisposable) {
    MutableFlatDataModel<PerlDebuggerSettings.Item> model = FlatDataModel.of(new ArrayList<>());
    myModel = model;

    Table<PerlDebuggerSettings.Item> table = Table.create(model);
    table.addColumn(LocalizeValue.of(PerlDebuggerBundle.message("perl.debugger.settings.class.column.title")),
                    (PerlDebuggerSettings.Item item) -> StringUtil.notNullize(item.namespaceName))
      .setWidth(PACKAGE_COLUMN_WIDTH)
      .setEditor(new TableItemEditor<>() {
        @RequiredUIAccess
        @Override
        public ValueComponent<String> createComponent(PerlDebuggerSettings.Item item) {
          return TextBox.create(StringUtil.notNullize(item.namespaceName));
        }

        @RequiredUIAccess
        @Override
        public void commit(PerlDebuggerSettings.Item item, @Nullable String value) {
          item.namespaceName = value;
        }
      });
    table.addColumn(LocalizeValue.of(PerlDebuggerBundle.message("perl.debugger.settings.renderer.column.title")),
                    (PerlDebuggerSettings.Item item) -> StringUtil.notNullize(item.renderExpression))
      .setWidth(RENDERER_COLUMN_WIDTH)
      .setEditor(new TableItemEditor<>() {
        @RequiredUIAccess
        @Override
        public ValueComponent<String> createComponent(PerlDebuggerSettings.Item item) {
          return TextBox.create(StringUtil.notNullize(item.renderExpression));
        }

        @RequiredUIAccess
        @Override
        public void commit(PerlDebuggerSettings.Item item, @Nullable String value) {
          item.renderExpression = value;
        }
      });

    // TODO consulo: upstream starts editing of the package name of the added item, unified Table has no API for it
    Component tablePanel = ToolbarDecoratorBuilderFactory.getInstance()
      .create(table)
      .addOrReplaceAction(new AddAction<>() {
        @RequiredUIAccess
        @Override
        protected void doAdd(AnActionEvent e) {
          PerlDebuggerSettings.Item itemToEdit = null;
          for (PerlDebuggerSettings.Item item : model) {
            if (StringUtil.isEmpty(item.namespaceName)) {
              itemToEdit = item;
              break;
            }
          }
          if (itemToEdit == null) {
            itemToEdit = new PerlDebuggerSettings.Item("Foo::Bar", "$it->to_string()");
            model.add(itemToEdit);
          }
          table.select(itemToEdit);
        }
      })
      .addOrReplaceAction(new RemoveAction<>() {
        @RequiredUIAccess
        @Override
        protected void doRemove(PerlDebuggerSettings.Item value, AnActionEvent e) {
          model.remove(value);
        }
      })
      .disableAction(UpMoveAction.class)
      .disableAction(DownMoveAction.class)
      .build();

    return DockLayout.create()
      .top(Label.create(LocalizeValue.of(PerlDebuggerBundle.message("perl.debugger.settings.type.renderers.explanation"))))
      .center(tablePanel);
  }

  @RequiredUIAccess
  @Override
  public boolean isModified() {
    return myModel != null && !mySettings.getDataRenderers().equals(getModelState());
  }

  @RequiredUIAccess
  @Override
  public void apply() {
    if (myModel != null) {
      mySettings.setDataRenderers(ContainerUtil.map(getModelState(), PerlDebuggerSettings.Item::new));
    }
  }

  @RequiredUIAccess
  @Override
  public void reset() {
    MutableFlatDataModel<PerlDebuggerSettings.Item> model = myModel;
    if (model != null) {
      model.replaceAll(mySettings.getDataRenderers());
    }
  }

  @RequiredUIAccess
  @Override
  public void disposeUIResources() {
    myModel = null;
  }

  private List<PerlDebuggerSettings.Item> getModelState() {
    List<PerlDebuggerSettings.Item> result = new ArrayList<>();
    MutableFlatDataModel<PerlDebuggerSettings.Item> model = myModel;
    if (model != null) {
      for (PerlDebuggerSettings.Item item : model) {
        if (item.isValid()) {
          result.add(item);
        }
      }
    }
    return result;
  }
}
