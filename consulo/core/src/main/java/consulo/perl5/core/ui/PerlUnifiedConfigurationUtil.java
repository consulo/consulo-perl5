package consulo.perl5.core.ui;

import com.perl5.PerlBundle;
import consulo.language.file.FileTypeManager;
import consulo.language.file.LanguageFileType;
import consulo.localize.LocalizeValue;
import consulo.ui.Component;
import consulo.ui.HasSize;
import consulo.ui.Label;
import consulo.ui.ListBox;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.action.ActionGroup;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.DumbAwareAction;
import consulo.ui.ex.popup.JBPopupFactory;
import consulo.ui.ex.toolbar.AddAction;
import consulo.ui.ex.toolbar.DownMoveAction;
import consulo.ui.ex.toolbar.EditAction;
import consulo.ui.ex.toolbar.ToolbarDecoratorBuilderFactory;
import consulo.ui.ex.toolbar.UpMoveAction;
import consulo.ui.model.MutableFlatDataModel;
import consulo.virtualFileSystem.fileType.FileNameMatcher;
import consulo.virtualFileSystem.fileType.FileType;
import consulo.virtualFileSystem.fileType.matcher.ExtensionFileNameMatcher;

/**
 * Unified UI counterparts of the Swing parts of upstream {@code PerlConfigurationUtil}
 */
public final class PerlUnifiedConfigurationUtil {
  /**
   * Height of lists and tables of settings panels, as upstream {@code PerlConfigurationUtil.WIDGET_HEIGHT} sized Swing ones
   */
  private static final int WIDGET_HEIGHT = 120;

  private PerlUnifiedConfigurationUtil() {
  }

  /**
   * @return list of the extensions of language file types (presentable strings of their matchers), which should be handled as a
   * templating language; the extensions are added from a popup with the extensions not in the {@code model} yet
   */
  @RequiredUIAccess
  public static Component createSubstitutedExtensionsComponent(MutableFlatDataModel<String> model) {
    return withHeight(ToolbarDecoratorBuilderFactory.getInstance()
                        .create(ListBox.create(model))
                        .addOrReplaceAction(new AddAction<>() {
                          @RequiredUIAccess
                          @Override
                          protected void doAdd(AnActionEvent e) {
                            showSubstitutedExtensionsPopup(model, e);
                          }
                        })
                        .disableAction(EditAction.class)
                        .disableAction(UpMoveAction.class)
                        .disableAction(DownMoveAction.class)
                        .build());
  }

  /**
   * @return label with the {@code text} as is: unified labels take the first {@code _} or {@code &} for a mnemonic marker, e.g. upstream
   * messages mention options like {@code allow_globals}
   */
  @RequiredUIAccess
  public static Label createLabel(String text) {
    return Label.create(LocalizeValue.of(text.replace("_", "__").replace("&", "&&")));
  }

  /**
   * Sets the height of a decorated list or table: they are stacked in settings panels
   */
  @RequiredUIAccess
  public static Component withHeight(Component component) {
    if (component instanceof HasSize hasSize) {
      hasSize.setHeight(WIDGET_HEIGHT);
    }
    return component;
  }

  /**
   * The extensions are listed in an action group popup: a list popup of so many items is shrunk to a single row on desktop
   */
  @RequiredUIAccess
  private static void showSubstitutedExtensionsPopup(MutableFlatDataModel<String> model, AnActionEvent event) {
    FileTypeManager fileTypeManager = FileTypeManager.getInstance();
    ActionGroup.Builder group = ActionGroup.newImmutableBuilder();
    for (FileType fileType : fileTypeManager.getRegisteredFileTypes()) {
      if (fileType instanceof LanguageFileType) {
        for (FileNameMatcher matcher : fileTypeManager.getAssociations(fileType)) {
          String extension = matcher.getPresentableString();
          if (matcher instanceof ExtensionFileNameMatcher && model.indexOf(extension) < 0) {
            group.add(new DumbAwareAction(LocalizeValue.of(extension), LocalizeValue.empty(), fileType.getIcon()) {
              @RequiredUIAccess
              @Override
              public void actionPerformed(AnActionEvent e) {
                model.add(extension);
              }
            });
          }
        }
      }
    }

    JBPopupFactory.getInstance()
      .createActionGroupPopup(PerlBundle.message("popup.title.select.extension"), group.build(), event.getDataContext(),
                              JBPopupFactory.ActionSelectionAid.SPEEDSEARCH, false)
      .showUnderneathOf(event);
  }
}
