package consulo.perl5.core.sdk;

import com.perl5.PerlBundle;
import com.perl5.lang.perl.idea.sdk.host.PerlHostHandler;
import com.perl5.lang.perl.idea.sdk.versionManager.PerlVersionManagerHandler;
import consulo.application.progress.Task;
import consulo.content.bundle.Sdk;
import consulo.content.bundle.SdkModelFactory;
import consulo.dataContext.DataManager;
import consulo.disposer.Disposable;
import consulo.ide.setting.bundle.SettingsSdksModel;
import consulo.localize.LocalizeValue;
import consulo.logging.Logger;
import consulo.module.ui.BundleBox;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.project.Project;
import consulo.ui.Button;
import consulo.ui.UIAccess;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.action.ActionGroup;
import consulo.ui.ex.action.DumbAwareAction;
import consulo.ui.ex.popup.JBPopupFactory;
import consulo.ui.ex.popup.ListPopup;
import consulo.ui.model.FlatDataModel;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * "Add..." button with a popup menu creating perl interpreters the same way as upstream {@code Perl5SdkConfigurable} does: an item per
 * applicable host handler and version manager handler.
 */
public final class PerlAddInterpreterButton {
  private static final Logger LOG = Logger.getInstance(PerlAddInterpreterButton.class);

  private PerlAddInterpreterButton() {
  }

  /**
   * Unified UI button
   *
   * @param sdkConsumer invoked in the UI thread with the created interpreter, which is already added to the sdk table
   */
  @RequiredUIAccess
  public static Button create(@Nullable Project project, Disposable uiDisposable, @RequiredUIAccess Consumer<Sdk> sdkConsumer) {
    Button addButton = Button.create(LocalizeValue.localizeTODO("Add..."), event -> createPopup(project, uiDisposable, sdkConsumer).showBy(event));
    addButton.setIcon(PlatformIconGroup.generalAdd());
    return addButton;
  }

  @RequiredUIAccess
  private static ListPopup createPopup(@Nullable Project project, Disposable uiDisposable, @RequiredUIAccess Consumer<Sdk> sdkConsumer) {
    ActionGroup.Builder group = ActionGroup.newImmutableBuilder();
    forEachCreator(project, uiDisposable, sdkConsumer,
                   (title, action) -> group.add(DumbAwareAction.create(LocalizeValue.of(title), e -> action.run())));
    return JBPopupFactory.getInstance()
      .createActionGroupPopup(null, group.build(), DataManager.getInstance().getDataContext(), JBPopupFactory.ActionSelectionAid.MNEMONICS,
                              false);
  }

  /**
   * Adds (if needed: a box built over another sdk model misses it) and selects the created interpreter in the {@code bundleBox}
   */
  @RequiredUIAccess
  public static void selectCreatedSdk(BundleBox bundleBox, Sdk sdk) {
    FlatDataModel<BundleBox.BundleBoxItem> model = bundleBox.getComponent().getDataModel();
    boolean found = false;
    for (int i = 0; i < model.getSize(); i++) {
      BundleBox.BundleBoxItem item = model.get(i);
      if (item.getBundle() != null && sdk.getName().equals(item.getBundleName())) {
        found = true;
        break;
      }
    }
    if (!found) {
      bundleBox.addBundleItem(sdk);
    }
    bundleBox.setSelectedBundle(sdk.getName());
  }

  /**
   * Passes menu items to the {@code itemConsumer}: title and action, which creates an interpreter in a background task (a modal one blocks
   * the (web) UI thread which the interpreter chooser needs)
   */
  @RequiredUIAccess
  private static void forEachCreator(@Nullable Project project,
                                     Disposable uiDisposable,
                                     @RequiredUIAccess Consumer<Sdk> sdkConsumer,
                                     BiConsumer<String, Runnable> itemConsumer) {
    UIAccess uiAccess = UIAccess.current();
    List<? extends PerlHostHandler<?, ?>> hostHandlers = PerlHostHandler.stream().filter(PerlHostHandler::isApplicable).toList();
    boolean severalHosts = hostHandlers.size() > 1;
    for (PerlHostHandler<?, ?> hostHandler : hostHandlers) {
      PerlVersionManagerHandler.stream().filter(it -> it.isApplicable(hostHandler.getOsHandler())).forEach(versionManagerHandler -> {
        String title = severalHosts
                       ? hostHandler.getMenuItemTitle() + ": " + versionManagerHandler.getMenuItemTitle()
                       : versionManagerHandler.getMenuItemTitle();
        itemConsumer.accept(title, () -> Task.Backgroundable.queue(
          project, LocalizeValue.of(PerlBundle.message("perl.create.interpreter.progress")),
          indicator -> versionManagerHandler.createSdkInteractively(
            project, hostHandler, sdk -> uiAccess.give(() -> {
              addToSettingsSdkModel(sdk);
              sdkConsumer.accept(sdk);
            }), uiDisposable)));
      });
    }
  }

  /**
   * Interpreters are added to the sdk table directly, but the sdk model shared by settings UI (bundle boxes, module extensions settings)
   * is a snapshot of the table, taken on its first use and reset only by the SDKs settings page. Registers the interpreter there the same
   * way the snapshot holds table sdks (original to a copy) - otherwise the settings show it as missing and applying the model removes it
   * from the table.
   */
  @RequiredUIAccess
  private static void addToSettingsSdkModel(Sdk sdk) {
    if (!(SdkModelFactory.getInstance().getOrCreateModel() instanceof SettingsSdksModel sdksModel)) {
      return;
    }
    Map<Sdk, Sdk> modelSdks = sdksModel.getModifiedSdksMap();
    if (modelSdks.containsKey(sdk)) {
      return;
    }
    try {
      Sdk modelSdk = (Sdk)sdk.clone();
      modelSdks.put(sdk, modelSdk);
      sdksModel.getMulticaster().sdkAdded(modelSdk);
    }
    catch (CloneNotSupportedException e) {
      LOG.error(e);
    }
  }
}
