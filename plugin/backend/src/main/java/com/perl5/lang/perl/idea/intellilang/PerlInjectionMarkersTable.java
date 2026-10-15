/*
 * Copyright 2015-2026 Alexandr Evstigneev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.perl5.lang.perl.idea.intellilang;

import consulo.language.Language;
import consulo.html.language.HTMLLanguage;
import consulo.configurable.Configurable;
import consulo.project.Project;
import consulo.ui.ex.awt.table.ComboBoxTableRenderer;
import consulo.ui.ex.awt.VerticalFlowLayout;
import consulo.util.lang.StringUtil;
import consulo.ui.ex.awt.util.TableUtil;
import consulo.ui.ex.awt.ToolbarDecorator;
import consulo.ui.ex.awt.table.JBTable;
import consulo.util.collection.ArrayUtil;
import consulo.fileEditor.util.FileContentUtil;
import consulo.ui.ex.awt.ColumnInfo;
import consulo.ui.ex.awt.FormBuilder;
import consulo.ui.ex.awt.table.ListTableModel;
import consulo.localize.LocalizeValue;
import com.perl5.PerlBundle;
import consulo.disposer.Disposable;
import consulo.ui.Component;
import consulo.ui.ex.awtUnsafe.TargetAWT;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.stream.Collectors;

// TODO consulo: Swing (consulo.ui.ex.awt) table based configurable, not rewritten with uni UI yet
final class PerlInjectionMarkersTable extends JBTable implements Configurable {
  private final @NotNull Project myProject;

  public PerlInjectionMarkersTable(@NotNull Project project) {
    super(new MyModel());
    myProject = project;

    final TableColumn languageColumn = getColumnModel().getColumn(1);

    List<String> languageIds = Language.getRegisteredLanguages().stream()
      .map(Language::getID)
      .filter(it -> !it.isEmpty())
      .sorted()
      .collect(Collectors.toList());
    ComboBoxTableRenderer<String> roleComboBoxTableRenderer =
      new ComboBoxTableRenderer<>(ArrayUtil.toStringArray(languageIds)) {
        @Override
        public boolean isCellEditable(EventObject event) {
          if (event instanceof MouseEvent mouseEvent) {
            return mouseEvent.getClickCount() >= 1;
          }

          return true;
        }
      };
    languageColumn.setCellRenderer(roleComboBoxTableRenderer);
    languageColumn.setCellEditor(roleComboBoxTableRenderer);
    getColumnModel().getColumn(0).setPreferredWidth(200);
    languageColumn.setPreferredWidth(600);
    // TODO consulo: Consulo JBTable has no setVisibleRowCount(18), the preferred viewport height is fixed to 18 rows instead
    setPreferredScrollableViewportSize(new Dimension(getPreferredScrollableViewportSize().width, 18 * getRowHeight()));
  }

  @Override
  public @NotNull LocalizeValue getDisplayName() {
    return LocalizeValue.localizeTODO(PerlBundle.message("perl.settings.markers.label.text"));
  }

  @Override
  public Component createUIComponent(Disposable parentDisposable) {
    JTextPane textPane = new JTextPane();
    textPane.setText(PerlBundle.message("perl.settings.markers.language.description"));
    JPanel panel = FormBuilder.createFormBuilder()
      .addComponent(textPane)
      .addComponent(
        ToolbarDecorator.createDecorator(this).setAddAction(action -> {
          final TableCellEditor cellEditor = getCellEditor();
          if (cellEditor != null) {
            cellEditor.stopCellEditing();
          }
            var model = getMyModel();

          int indexToEdit = -1;

          for (Item item : model.getItems()) {
            if (StringUtil.isEmpty(item.marker)) {
              indexToEdit = model.indexOf(item);
              break;
            }
          }

          if (indexToEdit == -1) {
            model.addRow(new Item("marker" + model.getItems().size(), HTMLLanguage.INSTANCE.getID()));
            indexToEdit = model.getRowCount() - 1;
          }

          TableUtil.editCellAt(this, indexToEdit, 0);
        })
          .disableDownAction()
          .disableUpAction()
          .createPanel()
      ).getPanel();
    panel.setLayout(new VerticalFlowLayout());
    return TargetAWT.wrap(panel);
  }

  private MyModel getMyModel() {
    return (MyModel)super.getModel();
  }

  @Override
  public boolean isModified() {
    return !new HashSet<>(computeItems()).equals(new HashSet<>(getMyModel().getItems()));
  }

  private List<Item> computeItems() {
    return PerlInjectionMarkersService.getInstance(myProject).computeMergedMarkersMap().entrySet().stream()
      .filter(it -> Language.findLanguageByID(it.getValue()) != null)
      .map(it -> new Item(it.getKey(), it.getValue()))
      .collect(Collectors.toList());
  }

  @Override
  public void reset() {
    getMyModel().setItems(computeItems());
  }

  @Override
  public void apply() {
    if (!isModified()) {
      return;
    }

    Map<String, String> result = new HashMap<>();
    getMyModel().getItems().forEach(item -> result.putIfAbsent(item.marker, item.languageId));
    PerlInjectionMarkersService.getInstance(myProject).setCustomMarkersMap(result);
    FileContentUtil.reparseOpenedFiles();
  }

  private static class Item {
    @NotNull
    String marker;
    @NotNull
    String languageId;

    public Item(@NotNull String marker, @NotNull String languageId) {
      this.marker = marker;
      this.languageId = languageId;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      Item item = (Item)o;

      if (!marker.equals(item.marker)) {
        return false;
      }
      return languageId.equals(item.languageId);
    }

    @Override
    public int hashCode() {
      int result = marker.hashCode();
      result = 31 * result + languageId.hashCode();
      return result;
    }
  }

  private static class MyModel extends ListTableModel<Item> {
    public MyModel() {
      super(new MarkerColumnInfo(), new LanguageColumnInfo());
      setSortable(true);
    }
  }

  private static class MarkerColumnInfo extends ColumnInfo<Item, String> {
    public MarkerColumnInfo() {
      super(PerlBundle.message("perl.settings.markers.column.marker.title"));
    }

    @Override
    public @Nullable Comparator<Item> getComparator() {
      return Comparator.comparing(it -> it.marker);
    }

    @Override
    public boolean isCellEditable(Item item) {
      return true;
    }

    @Override
    public @Nullable String valueOf(Item item) {
      return item.marker;
    }

    @Override
    public void setValue(Item item, String value) {
      item.marker = value;
    }
  }

  private static class LanguageColumnInfo extends ColumnInfo<Item, String> {
    public LanguageColumnInfo() {
      super(PerlBundle.message("perl.settings.markers.column.language.title"));
    }

    @Override
    public boolean isCellEditable(Item item) {
      return true;
    }

    @Override
    public @Nullable Comparator<Item> getComparator() {
      return Comparator.comparing(this::valueOf);
    }

    @Override
    public void setValue(Item item, String value) {
      item.languageId = value;
    }

    @Override
    public @NotNull String valueOf(Item item) {
      Language language = Language.findLanguageByID(item.languageId);
      return language == null ?
             PerlBundle.message("perl.settings.markers.language.unsupported", item.languageId) :
             language.getDisplayName().get();
    }
  }
}
