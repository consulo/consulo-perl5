package com.perl5.lang.perl.idea.run.prove;

import com.perl5.PerlBundle;
import com.perl5.lang.perl.idea.run.GenericPerlRunConfiguration;
import com.perl5.lang.perl.idea.run.GenericPerlRunConfigurationLayout;
import com.perl5.lang.perl.idea.run.GenericPerlRunConfigurationUIEditor;
import consulo.disposer.Disposable;
import consulo.fileChooser.FileChooser;
import consulo.fileChooser.FileChooserDescriptor;
import consulo.localize.LocalizeValue;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.process.cmd.ParametersListUtil;
import consulo.project.Project;
import consulo.ui.Button;
import consulo.ui.ButtonStyle;
import consulo.ui.ComboBox;
import consulo.ui.Component;
import consulo.ui.TextBox;
import consulo.ui.TextBoxWithExpandAction;
import consulo.ui.UIAccess;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.layout.DockLayout;
import consulo.ui.util.FormBuilder;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.VirtualFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.perl5.lang.perl.idea.run.GenericPerlRunConfiguration.FILES_JOINER;
import static com.perl5.lang.perl.idea.run.GenericPerlRunConfiguration.FILES_PARSER;
import static com.perl5.lang.perl.idea.run.PerlConfigurationUIUtil.labelText;

/**
 * Unified UI version of upstream {@link PerlTestRunConfigurationEditor}
 */
class PerlTestRunConfigurationUIEditor extends GenericPerlRunConfigurationUIEditor<PerlAbstractTestRunConfiguration> {
  private static final int MAX_JOBS_NUMBER = 32;

  private final PerlAbstractTestRunConfigurationProducer<? extends PerlAbstractTestRunConfiguration> myProducer;

  PerlTestRunConfigurationUIEditor(Project project,
                                   PerlAbstractTestRunConfigurationProducer<? extends PerlAbstractTestRunConfiguration> producer) {
    super(project);
    myProducer = producer;
  }

  @RequiredUIAccess
  @Override
  protected GenericPerlRunConfigurationLayout<PerlAbstractTestRunConfiguration> createGeneralLayout() {
    return new ArgumentsLayout(myProject, this);
  }

  // TODO consulo: CommonProgramParametersLayout has no API to change the program parameters label,
  //  upstream labels it with perl.run.test.runner.arguemnts
  private class ArgumentsLayout extends GenericPerlRunConfigurationLayout<PerlAbstractTestRunConfiguration> {
    private final ComboBox<Integer> myJobsComboBox;
    private final TextBoxWithExpandAction myTestScriptArguments;

    @RequiredUIAccess
    ArgumentsLayout(Project project, Disposable uiDisposable) {
      super(project, uiDisposable);

      List<Integer> jobs = new ArrayList<>();
      for (int i = 1; i <= MAX_JOBS_NUMBER; i++) {
        jobs.add(i);
      }
      myJobsComboBox = ComboBox.create(jobs);

      myTestScriptArguments = TextBoxWithExpandAction.create(
        PlatformIconGroup.actionsShow(),
        PerlBundle.message("perl.run.option.test.script.arguments"),
        ParametersListUtil.DEFAULT_LINE_PARSER,
        ParametersListUtil.DEFAULT_LINE_JOINER);
    }

    @Override
    protected PerlAbstractTestRunConfigurationProducer<? extends PerlAbstractTestRunConfiguration> getRunConfigurationProducer() {
      return myProducer;
    }

    @Override
    protected String getScriptFieldLabelText() {
      return PerlBundle.message("perl.run.prove.option.script.label");
    }

    /**
     * Several test files and directories may be selected, they are joined with {@link GenericPerlRunConfiguration#FILES_JOINER}
     */
    @RequiredUIAccess
    @Override
    protected TextBox createScriptBox() {
      return TextBoxWithExpandAction.create(PlatformIconGroup.actionsShow(), getScriptFieldLabelText(), FILES_PARSER, FILES_JOINER);
    }

    @RequiredUIAccess
    @Override
    protected Component wrapScriptBox(TextBox scriptBox) {
      Button browseButton = Button.create(LocalizeValue.empty(), event -> chooseTests(scriptBox));
      browseButton.setIcon(PlatformIconGroup.nodesFolderopened());
      browseButton.setToolTipText(LocalizeValue.of(PerlBundle.message("perl.run.prove.config.select.script.header")));
      browseButton.addStyle(ButtonStyle.INPLACE);
      return DockLayout.create().center(scriptBox).right(browseButton);
    }

    @RequiredUIAccess
    private void chooseTests(TextBox scriptBox) {
      List<VirtualFile> filesToSelect = GenericPerlRunConfiguration.computeVirtualFilesFromPaths(scriptBox.getValue());
      UIAccess uiAccess = UIAccess.current();
      // TODO consulo: Consulo file chooser preselects a single file only, upstream preselects all configured tests
      FileChooser.chooseFiles(createFileChooserDescriptor(), myProject, filesToSelect.isEmpty() ? null : filesToSelect.getFirst())
        .whenComplete((chosenFiles, error) -> {
          if (error == null && chosenFiles != null && chosenFiles.length > 0) {
            // upstream joins chosen paths with a space, but paths are parsed with FILES_PARSER
            uiAccess.give(() -> scriptBox.setValue(GenericPerlRunConfiguration.computePathsFromVirtualFiles(Arrays.asList(chosenFiles))));
          }
        });
    }

    private FileChooserDescriptor createFileChooserDescriptor() {
      FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(true, true, false, false, false, true) {
        @Override
        public boolean isFileVisible(VirtualFile file, boolean showHiddenFiles) {
          return getRunConfigurationProducer().isOurFile(file);
        }
      };
      return fileChooserDescriptor
        .withTitle(LocalizeValue.of(PerlBundle.message("perl.run.prove.config.select.script.header")))
        .withDescription(LocalizeValue.of(PerlBundle.message("perl.run.prove.config.select.script.prompt")));
    }

    @RequiredUIAccess
    @Override
    protected void addRows(FormBuilder builder) {
      builder.addLabeled(labelText(PerlBundle.message("perl.run.option.jobs.number")), myJobsComboBox);
      builder.addLabeled(labelText(PerlBundle.message("perl.run.option.test.script.arguments")), myTestScriptArguments);
    }

    @RequiredUIAccess
    @Override
    public void reset(PerlAbstractTestRunConfiguration runConfiguration) {
      super.reset(runConfiguration);
      myJobsComboBox.setValue(runConfiguration.getJobsNumber());
      myTestScriptArguments.setValue(StringUtil.notNullize(runConfiguration.getTestScriptParameters()));
    }

    @RequiredUIAccess
    @Override
    public void apply(PerlAbstractTestRunConfiguration runConfiguration) {
      super.apply(runConfiguration);
      Integer jobsNumber = myJobsComboBox.getValue();
      runConfiguration.setJobsNumber(jobsNumber != null ? jobsNumber : PerlAbstractTestRunConfiguration.DEFAULT_JOBS_NUMBER);
      runConfiguration.setTestScriptParameters(myTestScriptArguments.getValue());
    }
  }
}
