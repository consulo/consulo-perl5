package com.perl5.lang.perl.idea.run;

import com.perl5.PerlBundle;
import com.perl5.lang.perl.idea.sdk.PerlSdkType;
import consulo.disposer.Disposable;
import consulo.execution.configuration.fragment.RunConfigurationFragment;
import consulo.execution.configuration.fragment.RunConfigurationFragmentEditor;
import consulo.execution.configuration.fragment.RunConfigurationFragmentLayout;
import consulo.localize.LocalizeValue;
import consulo.module.ui.BundleBox;
import consulo.perl5.core.sdk.PerlAddInterpreterButton;
import consulo.module.ui.BundleBoxBuilder;
import consulo.project.Project;
import consulo.ui.Button;
import consulo.ui.Space;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.layout.DockLayout;

import static com.perl5.lang.perl.idea.run.PerlConfigurationUIUtil.fragmentName;
import static com.perl5.lang.perl.idea.run.PerlConfigurationUIUtil.labelText;

/**
 * An interpreter used instead of the one of the script module.
 */
public class PerlAlternativeSdkFragment<T extends GenericPerlRunConfiguration> implements RunConfigurationFragment<T> {
  @Override
  public String getId() {
    return "perlAlternativeSdk";
  }

  @Override
  public LocalizeValue getName() {
    return fragmentName(PerlBundle.message("perl.run.config.use.alternative.label"));
  }

  @Override
  public boolean isVisibleByDefault(T target) {
    return target.isUseAlternativeSdk();
  }

  @Override
  public void applyHidden(T target) {
    target.setUseAlternativeSdk(false);
  }

  @RequiredUIAccess
  @Override
  public RunConfigurationFragmentEditor<T> createEditor(Project project, Disposable parent) {
    BundleBox sdk = BundleBoxBuilder.create(parent)
      .withSdkTypeFilterByType(PerlSdkType.getInstance())
      .withNoneItem()
      .build();
    Button addSdk = PerlAddInterpreterButton.create(project, parent, created -> PerlAddInterpreterButton.selectCreatedSdk(sdk, created));
    DockLayout row = DockLayout.create(Space.SMALL).center(sdk.getComponent()).right(addSdk);

    return new RunConfigurationFragmentEditor<>() {
      @RequiredUIAccess
      @Override
      public void addRows(RunConfigurationFragmentLayout layout) {
        layout.addLabeled(labelText(PerlBundle.message("perl.run.config.use.alternative.label")), row);
      }

      @RequiredUIAccess
      @Override
      public void reset(T target) {
        sdk.setSelectedBundle(target.getAlternativeSdkName());
      }

      @RequiredUIAccess
      @Override
      public void apply(T target) {
        target.setUseAlternativeSdk(true);
        target.setAlternativeSdkName(sdk.getSelectedBundleName());
      }
    };
  }
}
