package com.perl5.lang.perl.idea.run;

import com.perl5.PerlBundle;
import com.perl5.lang.perl.idea.sdk.PerlSdkType;
import consulo.disposer.Disposable;
import consulo.execution.ui.CommonProgramParametersLayout;
import consulo.fileChooser.FileChooserDescriptorFactory;
import consulo.fileChooser.FileChooserTextBoxBuilder;
import consulo.localize.LocalizeValue;
import consulo.module.ui.BundleBox;
import consulo.module.ui.BundleBoxBuilder;
import consulo.perl5.core.sdk.PerlAddInterpreterButton;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.process.cmd.ParametersListUtil;
import consulo.project.Project;
import consulo.ui.Button;
import consulo.ui.CheckBox;
import consulo.ui.ComboBox;
import consulo.ui.Component;
import consulo.ui.TextBox;
import consulo.ui.TextBoxWithExpandAction;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.dialog.DialogService;
import consulo.ui.layout.DockLayout;
import consulo.ui.util.FormBuilder;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.LocalFileSystem;
import consulo.virtualFileSystem.VirtualFile;
import org.jspecify.annotations.Nullable;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.perl5.lang.perl.idea.run.GenericPerlRunConfiguration.PREREQUISITES_JOINER;
import static com.perl5.lang.perl.idea.run.GenericPerlRunConfiguration.PREREQUISITES_PARSER;
import static com.perl5.lang.perl.idea.run.PerlConfigurationUIUtil.labelText;

/**
 * Unified UI version of upstream {@link GenericPerlRunConfigurationEditorPanel}: the "General" part of perl run configurations editor.
 * Rows: script, program parameters, working directory, environment variables (from {@link CommonProgramParametersLayout}),
 * perl arguments, output encoding, required modules, subclass rows ({@link #addRows(FormBuilder)}) and an alternative interpreter.
 */
public abstract class GenericPerlRunConfigurationLayout<Configuration extends GenericPerlRunConfiguration>
  extends CommonProgramParametersLayout<Configuration> {
  /**
   * Console charset item meaning "not set", upstream combobox has no selection in this case
   */
  private static final String NO_CHARSET = "";

  protected final Project myProject;
  protected final Disposable myUIDisposable;

  private TextBox myScriptBox;
  private final ComboBox<String> myConsoleCharset;
  private final TextBoxWithExpandAction myPerlArguments;
  private final TextBoxWithExpandAction myRequiredModules;
  private final CheckBox myAlternativeSdkCheckBox;
  private final BundleBox myAlternativeSdkBox;
  private final Button myAddSdkButton;

  @RequiredUIAccess
  protected GenericPerlRunConfigurationLayout(Project project, Disposable uiDisposable) {
    super(project.getApplication().getInstance(DialogService.class));
    myProject = project;
    myUIDisposable = uiDisposable;

    List<String> charsets = new ArrayList<>();
    charsets.add(NO_CHARSET);
    charsets.addAll(Charset.availableCharsets().keySet());
    myConsoleCharset = ComboBox.create(charsets);
    myConsoleCharset.setTextRenderer(charset -> charset == null ? LocalizeValue.empty() : LocalizeValue.of(charset));

    myPerlArguments = TextBoxWithExpandAction.create(
      PlatformIconGroup.actionsShow(),
      PerlBundle.message("perl.run.option.perl.arguments"),
      ParametersListUtil.DEFAULT_LINE_PARSER,
      ParametersListUtil.DEFAULT_LINE_JOINER);

    myRequiredModules = TextBoxWithExpandAction.create(
      PlatformIconGroup.actionsShow(), PerlBundle.message("label.required.modules"), PREREQUISITES_PARSER, PREREQUISITES_JOINER);
    myRequiredModules.setToolTipText(
      LocalizeValue.of(PerlBundle.message("tooltip.comma.separated.list.modules.required.to.run.configuration")));

    myAlternativeSdkCheckBox = CheckBox.create(LocalizeValue.of(PerlBundle.message("perl.run.config.use.alternative.label")));
    myAlternativeSdkBox = BundleBoxBuilder.create(uiDisposable)
      .withSdkTypeFilterByType(PerlSdkType.getInstance())
      .withNoneItem()
      .build();
    myAddSdkButton = createAddSdkButton();
    myAlternativeSdkCheckBox.addValueListener(event -> updateAlternativeSdkState());
    updateAlternativeSdkState();
  }

  @Override
  protected Project getProject() {
    return myProject;
  }

  /**
   * @return a run configuration producer for the run configuration
   */
  protected abstract GenericPerlRunConfigurationProducer<? extends Configuration> getRunConfigurationProducer();

  protected String getScriptFieldLabelText() {
    return PerlBundle.message("perl.run.option.script");
  }

  /**
   * @return text box holding {@link GenericPerlRunConfiguration#getScriptPath()}, invoked on {@link #build()}
   */
  @RequiredUIAccess
  protected TextBox createScriptBox() {
    return FileChooserTextBoxBuilder.create(myProject)
      .fileChooserDescriptor(FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor()
                               .withFileFilter(getRunConfigurationProducer()::isOurFile))
      .dialogTitle(LocalizeValue.of(PerlBundle.message("perl.run.config.select.script.header")))
      .dialogDescription(LocalizeValue.of(PerlBundle.message("perl.run.config.select.script.prompt")))
      .uiDisposable(myUIDisposable)
      .build()
      .getComponent();
  }

  /**
   * @return a component to show in the form for the {@code scriptBox} created by {@link #createScriptBox()}
   */
  @RequiredUIAccess
  protected Component wrapScriptBox(TextBox scriptBox) {
    return scriptBox;
  }

  /**
   * Adds labeled rows after required modules and before an alternative interpreter
   */
  @RequiredUIAccess
  protected void addRows(FormBuilder builder) {
  }

  @RequiredUIAccess
  @Override
  protected void addBefore(FormBuilder builder) {
    TextBox scriptBox = createScriptBox();
    scriptBox.addValueListener(event -> updateConsoleCharset(event.getValue()));
    myScriptBox = scriptBox;
    builder.addLabeled(labelText(getScriptFieldLabelText()), wrapScriptBox(scriptBox));
  }

  @RequiredUIAccess
  @Override
  protected void addAfter(FormBuilder builder) {
    builder.addLabeled(labelText(PerlBundle.message("perl.run.option.perl.arguments")), myPerlArguments);
    builder.addLabeled(labelText(PerlBundle.message("perl.run.option.output.encoding")), myConsoleCharset);
    builder.addLabeled(labelText(PerlBundle.message("label.required.modules")), myRequiredModules);
    addRows(builder);
    builder.addLabeled(myAlternativeSdkCheckBox, DockLayout.create().center(myAlternativeSdkBox.getComponent()).right(myAddSdkButton));
  }

  @RequiredUIAccess
  private void updateConsoleCharset(@Nullable String scriptPath) {
    VirtualFile file = StringUtil.isEmpty(scriptPath) ? null : LocalFileSystem.getInstance().findFileByPath(scriptPath);
    myConsoleCharset.setValue(file != null ? file.getCharset().displayName() : NO_CHARSET);
  }

  @RequiredUIAccess
  private void updateAlternativeSdkState() {
    boolean enabled = Boolean.TRUE.equals(myAlternativeSdkCheckBox.getValue());
    myAlternativeSdkBox.getComponent().setEnabled(enabled);
    myAddSdkButton.setEnabled(enabled);
  }

  /**
   * Interpreters are created the same way as with upstream {@code Perl5SdkConfigurable}: with host handler and version manager
   */
  @RequiredUIAccess
  private Button createAddSdkButton() {
    // TODO consulo: upstream Perl5SdkConfigurable also has rename/delete interpreter actions, they are available in the bundles settings
    return PerlAddInterpreterButton.create(myProject, myUIDisposable,
                                           sdk -> PerlAddInterpreterButton.selectCreatedSdk(myAlternativeSdkBox, sdk));
  }

  @RequiredUIAccess
  @Override
  public void reset(Configuration configuration) {
    myScriptBox.setValue(StringUtil.notNullize(configuration.getScriptPath()));
    myRequiredModules.setValue(configuration.getRequiredModules());
    myConsoleCharset.setValue(StringUtil.notNullize(configuration.getConsoleCharset(), NO_CHARSET));
    myPerlArguments.setValue(StringUtil.notNullize(configuration.getPerlArguments()));
    myAlternativeSdkCheckBox.setValue(configuration.isUseAlternativeSdk());
    myAlternativeSdkBox.setSelectedBundle(configuration.getAlternativeSdkName());
    updateAlternativeSdkState();
    super.reset(configuration);
  }

  @RequiredUIAccess
  @Override
  public void apply(Configuration configuration) {
    configuration.setRequiredModules(StringUtil.notNullize(myRequiredModules.getValue()));
    configuration.setScriptPath(myScriptBox.getValue());
    configuration.setConsoleCharset(StringUtil.nullize(myConsoleCharset.getValue(), true));
    configuration.setPerlArguments(myPerlArguments.getValue());
    configuration.setUseAlternativeSdk(Boolean.TRUE.equals(myAlternativeSdkCheckBox.getValue()));
    configuration.setAlternativeSdkName(myAlternativeSdkBox.getSelectedBundleName());
    super.apply(configuration);
  }
}
