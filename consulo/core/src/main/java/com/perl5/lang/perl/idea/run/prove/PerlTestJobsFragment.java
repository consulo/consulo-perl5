package com.perl5.lang.perl.idea.run.prove;

import com.perl5.PerlBundle;
import consulo.disposer.Disposable;
import consulo.execution.configuration.fragment.RunConfigurationFragment;
import consulo.execution.configuration.fragment.RunConfigurationFragmentEditor;
import consulo.execution.configuration.fragment.RunConfigurationFragmentLayout;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.ui.ComboBox;
import consulo.ui.annotation.RequiredUIAccess;

import java.util.ArrayList;
import java.util.List;

import static com.perl5.lang.perl.idea.run.PerlConfigurationUIUtil.fragmentName;
import static com.perl5.lang.perl.idea.run.PerlConfigurationUIUtil.labelText;

public class PerlTestJobsFragment implements RunConfigurationFragment<PerlAbstractTestRunConfiguration> {
  private static final int MAX_JOBS_NUMBER = 32;

  @Override
  public String getId() {
    return "perlTestJobs";
  }

  @Override
  public LocalizeValue getName() {
    return fragmentName(PerlBundle.message("perl.run.option.jobs.number"));
  }

  @Override
  public boolean isVisibleByDefault(PerlAbstractTestRunConfiguration target) {
    return target.getJobsNumber() != PerlAbstractTestRunConfiguration.DEFAULT_JOBS_NUMBER;
  }

  @Override
  public void applyHidden(PerlAbstractTestRunConfiguration target) {
    target.setJobsNumber(PerlAbstractTestRunConfiguration.DEFAULT_JOBS_NUMBER);
  }

  @RequiredUIAccess
  @Override
  public RunConfigurationFragmentEditor<PerlAbstractTestRunConfiguration> createEditor(Project project, Disposable parent) {
    List<Integer> jobs = new ArrayList<>();
    for (int i = 1; i <= MAX_JOBS_NUMBER; i++) {
      jobs.add(i);
    }
    ComboBox<Integer> jobsNumber = ComboBox.create(jobs);

    return new RunConfigurationFragmentEditor<>() {
      @RequiredUIAccess
      @Override
      public void addRows(RunConfigurationFragmentLayout layout) {
        layout.addLabeled(labelText(PerlBundle.message("perl.run.option.jobs.number")), jobsNumber);
      }

      @RequiredUIAccess
      @Override
      public void reset(PerlAbstractTestRunConfiguration target) {
        jobsNumber.setValue(target.getJobsNumber(), false);
      }

      @RequiredUIAccess
      @Override
      public void apply(PerlAbstractTestRunConfiguration target) {
        Integer value = jobsNumber.getValue();
        target.setJobsNumber(value != null ? value : PerlAbstractTestRunConfiguration.DEFAULT_JOBS_NUMBER);
      }
    };
  }
}
