package com.perl5.lang.perl.idea.intellilang;

import com.perl5.PerlBundle;
import consulo.configurable.Configurable;
import consulo.disposer.Disposable;
import consulo.fileEditor.util.FileContentUtil;
import consulo.html.language.HTMLLanguage;
import consulo.language.Language;
import consulo.language.LanguageRegistry;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.ui.ComboBox;
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
import consulo.ui.ex.toolbar.EditAction;
import consulo.ui.ex.toolbar.ToolbarDecoratorBuilderFactory;
import consulo.ui.ex.toolbar.UpMoveAction;
import consulo.ui.layout.DockLayout;
import consulo.ui.model.FlatDataModel;
import consulo.ui.model.MutableFlatDataModel;
import consulo.util.lang.StringUtil;
import org.jspecify.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Unified UI (works in desktop and web) counterpart of {@link PerlInjectionMarkersTable}: marker text to injected language mappings.
 */
final class PerlUnifiedInjectionMarkersConfigurable implements Configurable, Configurable.NoScroll {
  private final Project myProject;
  private final LanguageRegistry myLanguageRegistry;

  private @Nullable MutableFlatDataModel<Item> myModel;

  PerlUnifiedInjectionMarkersConfigurable(Project project) {
    myProject = project;
    myLanguageRegistry = project.getApplication().getInstance(LanguageRegistry.class);
  }

  @Override
  public LocalizeValue getDisplayName() {
    return LocalizeValue.of(PerlBundle.message("perl.settings.markers.label.text"));
  }

  @RequiredUIAccess
  @Override
  public Component createUIComponent(Disposable uiDisposable) {
    MutableFlatDataModel<Item> model = FlatDataModel.of(List.of());
    myModel = model;

    List<String> languageIds = myLanguageRegistry.getLanguages().stream()
      .map(Language::getID)
      .filter(it -> !it.isEmpty())
      .sorted()
      .toList();

    Table<Item> table = Table.create(model);
    table.addColumn(LocalizeValue.of(PerlBundle.message("perl.settings.markers.column.marker.title")), item -> item.myMarker)
      .setWidth(200)
      .setSortable(Comparator.naturalOrder())
      .setEditor(new TableItemEditor<>() {
        @RequiredUIAccess
        @Override
        public ValueComponent<String> createComponent(Item item) {
          return TextBox.create(item.myMarker);
        }

        @RequiredUIAccess
        @Override
        public void commit(Item item, @Nullable String value) {
          item.myMarker = StringUtil.notNullize(value);
          model.update(item);
        }
      });
    table.addColumn(LocalizeValue.of(PerlBundle.message("perl.settings.markers.column.language.title")), item -> item.myLanguageId)
      .setWidth(600)
      .setSortable(Comparator.comparing(languageId -> getLanguagePresentation(languageId).get()))
      .setRender((presentation, item) -> {
        String languageId = item.getValue();
        if (languageId != null) {
          presentation.append(getLanguagePresentation(languageId));
        }
      })
      .setEditor(new TableItemEditor<>() {
        @RequiredUIAccess
        @Override
        public ValueComponent<String> createComponent(Item item) {
          ComboBox<String> comboBox = ComboBox.create(languageIds);
          comboBox.setTextRenderer(languageId -> languageId == null ? LocalizeValue.empty() : getLanguagePresentation(languageId));
          comboBox.setValue(item.myLanguageId);
          return comboBox;
        }

        @RequiredUIAccess
        @Override
        public void commit(Item item, @Nullable String value) {
          if (value != null) {
            item.myLanguageId = value;
            model.update(item);
          }
        }
      });

    Component tablePanel = ToolbarDecoratorBuilderFactory.getInstance()
      .create(table)
      .addOrReplaceAction(new AddAction<>() {
        @RequiredUIAccess
        @Override
        protected void doAdd(AnActionEvent e) {
          addMarker(table, model);
        }
      })
      .disableAction(EditAction.class)
      .disableAction(UpMoveAction.class)
      .disableAction(DownMoveAction.class)
      .build();

    DockLayout layout = DockLayout.create();
    layout.top(Label.create(LocalizeValue.of(PerlBundle.message("perl.settings.markers.language.description"))));
    layout.center(tablePanel);
    return layout;
  }

  /**
   * upstream: selects a row with the empty marker if any, or adds a new one, and starts editing of its marker.
   * TODO consulo: unified table has no API to start editing of a cell, the row is selected only
   */
  @RequiredUIAccess
  private static void addMarker(Table<Item> table, MutableFlatDataModel<Item> model) {
    Item itemToEdit = null;
    for (Item item : model) {
      if (StringUtil.isEmpty(item.myMarker)) {
        itemToEdit = item;
        break;
      }
    }

    if (itemToEdit == null) {
      itemToEdit = new Item("marker" + model.getSize(), HTMLLanguage.INSTANCE.getID());
      model.add(itemToEdit);
    }

    table.select(itemToEdit);
    table.scrollTo(itemToEdit);
  }

  private LocalizeValue getLanguagePresentation(String languageId) {
    Language language = myLanguageRegistry.findLanguage(languageId);
    return language == null ?
           LocalizeValue.of(PerlBundle.message("perl.settings.markers.language.unsupported", languageId)) :
           language.getDisplayName();
  }

  @RequiredUIAccess
  @Override
  public boolean isModified() {
    MutableFlatDataModel<Item> model = myModel;
    return model != null && !toMappings(computeItems()).equals(toMappings(model));
  }

  private List<Item> computeItems() {
    return PerlInjectionMarkersService.getInstance(myProject).computeMergedMarkersMap().entrySet().stream()
      .filter(it -> myLanguageRegistry.findLanguage(it.getValue()) != null)
      .map(it -> new Item(it.getKey(), it.getValue()))
      .toList();
  }

  /**
   * upstream compares sets of items, items are mutable here (edited in place), so they are compared by value
   */
  private static Set<Map.Entry<String, String>> toMappings(Iterable<Item> items) {
    Set<Map.Entry<String, String>> result = new HashSet<>();
    for (Item item : items) {
      result.add(Map.entry(item.myMarker, item.myLanguageId));
    }
    return result;
  }

  @RequiredUIAccess
  @Override
  public void reset() {
    MutableFlatDataModel<Item> model = myModel;
    if (model != null) {
      model.replaceAll(computeItems());
    }
  }

  @RequiredUIAccess
  @Override
  public void apply() {
    MutableFlatDataModel<Item> model = myModel;
    if (model == null || !isModified()) {
      return;
    }

    Map<String, String> result = new HashMap<>();
    for (Item item : model) {
      result.putIfAbsent(item.myMarker, item.myLanguageId);
    }
    PerlInjectionMarkersService.getInstance(myProject).setCustomMarkersMap(result);
    FileContentUtil.reparseOpenedFiles();
  }

  @RequiredUIAccess
  @Override
  public void disposeUIResources() {
    myModel = null;
  }

  /**
   * Mutable row of the table, identity is used by the table model
   */
  private static final class Item {
    private String myMarker;
    private String myLanguageId;

    private Item(String marker, String languageId) {
      myMarker = marker;
      myLanguageId = languageId;
    }
  }
}
