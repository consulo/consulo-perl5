package com.perl5.lang.perl.idea.configuration.settings.sdk;

import com.perl5.PerlBundle;
import com.perl5.PerlIcons;
import com.perl5.lang.perl.idea.configuration.settings.PerlLocalSettings;
import com.perl5.lang.perl.idea.configuration.settings.PerlSharedSettings;
import com.perl5.lang.perl.idea.project.PerlProjectManager;
import com.perl5.lang.perl.idea.sdk.host.PerlHostHandler;
import com.perl5.lang.perl.internals.PerlVersion;
import consulo.configurable.Configurable;
import consulo.configurable.ConfigurationException;
import consulo.configurable.UnnamedConfigurable;
import consulo.content.bundle.Sdk;
import consulo.disposer.Disposable;
import consulo.fileChooser.FileChooser;
import consulo.fileChooser.FileChooserDescriptor;
import consulo.fileChooser.FileChooserDescriptorFactory;
import consulo.fileEditor.util.FileContentUtil;
import consulo.language.util.ModuleUtilCore;
import consulo.localize.LocalizeValue;
import consulo.perl5.core.module.PerlModuleSettingsUtil;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.process.cmd.ParametersListUtil;
import consulo.project.Project;
import consulo.ui.CheckBox;
import consulo.ui.ComboBox;
import consulo.ui.Component;
import consulo.ui.Hyperlink;
import consulo.ui.InputBoxBuilder;
import consulo.ui.Label;
import consulo.ui.ListBox;
import consulo.ui.MessageBoxes;
import consulo.ui.Space;
import consulo.ui.TextBox;
import consulo.ui.TextBoxWithExpandAction;
import consulo.ui.UIAccess;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.toolbar.AddAction;
import consulo.ui.ex.toolbar.DownMoveAction;
import consulo.ui.ex.toolbar.EditAction;
import consulo.ui.ex.toolbar.ToolbarDecoratorBuilder;
import consulo.ui.ex.toolbar.ToolbarDecoratorBuilderFactory;
import consulo.ui.ex.toolbar.UpMoveAction;
import consulo.ui.layout.HorizontalLayout;
import consulo.ui.layout.LabeledLayout;
import consulo.ui.layout.VerticalLayout;
import consulo.ui.model.FlatDataModel;
import consulo.ui.model.MutableFlatDataModel;
import consulo.ui.util.FormBuilder;
import consulo.util.io.FileUtil;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.VirtualFile;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Unified UI (works in desktop and web) counterpart of {@link Perl5ProjectConfigurable}: project-level perl settings. Same options and
 * same apply/reset/isModified semantics as upstream.
 * <p>
 * TODO consulo: upstream has the project interpreter selector here, in Consulo interpreters are configured per module with the Perl
 *  module extension (Project Structure), the page shows a link to the module settings instead. Interpreter host settings (e.g. Docker)
 *  are shown for the host of the project interpreter ({@link PerlProjectManager#getProjectSdk()}).
 */
public class Perl5UnifiedProjectConfigurable implements Configurable {
  private final Project myProject;
  private final PerlSharedSettings mySharedSettings;
  private final PerlLocalSettings myLocalSettings;

  private @Nullable UnnamedConfigurable myHostProjectConfigurable;

  private @Nullable Form myForm;

  public Perl5UnifiedProjectConfigurable(Project project) {
    myProject = project;
    mySharedSettings = PerlSharedSettings.getInstance(myProject);
    myLocalSettings = PerlLocalSettings.getInstance(myProject);
  }

  @Override
  public LocalizeValue getDisplayName() {
    return LocalizeValue.of(PerlBundle.message("perl.perl5"));
  }

  @RequiredUIAccess
  @Override
  public Component createUIComponent(Disposable uiDisposable) {
    Form form = new Form();
    myForm = form;

    VerticalLayout root = VerticalLayout.create();
    root.add(createInterpreterRow());
    createHostSettings(form, uiDisposable);
    root.add(form.mySdkProjectSettingsPanel);

    root.add(FormBuilder.create()
               .addLabeled(LocalizeValue.of(PerlBundle.message("perl.config.language.level")), form.myTargetPerlVersionComboBox)
               .build());

    root.add(LabeledLayout.create(LocalizeValue.of(PerlBundle.message("perl.settings.external.libs")),
                                  decorate(form.myLibsList, this::doAddExternalLibrary, true)));

    root.add(form.mySimpleMainCheckbox);
    root.add(form.myAutoInjectionCheckbox);
    root.add(form.myAllowInjectionWithInterpolation);
    root.add(form.myAllowRegexpInjections);
    // upstream: perlAnnotatorCheckBox is not added to the panel
    root.add(form.myPerlCriticCheckBox);
    root.add(form.myEnablePerlSwitchCheckbox);

    addLabeledOnTop(root, LocalizeValue.of(PerlBundle.message("perl.config.critic.cmd.arguments")), form.myPerlCriticArgsInputField);
    addLabeledOnTop(root, LocalizeValue.of(PerlBundle.message("perl.config.tidy.options.label")), form.myPerlTidyArgsInputField);

    root.add(FormBuilder.create()
               .addLabeled(LocalizeValue.of(PerlBundle.message("perl.config.deparse.options.label")), form.myDeparseArgumentsTextField)
               .build());

    root.add(LabeledLayout.create(
      LocalizeValue.of(PerlBundle.message("perl.config.strict.provider.label")),
      decorate(form.myStrictProvidersList,
               () -> addModuleList(LocalizeValue.of(PerlBundle.message("perl.config.add.strict.provider.title")),
                                   form.myStrictProvidersModel, form.myStrictProvidersList),
               false)));

    root.add(LabeledLayout.create(
      LocalizeValue.of(PerlBundle.message("perl.config.warnings.provider.label")),
      decorate(form.myWarningsProvidersList,
               () -> addModuleList(LocalizeValue.of(PerlBundle.message("perl.config.add.warnings.provider.title")),
                                   form.myWarningsProvidersModel, form.myWarningsProvidersList),
               false)));

    root.add(LabeledLayout.create(LocalizeValue.of(PerlBundle.message("perl.config.self.names.label")),
                                  decorate(form.mySelfNamesList, this::addSelfName, true)));
    return root;
  }

  /**
   * Replaces upstream interpreter selector
   */
  @RequiredUIAccess
  private Component createInterpreterRow() {
    HorizontalLayout layout = HorizontalLayout.create(Space.SMALL);
    layout.add(Label.create(LocalizeValue.localizeTODO("Perl interpreter is configured per module in Project Structure.")));
    layout.add(Hyperlink.create(
      LocalizeValue.localizeTODO("Configure..."),
      event -> PerlModuleSettingsUtil.selectInterpreterSettings(myProject, PerlModuleSettingsUtil.findModuleToConfigure(myProject))));
    return layout;
  }

  /**
   * Adds the panel for interpreter host settings of the project interpreter if available. E.g. Docker settings
   */
  @RequiredUIAccess
  private void createHostSettings(Form form, Disposable uiDisposable) {
    Sdk projectSdk = PerlProjectManager.getInstance(myProject).getProjectSdk();
    PerlHostHandler<?, ?> hostHandler = projectSdk == null ? null : PerlHostHandler.from(projectSdk);
    UnnamedConfigurable hostProjectConfigurable = hostHandler == null ? null : hostHandler.getSettingsConfigurable(myProject);
    myHostProjectConfigurable = hostProjectConfigurable;
    if (hostProjectConfigurable == null) {
      form.mySdkProjectSettingsPanel.setVisible(false);
      return;
    }
    Component component = hostProjectConfigurable.createUIComponent(uiDisposable);
    if (component == null) {
      // TODO consulo: host settings configurable provides only Swing UI (e.g. docker), which can't be shown by unified UI
      component = Label.create(LocalizeValue.localizeTODO("Not supported UI"));
    }
    form.mySdkProjectSettingsPanel.add(component);
    form.mySdkProjectSettingsPanel.setVisible(true);
  }

  @RequiredUIAccess
  private static void addLabeledOnTop(VerticalLayout root, LocalizeValue labelText, Component component) {
    // upstream: LabeledComponent, label above the field
    Label label = Label.create(labelText);
    label.setTarget(component);
    root.add(label);
    root.add(component);
  }

  @RequiredUIAccess
  private static <E> Component decorate(ListBox<E> list, Runnable addAction, boolean allowMove) {
    ToolbarDecoratorBuilder<E> builder = ToolbarDecoratorBuilderFactory.getInstance()
      .create(list)
      .addOrReplaceAction(new AddAction<>() {
        @RequiredUIAccess
        @Override
        protected void doAdd(AnActionEvent e) {
          addAction.run();
        }
      })
      .disableAction(EditAction.class);
    if (!allowMove) {
      builder.disableAction(UpMoveAction.class).disableAction(DownMoveAction.class);
    }
    return builder.build();
  }

  @RequiredUIAccess
  private void addSelfName() {
    Form form = myForm;
    if (form == null) {
      return;
    }
    UIAccess uiAccess = UIAccess.current();
    InputBoxBuilder.text()
      .title(LocalizeValue.of(PerlBundle.message("perl.config.self.add.title")))
      .text(LocalizeValue.of(PerlBundle.message("perl.config.self.add.text")))
      .asQuestion()
      .value("")
      .showAsync(form.mySelfNamesList)
      .whenComplete((value, error) -> {
        if (error != null || StringUtil.isEmpty(value)) {
          return;
        }
        uiAccess.giveIfNeed(() -> {
          String variableName = value;
          while (variableName.startsWith("$")) {
            variableName = variableName.substring(1);
          }

          if (StringUtil.isNotEmpty(variableName) && form.mySelfNamesModel.indexOf(variableName) < 0) {
            form.mySelfNamesModel.add(variableName);
          }
        });
      });
  }

  @RequiredUIAccess
  private static void addModuleList(LocalizeValue title, MutableFlatDataModel<String> model, ListBox<String> list) {
    UIAccess uiAccess = UIAccess.current();
    InputBoxBuilder.text()
      .title(title)
      .text(LocalizeValue.of(PerlBundle.message("perl.enter.comma.separated.values.to.add")))
      .asQuestion()
      .value("")
      .showAsync(list)
      .whenComplete((moduleNames, error) -> {
        if (error != null) {
          return;
        }
        uiAccess.giveIfNeed(() -> {
          List<String> items = toList(model);
          for (String moduleName : StringUtil.split(StringUtil.notNullize(moduleNames), ",")) {
            String trimmedName = moduleName.trim();
            if (StringUtil.isNotEmpty(trimmedName) && !items.contains(trimmedName)) {
              items.add(trimmedName);
            }
          }
          items.sort(Comparator.naturalOrder());
          model.replaceAll(items);
        });
      });
  }

  @RequiredUIAccess
  private void doAddExternalLibrary() {
    FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createMultipleFoldersDescriptor()
      .withTreeRootVisible(true)
      .withTitle(LocalizeValue.of(PerlBundle.message("perl.settings.select.libs")));
    UIAccess uiAccess = UIAccess.current();
    FileChooser.chooseFiles(descriptor, myProject, null).whenComplete((files, error) -> {
      if (error != null || files == null || files.length == 0) {
        return;
      }
      uiAccess.giveIfNeed(() -> processChosenFiles(Arrays.asList(files)));
    });
  }

  @RequiredUIAccess
  private void processChosenFiles(List<VirtualFile> virtualFiles) {
    Form form = myForm;
    if (form == null) {
      return;
    }
    boolean notifyInternals = false;

    List<VirtualFile> rootsToAdd = new ArrayList<>();

    for (VirtualFile chosenFile : virtualFiles) {
      VirtualFile virtualFile = chosenFile;
      if (!virtualFile.isValid()) {
        continue;
      }
      if (!virtualFile.isDirectory()) {
        virtualFile = virtualFile.getParent();
        if (virtualFile == null || !virtualFile.isValid()) {
          continue;
        }
      }

      if (ModuleUtilCore.findModuleForFile(virtualFile, myProject) != null) {
        notifyInternals = true;
        continue;
      }
      rootsToAdd.add(virtualFile);
    }

    for (VirtualFile root : rootsToAdd) {
      form.myLibsModel.add(root);
    }

    if (notifyInternals) {
      MessageBoxes.okError(LocalizeValue.of(PerlBundle.message("perl.settings.external.libs.internal.text")))
        .title(LocalizeValue.of(PerlBundle.message("perl.settings.external.libs.internal.title")))
        .showAsync(form.myLibsList);
    }
  }

  @RequiredUIAccess
  @Override
  public boolean isModified() {
    Form form = myForm;
    if (form == null) {
      return false;
    }
    return isLibsModified(form) ||
           mySharedSettings.SIMPLE_MAIN_RESOLUTION != isSelected(form.mySimpleMainCheckbox) ||
           mySharedSettings.AUTOMATIC_HEREDOC_INJECTIONS != isSelected(form.myAutoInjectionCheckbox) ||
           mySharedSettings.ALLOW_INJECTIONS_WITH_INTERPOLATION != isSelected(form.myAllowInjectionWithInterpolation) ||
           myLocalSettings.ENABLE_REGEX_INJECTIONS != isSelected(form.myAllowRegexpInjections) ||
           mySharedSettings.PERL_ANNOTATOR_ENABLED != isSelected(form.myPerlAnnotatorCheckBox) ||
           mySharedSettings.PERL_CRITIC_ENABLED != isSelected(form.myPerlCriticCheckBox) ||
           mySharedSettings.PERL_SWITCH_ENABLED != isSelected(form.myEnablePerlSwitchCheckbox) ||
           !mySharedSettings.getTargetPerlVersion().equals(form.myTargetPerlVersionComboBox.getValue()) ||
           !StringUtil.equals(mySharedSettings.PERL_DEPARSE_ARGUMENTS, getText(form.myDeparseArgumentsTextField)) ||
           !StringUtil.equals(mySharedSettings.PERL_CRITIC_ARGS, getText(form.myPerlCriticArgsInputField)) ||
           !StringUtil.equals(mySharedSettings.PERL_TIDY_ARGS, getText(form.myPerlTidyArgsInputField)) ||
           !mySharedSettings.selfNames.equals(toList(form.mySelfNamesModel)) ||
           !mySharedSettings.getStrictProviders().equals(toList(form.myStrictProvidersModel)) ||
           !mySharedSettings.getWarningsProviders().equals(toList(form.myWarningsProvidersModel)) ||
           myHostProjectConfigurable != null && myHostProjectConfigurable.isModified();
  }

  private boolean isLibsModified(Form form) {
    return !toList(form.myLibsModel).equals(PerlProjectManager.getInstance(myProject).getExternalLibraryRoots());
  }

  @RequiredUIAccess
  @Override
  public void reset() {
    Form form = myForm;
    if (form == null) {
      return;
    }
    if (myHostProjectConfigurable != null) {
      myHostProjectConfigurable.reset();
    }
    form.myLibsModel.replaceAll(new ArrayList<>(PerlProjectManager.getInstance(myProject).getExternalLibraryRoots()));
    form.mySelfNamesModel.replaceAll(new ArrayList<>(mySharedSettings.selfNames));

    List<String> strictProviders = new ArrayList<>(mySharedSettings.getStrictProviders());
    strictProviders.sort(Comparator.naturalOrder());
    form.myStrictProvidersModel.replaceAll(strictProviders);
    List<String> warningsProviders = new ArrayList<>(mySharedSettings.getWarningsProviders());
    warningsProviders.sort(Comparator.naturalOrder());
    form.myWarningsProvidersModel.replaceAll(warningsProviders);

    form.myTargetPerlVersionComboBox.setValue(mySharedSettings.getTargetPerlVersion());
    form.mySimpleMainCheckbox.setValue(mySharedSettings.SIMPLE_MAIN_RESOLUTION);
    form.myAutoInjectionCheckbox.setValue(mySharedSettings.AUTOMATIC_HEREDOC_INJECTIONS);
    form.myAllowInjectionWithInterpolation.setValue(mySharedSettings.ALLOW_INJECTIONS_WITH_INTERPOLATION);
    form.myAllowRegexpInjections.setValue(myLocalSettings.ENABLE_REGEX_INJECTIONS);
    form.myPerlAnnotatorCheckBox.setValue(mySharedSettings.PERL_ANNOTATOR_ENABLED);
    form.myDeparseArgumentsTextField.setValue(StringUtil.notNullize(mySharedSettings.PERL_DEPARSE_ARGUMENTS));
    form.myEnablePerlSwitchCheckbox.setValue(mySharedSettings.PERL_SWITCH_ENABLED);

    form.myPerlCriticCheckBox.setValue(mySharedSettings.PERL_CRITIC_ENABLED);
    form.myPerlCriticArgsInputField.setValue(StringUtil.notNullize(mySharedSettings.PERL_CRITIC_ARGS));

    form.myPerlTidyArgsInputField.setValue(StringUtil.notNullize(mySharedSettings.PERL_TIDY_ARGS));
  }

  @RequiredUIAccess
  @Override
  public void apply() throws ConfigurationException {
    Form form = myForm;
    if (form == null) {
      return;
    }
    if (myHostProjectConfigurable != null) {
      myHostProjectConfigurable.apply();
    }

    if (isLibsModified(form)) {
      PerlProjectManager.getInstance(myProject).setExternalLibraries(toList(form.myLibsModel));
    }
    boolean reparseOpenFiles = false;
    mySharedSettings.SIMPLE_MAIN_RESOLUTION = isSelected(form.mySimpleMainCheckbox);
    mySharedSettings.AUTOMATIC_HEREDOC_INJECTIONS = isSelected(form.myAutoInjectionCheckbox);
    mySharedSettings.ALLOW_INJECTIONS_WITH_INTERPOLATION = isSelected(form.myAllowInjectionWithInterpolation);
    mySharedSettings.PERL_ANNOTATOR_ENABLED = isSelected(form.myPerlAnnotatorCheckBox);
    mySharedSettings.setDeparseOptions(getText(form.myDeparseArgumentsTextField));
    PerlVersion targetPerlVersion = form.myTargetPerlVersionComboBox.getValue();
    if (targetPerlVersion != null) {
      mySharedSettings.setTargetPerlVersion(targetPerlVersion);
    }

    mySharedSettings.PERL_CRITIC_ENABLED = isSelected(form.myPerlCriticCheckBox);
    mySharedSettings.PERL_CRITIC_ARGS = getText(form.myPerlCriticArgsInputField);

    if (mySharedSettings.PERL_SWITCH_ENABLED != isSelected(form.myEnablePerlSwitchCheckbox)) {
      mySharedSettings.PERL_SWITCH_ENABLED = isSelected(form.myEnablePerlSwitchCheckbox);
      reparseOpenFiles = true;
    }

    if (myLocalSettings.ENABLE_REGEX_INJECTIONS != isSelected(form.myAllowRegexpInjections)) {
      myLocalSettings.ENABLE_REGEX_INJECTIONS = isSelected(form.myAllowRegexpInjections);
      reparseOpenFiles = true;
    }

    mySharedSettings.PERL_TIDY_ARGS = getText(form.myPerlTidyArgsInputField);

    mySharedSettings.selfNames.clear();
    mySharedSettings.selfNames.addAll(toList(form.mySelfNamesModel));

    mySharedSettings.setStrictProviders(toList(form.myStrictProvidersModel));
    mySharedSettings.setWarningsProviders(toList(form.myWarningsProvidersModel));

    mySharedSettings.settingsUpdated();

    if (reparseOpenFiles) {
      FileContentUtil.reparseOpenedFiles();
    }
  }

  @RequiredUIAccess
  @Override
  public void disposeUIResources() {
    myForm = null;
    if (myHostProjectConfigurable != null) {
      myHostProjectConfigurable.disposeUIResources();
      myHostProjectConfigurable = null;
    }
  }

  private static boolean isSelected(CheckBox checkBox) {
    return Boolean.TRUE.equals(checkBox.getValue());
  }

  private static String getText(TextBox textBox) {
    return StringUtil.notNullize(textBox.getValue());
  }

  private static <E> List<E> toList(FlatDataModel<E> model) {
    List<E> result = new ArrayList<>(model.getSize());
    for (E item : model) {
      result.add(item);
    }
    return result;
  }

  /**
   * Components of the page, exist between {@link #createUIComponent(Disposable)} and {@link #disposeUIResources()}
   */
  private static final class Form {
    private final VerticalLayout mySdkProjectSettingsPanel = VerticalLayout.create();
    private final ComboBox<PerlVersion> myTargetPerlVersionComboBox;

    private final MutableFlatDataModel<VirtualFile> myLibsModel = FlatDataModel.of(List.of());
    private final ListBox<VirtualFile> myLibsList = ListBox.create(myLibsModel);

    private final CheckBox mySimpleMainCheckbox = CheckBox.create(LocalizeValue.of(PerlBundle.message("perl.config.simple.main")));
    private final CheckBox myAutoInjectionCheckbox =
      CheckBox.create(LocalizeValue.of(PerlBundle.message("perl.config.heredoc.injections")));
    private final CheckBox myAllowInjectionWithInterpolation =
      CheckBox.create(LocalizeValue.of(PerlBundle.message("perl.config.heredoc.injections.qq")));
    private final CheckBox myAllowRegexpInjections = CheckBox.create(LocalizeValue.of(PerlBundle.message("perl.config.regex.injections")));
    private final CheckBox myPerlAnnotatorCheckBox = CheckBox.create(LocalizeValue.of(PerlBundle.message("perl.config.annotations.cw")));
    private final CheckBox myPerlCriticCheckBox = CheckBox.create(LocalizeValue.of(PerlBundle.message("perl.config.annotations.critic")));
    private final CheckBox myEnablePerlSwitchCheckbox = CheckBox.create(LocalizeValue.of(PerlBundle.message("perl.config.enable.switch")));

    private final TextBoxWithExpandAction myPerlCriticArgsInputField = createArgumentsField("perl.config.critic.cmd.arguments");
    private final TextBoxWithExpandAction myPerlTidyArgsInputField = createArgumentsField("perl.config.tidy.options.label.short");
    private final TextBox myDeparseArgumentsTextField = TextBox.create();

    private final MutableFlatDataModel<String> myStrictProvidersModel = FlatDataModel.of(List.of());
    private final ListBox<String> myStrictProvidersList = ListBox.create(myStrictProvidersModel);
    private final MutableFlatDataModel<String> myWarningsProvidersModel = FlatDataModel.of(List.of());
    private final ListBox<String> myWarningsProvidersList = ListBox.create(myWarningsProvidersModel);
    private final MutableFlatDataModel<String> mySelfNamesModel = FlatDataModel.of(List.of());
    private final ListBox<String> mySelfNamesList = ListBox.create(mySelfNamesModel);

    @RequiredUIAccess
    private Form() {
      // PerlVersion#ALL_VERSIONS contains equal versions (5.40 and 5.42 are aliases of 5.36 and 5.38), combobox items must be unique
      myTargetPerlVersionComboBox = ComboBox.create(new ArrayList<>(new LinkedHashSet<>(PerlVersion.ALL_VERSIONS)));
      myTargetPerlVersionComboBox.setRenderer((presentation, item) -> {
        PerlVersion value = item.getValue();
        if (value == null) {
          return;
        }
        presentation.append(value.getStrictDottedVersion());
        String versionDescription = PerlVersion.PERL_VERSION_DESCRIPTIONS.get(value);
        if (StringUtil.isNotEmpty(versionDescription)) {
          presentation.append(" (" + versionDescription + ")");
        }
      });

      myLibsList.setRenderer((presentation, item) -> {
        VirtualFile value = item.getValue();
        if (value == null) {
          return;
        }
        presentation.withIcon(PerlIcons.PERL_LANGUAGE_ICON);
        presentation.append(FileUtil.toSystemDependentName(value.getPath()));
      });

      myAllowRegexpInjections.setEnabled(false);
      myAllowRegexpInjections.setVisible(false);
    }

    @RequiredUIAccess
    private static TextBoxWithExpandAction createArgumentsField(String dialogTitleKey) {
      // upstream: RawCommandLineEditor
      return TextBoxWithExpandAction.create(PlatformIconGroup.actionsShow(),
                                            StringUtil.trimEnd(PerlBundle.message(dialogTitleKey), ":"),
                                            ParametersListUtil.DEFAULT_LINE_PARSER,
                                            ParametersListUtil.DEFAULT_LINE_JOINER);
    }
  }
}
