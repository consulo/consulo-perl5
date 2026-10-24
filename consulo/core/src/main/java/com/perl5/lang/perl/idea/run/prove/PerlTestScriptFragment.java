package com.perl5.lang.perl.idea.run.prove;

import com.perl5.PerlBundle;
import com.perl5.lang.perl.idea.run.GenericPerlRunConfiguration;
import consulo.disposer.Disposable;
import consulo.execution.configuration.fragment.RunConfigurationFragment;
import consulo.execution.configuration.fragment.RunConfigurationFragmentEditor;
import consulo.execution.configuration.fragment.RunConfigurationFragmentLayout;
import consulo.execution.configuration.fragment.StandardRunConfigurationFragmentOrder;
import consulo.fileChooser.FileChooser;
import consulo.fileChooser.FileChooserDescriptor;
import consulo.localize.LocalizeValue;
import consulo.platform.base.icon.PlatformIconGroup;
import consulo.project.Project;
import consulo.ui.Button;
import consulo.ui.ButtonStyle;
import consulo.ui.TextBoxWithExpandAction;
import consulo.ui.UIAccess;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.layout.DockLayout;
import consulo.util.lang.StringUtil;
import consulo.virtualFileSystem.VirtualFile;

import java.util.Arrays;
import java.util.List;

import static com.perl5.lang.perl.idea.run.GenericPerlRunConfiguration.FILES_JOINER;
import static com.perl5.lang.perl.idea.run.GenericPerlRunConfiguration.FILES_PARSER;
import static com.perl5.lang.perl.idea.run.PerlConfigurationUIUtil.fragmentName;
import static com.perl5.lang.perl.idea.run.PerlConfigurationUIUtil.labelText;

/**
 * Test files and directories a perl test configuration runs, joined with {@link GenericPerlRunConfiguration#FILES_JOINER}.
 */
public class PerlTestScriptFragment implements RunConfigurationFragment<PerlAbstractTestRunConfiguration> {
  private final PerlAbstractTestRunConfigurationProducer<?> myProducer;

  public PerlTestScriptFragment(PerlAbstractTestRunConfigurationProducer<?> producer) {
    myProducer = producer;
  }

  @Override
  public String getId() {
    return "perlTestScript";
  }

  @Override
  public LocalizeValue getName() {
    return fragmentName(PerlBundle.message("perl.run.prove.option.script.label"));
  }

  @Override
  public int getOrder() {
    return StandardRunConfigurationFragmentOrder.FIRST;
  }

  @Override
  public boolean isRemovable() {
    return false;
  }

  @RequiredUIAccess
  @Override
  public RunConfigurationFragmentEditor<PerlAbstractTestRunConfiguration> createEditor(Project project, Disposable parent) {
    String label = PerlBundle.message("perl.run.prove.option.script.label");
    TextBoxWithExpandAction tests = TextBoxWithExpandAction.create(PlatformIconGroup.actionsShow(), label, FILES_PARSER, FILES_JOINER);
    Button browse = Button.create(LocalizeValue.empty(), event -> chooseTests(project, tests));
    browse.setIcon(PlatformIconGroup.nodesFolderopened());
    browse.setToolTipText(LocalizeValue.of(PerlBundle.message("perl.run.prove.config.select.script.header")));
    browse.addStyle(ButtonStyle.INPLACE);
    DockLayout row = DockLayout.create().center(tests).right(browse);

    return new RunConfigurationFragmentEditor<>() {
      @RequiredUIAccess
      @Override
      public void addRows(RunConfigurationFragmentLayout layout) {
        layout.addLabeled(labelText(label), row);
      }

      @RequiredUIAccess
      @Override
      public void reset(PerlAbstractTestRunConfiguration target) {
        tests.setValue(StringUtil.notNullize(target.getScriptPath()), false);
      }

      @RequiredUIAccess
      @Override
      public void apply(PerlAbstractTestRunConfiguration target) {
        target.setScriptPath(tests.getValue());
      }
    };
  }

  @RequiredUIAccess
  private void chooseTests(Project project, TextBoxWithExpandAction tests) {
    List<VirtualFile> filesToSelect = GenericPerlRunConfiguration.computeVirtualFilesFromPaths(tests.getValue());
    UIAccess uiAccess = UIAccess.current();
    FileChooser.chooseFiles(createFileChooserDescriptor(), project, filesToSelect.isEmpty() ? null : filesToSelect.getFirst())
      .whenComplete((chosenFiles, error) -> {
        if (error == null && chosenFiles != null && chosenFiles.length > 0) {
          uiAccess.give(() -> tests.setValue(GenericPerlRunConfiguration.computePathsFromVirtualFiles(Arrays.asList(chosenFiles))));
        }
      });
  }

  private FileChooserDescriptor createFileChooserDescriptor() {
    FileChooserDescriptor descriptor = new FileChooserDescriptor(true, true, false, false, false, true) {
      @Override
      public boolean isFileVisible(VirtualFile file, boolean showHiddenFiles) {
        return myProducer.isOurFile(file);
      }
    };
    return descriptor
      .withTitle(LocalizeValue.of(PerlBundle.message("perl.run.prove.config.select.script.header")))
      .withDescription(LocalizeValue.of(PerlBundle.message("perl.run.prove.config.select.script.prompt")));
  }
}
