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

package com.perl5.lang.perl.idea.run;

import consulo.execution.CommonProgramRunConfigurationParameters;
import consulo.execution.ui.awt.CommonProgramParametersPanel;
import consulo.fileChooser.FileChooserDescriptorFactory;
import consulo.project.Project;
import consulo.content.bundle.Sdk;
import com.intellij.openapi.projectRoots.impl.PerlSdkTable;

import consulo.application.util.NotNullLazyValue;
import consulo.application.util.HtmlChunk;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.LocalFileSystem;
import consulo.virtualFileSystem.VirtualFile;

import consulo.ui.ex.awt.JBCheckBox;
import consulo.localize.LocalizeValue;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.ui.TextBoxWithExpandAction;
import consulo.ui.ex.awtUnsafe.TargetAWT;
import consulo.util.lang.ObjectUtil;
import consulo.ui.ex.awt.UIUtil;
import com.perl5.PerlBundle;
import com.perl5.lang.perl.idea.configuration.settings.sdk.Perl5SdkConfigurable;
import com.perl5.lang.perl.idea.configuration.settings.sdk.Perl5SdkManipulator;
import com.perl5.lang.perl.idea.configuration.settings.sdk.wrappers.Perl5RealSdkWrapper;
import com.perl5.lang.perl.idea.configuration.settings.sdk.wrappers.Perl5SdkWrapper;
import net.miginfocom.swing.MigLayout;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.perl5.lang.perl.idea.run.GenericPerlRunConfiguration.PREREQUISITES_JOINER;
import static com.perl5.lang.perl.idea.run.GenericPerlRunConfiguration.PREREQUISITES_PARSER;
import consulo.ui.ex.awt.ComboBox;
import consulo.ui.ex.awt.LabeledComponent;
import consulo.ui.ex.awt.TextComponentAccessor;
import consulo.ui.ex.awt.TextFieldWithBrowseButton;
import consulo.ui.ex.awt.VerticalFlowLayout;
import consulo.ui.ex.awt.CollectionComboBoxModel;
import consulo.ui.ex.awt.event.DocumentAdapter;
import consulo.ui.ex.awt.PanelWithAnchor;
import consulo.execution.ui.awt.RawCommandLineEditor;
import consulo.ui.ex.awt.TextAccessor;

public abstract class GenericPerlRunConfigurationEditorPanel<Configuration extends GenericPerlRunConfiguration>
  extends CommonProgramParametersPanel implements Perl5SdkManipulator {
  private @Nullable Sdk mySdkProxy;

  private final @NotNull Project myProject;

  private LabeledComponent<?> myScriptLabeledField;
  private JPanel myScriptField;

  private LabeledComponent<?> myLabeledConsoleCharset;
  private ComboBox<String> myConsoleCharset;

  private LabeledComponent<RawCommandLineEditor> myLabeledPerlArgumentsPanel;
  private RawCommandLineEditor myPerlArgumentsPanel;

  private @NotNull JBCheckBox myAlternativeSdkCheckbox;
  private @NotNull Perl5SdkConfigurable mySdkConfigurable;

  // TODO consulo: no Swing ExpandableTextField in Consulo API, uni UI TextBoxWithExpandAction is embedded into the Swing form
  private TextBoxWithExpandAction myRequiredModulesField;
  private LabeledComponent<JComponent> myRequiredModulesLabeledField;

  public GenericPerlRunConfigurationEditorPanel(@NotNull Project project) {
    // TODO consulo: CommonProgramParametersPanel has no project-aware constructor in Consulo
    super();
    myProject = project;
    mySdkConfigurable.setProject(project);
  }

  @Override
  protected void addComponents() {
    createLabeledComponents();
    add(myScriptLabeledField);
    super.addComponents();
    getLabeledComponents().forEach(this::add);
    add(createAlternativeSdkPanel());

    setLayout(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 5, true, false));
    setProgramParametersLabel(getProgramArgumentsLabel());
  }

  protected void createLabeledComponents() {
    createConsoleEncodingField();
    createPerlArgumentsField();
    createScriptField();
    createPrerequisitesField();
  }

  protected void createPrerequisitesField() {
    myRequiredModulesField = TextBoxWithExpandAction.create(
      PlatformIconGroup.actionsShow(), PerlBundle.message("label.required.modules"), PREREQUISITES_PARSER, PREREQUISITES_JOINER);
    myRequiredModulesLabeledField = createLabeledComponent(
      (JComponent)TargetAWT.to(myRequiredModulesField),
      PerlBundle.message("label.required.modules"),
      PerlBundle.message("tooltip.comma.separated.list.modules.required.to.run.configuration"));
  }

  protected static @NotNull <T extends JComponent> LabeledComponent<T> createLabeledComponent(@NotNull T component,
                                                                                              @NotNull String label,
                                                                                              @Nullable String tooltip) {
    var labeledComponent = LabeledComponent.create(component, label).setLabelLocation(BorderLayout.WEST);
    if (tooltip != null) {
      labeledComponent.setToolTipText(HtmlChunk.text(tooltip).toString());
      component.setToolTipText(HtmlChunk.text(tooltip).toString());
    }
    return labeledComponent;
  }

  protected @NotNull List<LabeledComponent<?>> getLabeledComponents() {
    return Arrays.asList(myLabeledPerlArgumentsPanel, myLabeledConsoleCharset, myRequiredModulesLabeledField);
  }

  protected @Nls @NotNull String getProgramArgumentsLabel() {
    return PerlBundle.message("perl.run.option.script.arguments");
  }

  protected void createPerlArgumentsField() {
    myPerlArgumentsPanel = new RawCommandLineEditor();
    myLabeledPerlArgumentsPanel = LabeledComponent.create(myPerlArgumentsPanel, PerlBundle.message("perl.run.option.perl.arguments"));
    myLabeledPerlArgumentsPanel.setLabelLocation(BorderLayout.WEST);
    copyDialogCaption(myLabeledPerlArgumentsPanel);
  }

  private void createConsoleEncodingField() {
    myConsoleCharset = new ComboBox<>(new CollectionComboBoxModel<>(new ArrayList<>(Charset.availableCharsets().keySet())));
    myLabeledConsoleCharset = LabeledComponent.create(myConsoleCharset, PerlBundle.message("perl.run.option.output.encoding"));
    myLabeledConsoleCharset.setLabelLocation(BorderLayout.WEST);
  }

  protected @NotNull TextFieldWithBrowseButton createTextFieldForScript() {
    TextFieldWithBrowseButton fieldWithBrowseButton = new TextFieldWithBrowseButton();
    fieldWithBrowseButton.addBrowseFolderListener(
      LocalizeValue.empty(),
      LocalizeValue.empty(),
      myProject,
      FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor()
        .withFileFilter(getRunConfigurationProducer()::isOurFile)
        .withTitle(PerlBundle.message("perl.run.config.select.script.header"))
        .withDescription(PerlBundle.message("perl.run.config.select.script.prompt")),
      TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT);
    return fieldWithBrowseButton;
  }

  protected @NotNull JPanel doCreateScriptField() {
    TextFieldWithBrowseButton scriptField = createTextFieldForScript();

    scriptField.getTextField().getDocument().addDocumentListener(new DocumentAdapter() {
      @Override
      protected void textChanged(@NotNull DocumentEvent documentEvent) {
        VirtualFile file = LocalFileSystem.getInstance().findFileByPath(scriptField.getText());
        if (file != null) {
          myConsoleCharset.setSelectedItem(file.getCharset().displayName());
        }
        else {
          myConsoleCharset.setSelectedItem(null);
        }
      }
    });
    return scriptField;
  }

  protected @NotNull String getScriptFieldLabelText() {
    return PerlBundle.message("perl.run.option.script");
  }

  private void createScriptField() {
    myScriptField = doCreateScriptField();
    if (!(myScriptField instanceof TextAccessor)) {
      throw new RuntimeException("Script field must be a TextAccessor");
    }
    myScriptLabeledField = LabeledComponent.create(myScriptField, getScriptFieldLabelText());
    myScriptLabeledField.setLabelLocation(BorderLayout.WEST);
  }

  private @NotNull JPanel createAlternativeSdkPanel() {
    JPanel alternativeSdkPanel = new JPanel(new MigLayout("ins 0, gap 10, fill, flowx"));
    myAlternativeSdkCheckbox = new JBCheckBox();
    mySdkConfigurable = new Perl5SdkConfigurable(this, null);
    alternativeSdkPanel.add(myAlternativeSdkCheckbox, "shrinkx");
    JComponent sdkComponent = mySdkConfigurable.getComponent();
    mySdkConfigurable.setLabelText(PerlBundle.message("perl.run.config.use.alternative.label"));
    alternativeSdkPanel.add(sdkComponent, "growx, pushx");
    myAlternativeSdkCheckbox.addChangeListener(e -> mySdkConfigurable.setEnabled(myAlternativeSdkCheckbox.isSelected()));
    return alternativeSdkPanel;
  }

  @Override
  protected final void setupAnchor() {
    super.setupAnchor();
    List<PanelWithAnchor> components = new ArrayList<>(getLabeledComponents());
    components.addFirst(myScriptLabeledField);
    components.addFirst(this);
    myAnchor = UIUtil.mergeComponentsWithAnchor(components);
  }

  protected void reset(Configuration runConfiguration) {
    ((TextAccessor)myScriptField).setText(runConfiguration.getScriptPath());
    myRequiredModulesField.setValue(runConfiguration.getRequiredModules());
    myConsoleCharset.setSelectedItem(runConfiguration.getConsoleCharset());
    myPerlArgumentsPanel.setText(runConfiguration.getPerlArguments());
    myAlternativeSdkCheckbox.setSelected(runConfiguration.isUseAlternativeSdk());
    mySdkConfigurable.setEnabled(runConfiguration.isUseAlternativeSdk());
    mySdkProxy = PerlSdkTable.getInstance().findJdk(runConfiguration.getAlternativeSdkName());
    mySdkConfigurable.reset();
    super.reset(runConfiguration);
  }

  protected void applyTo(Configuration runConfiguration) {
    runConfiguration.setRequiredModules(StringUtil.notNullize(myRequiredModulesField.getValue()));
    runConfiguration.setScriptPath(((TextAccessor)myScriptField).getText());
    runConfiguration.setConsoleCharset(StringUtil.nullize((String)myConsoleCharset.getSelectedItem(), true));
    runConfiguration.setPerlArguments(myPerlArgumentsPanel.getText());
    runConfiguration.setUseAlternativeSdk(myAlternativeSdkCheckbox.isSelected());
    mySdkConfigurable.apply();
    runConfiguration.setAlternativeSdkName(ObjectUtil.doIfNotNull(mySdkProxy, Sdk::getName));
    super.applyTo(runConfiguration);
  }

  @Override
  public final void reset(@NotNull CommonProgramRunConfigurationParameters configuration) {
    throw new RuntimeException("Should not be invoked");
  }

  @Override
  public final void applyTo(@NotNull CommonProgramRunConfigurationParameters configuration) {
    throw new RuntimeException("Should not be invoked");
  }

  public void disposeUIResources() {
    mySdkConfigurable.disposeUIResources();
  }

  @Override
  public Perl5SdkWrapper getCurrentSdkWrapper() {
    return mySdkProxy == null ? null : new Perl5RealSdkWrapper(mySdkProxy);
  }

  @Override
  public void setSdk(@Nullable Sdk sdk) {
    mySdkProxy = sdk;
  }

  /**
   * @return a run configuration producer for the run configuration
   */
  protected abstract @NotNull GenericPerlRunConfigurationProducer<? extends Configuration> getRunConfigurationProducer();
}
