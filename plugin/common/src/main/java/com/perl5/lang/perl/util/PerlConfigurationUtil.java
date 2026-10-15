/*
 * Copyright 2015-2025 Alexandr Evstigneev
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

package com.perl5.lang.perl.util;


import consulo.ui.ex.popup.JBPopupFactory;
import consulo.ui.ex.popup.PopupStep;
import consulo.ui.ex.popup.BaseListPopupStep;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.awt.AnActionButton;
import consulo.ui.ex.awt.CollectionListModel;
import consulo.ui.ex.awt.ToolbarDecorator;
import consulo.ui.ex.awt.JBList;
import consulo.ui.ex.awt.JBUI;
import com.perl5.PerlBundle;
import consulo.ui.image.Image;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import consulo.virtualFileSystem.fileType.matcher.ExtensionFileNameMatcher;
import consulo.virtualFileSystem.fileType.FileNameMatcher;
import consulo.virtualFileSystem.fileType.FileType;
import consulo.language.file.FileTypeManager;
import consulo.language.file.LanguageFileType;


public class PerlConfigurationUtil {
  public static final int WIDGET_HEIGHT = 90;

  /**
   * consulo: shared by the per-module Template Toolkit and HTML::Mason settings, upstream computes the matchers in each settings service
   *
   * @param substitutedExtensions presentable strings of the matchers, as shown by {@link #createSubstituteExtensionPanel}
   * @return file name matchers of the language file types with the {@code substitutedExtensions} presentable strings
   */
  public static @NotNull List<FileNameMatcher> getSubstitutedExtensionsMatchers(@NotNull Collection<String> substitutedExtensions) {
    List<FileNameMatcher> result = new ArrayList<>();
    if (substitutedExtensions.isEmpty()) {
      return result;
    }
    FileTypeManager fileTypeManager = FileTypeManager.getInstance();
    for (FileType fileType : fileTypeManager.getRegisteredFileTypes()) {
      if (fileType instanceof LanguageFileType) {
        for (FileNameMatcher matcher : fileTypeManager.getAssociations(fileType)) {
          if (substitutedExtensions.contains(matcher.getPresentableString())) {
            result.add(matcher);
          }
        }
      }
    }
    return result;
  }

  public static JPanel createSubstituteExtensionPanel(final @NotNull CollectionListModel<String> substitutedExtensionsModel,
                                                      final @NotNull JBList<String> substitutedExtensionsList
  ) {
    return ToolbarDecorator
      .createDecorator(substitutedExtensionsList)
      // consulo: AnActionButton#getPreferredPopupPoint() does not exist, popup is shown under the action event source instead
      .setAddAction(new BiConsumer<AnActionButton, AnActionEvent>() {
        @Override
        public void accept(AnActionButton anActionButton, AnActionEvent event) {
          FileTypeManager fileTypeManager = FileTypeManager.getInstance();
          final List<String> currentItems = substitutedExtensionsModel.getItems();
          List<FileNameMatcher> possibleItems = new ArrayList<>();
          List<Image> itemsIcons = new ArrayList<>();

          for (FileType fileType : fileTypeManager.getRegisteredFileTypes()) {
            if (fileType instanceof LanguageFileType) {
              for (FileNameMatcher matcher : fileTypeManager.getAssociations(fileType)) {
                if (matcher instanceof ExtensionFileNameMatcher) {
                  String presentableString = matcher.getPresentableString();
                  if (!currentItems.contains(presentableString)) {
                    possibleItems.add(matcher);
                    itemsIcons.add(fileType.getIcon());
                  }
                }
              }
            }
          }

          BaseListPopupStep<FileNameMatcher> fileNameMatcherBaseListPopupStep =
            new BaseListPopupStep<>(PerlBundle.message("popup.title.select.extension"), possibleItems, itemsIcons) {
              @Override
              public PopupStep<?> onChosen(FileNameMatcher selectedValue, boolean finalChoice) {
                substitutedExtensionsModel.add(selectedValue.getPresentableString());
                //noinspection unchecked
                return super.onChosen(selectedValue, finalChoice);
              }
            };

          JBPopupFactory.getInstance()
            .createListPopup(fileNameMatcherBaseListPopupStep)
            .showUnderneathOf(event);
        }
      })
      .disableDownAction()
      .disableUpAction()
      .setPreferredSize(JBUI.size(0, PerlConfigurationUtil.WIDGET_HEIGHT))
      .createPanel();
  }
}
