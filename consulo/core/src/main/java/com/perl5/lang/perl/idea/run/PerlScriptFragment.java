package com.perl5.lang.perl.idea.run;

import com.perl5.PerlBundle;
import consulo.disposer.Disposable;
import consulo.execution.configuration.fragment.RunConfigurationFragment;
import consulo.execution.configuration.fragment.RunConfigurationFragmentEditor;
import consulo.execution.configuration.fragment.RunConfigurationFragmentLayout;
import consulo.execution.configuration.fragment.StandardRunConfigurationFragmentOrder;
import consulo.fileChooser.FileChooserDescriptorFactory;
import consulo.fileChooser.FileChooserTextBoxBuilder;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.util.lang.StringUtil;

import static com.perl5.lang.perl.idea.run.PerlConfigurationUIUtil.fragmentName;
import static com.perl5.lang.perl.idea.run.PerlConfigurationUIUtil.labelText;

/**
 * The script a perl configuration runs, chosen among the files of its producer.
 */
public class PerlScriptFragment<T extends GenericPerlRunConfiguration> implements RunConfigurationFragment<T> {
  private final GenericPerlRunConfigurationProducer<?> myProducer;

  public PerlScriptFragment(GenericPerlRunConfigurationProducer<?> producer) {
    myProducer = producer;
  }

  @Override
  public String getId() {
    return "perlScript";
  }

  @Override
  public LocalizeValue getName() {
    return fragmentName(PerlBundle.message("perl.run.option.script"));
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
  public RunConfigurationFragmentEditor<T> createEditor(Project project, Disposable parent) {
    FileChooserTextBoxBuilder.Controller script = FileChooserTextBoxBuilder.create(project)
      .fileChooserDescriptor(FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor().withFileFilter(myProducer::isOurFile))
      .dialogTitle(LocalizeValue.of(PerlBundle.message("perl.run.config.select.script.header")))
      .dialogDescription(LocalizeValue.of(PerlBundle.message("perl.run.config.select.script.prompt")))
      .uiDisposable(parent)
      .build();

    return new RunConfigurationFragmentEditor<>() {
      @RequiredUIAccess
      @Override
      public void addRows(RunConfigurationFragmentLayout layout) {
        layout.addLabeled(labelText(PerlBundle.message("perl.run.option.script")), script.getComponent());
      }

      @RequiredUIAccess
      @Override
      public void reset(T target) {
        script.setValue(StringUtil.notNullize(target.getScriptPath()), false);
      }

      @RequiredUIAccess
      @Override
      public void apply(T target) {
        target.setScriptPath(script.getValue());
      }
    };
  }
}
