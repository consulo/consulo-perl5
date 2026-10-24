package com.perl5.lang.perl.idea.run;

import com.perl5.PerlBundle;
import consulo.disposer.Disposable;
import consulo.execution.configuration.fragment.RunConfigurationFragment;
import consulo.execution.configuration.fragment.RunConfigurationFragmentEditor;
import consulo.execution.configuration.fragment.RunConfigurationFragmentLayout;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.ui.ComboBox;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.util.lang.StringUtil;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.perl5.lang.perl.idea.run.PerlConfigurationUIUtil.fragmentName;
import static com.perl5.lang.perl.idea.run.PerlConfigurationUIUtil.labelText;

/**
 * The charset of the console output; without it the charset of the script is used.
 */
public class PerlConsoleCharsetFragment<T extends GenericPerlRunConfiguration> implements RunConfigurationFragment<T> {
  @Override
  public String getId() {
    return "perlConsoleCharset";
  }

  @Override
  public LocalizeValue getName() {
    return fragmentName(PerlBundle.message("perl.run.option.output.encoding"));
  }

  @Override
  public boolean isVisibleByDefault(T target) {
    return !StringUtil.isEmpty(target.getConsoleCharset());
  }

  @Override
  public void applyHidden(T target) {
    target.setConsoleCharset(null);
  }

  @RequiredUIAccess
  @Override
  public RunConfigurationFragmentEditor<T> createEditor(Project project, Disposable parent) {
    List<String> charsets = new ArrayList<>(Charset.availableCharsets().keySet());
    ComboBox<String> charset = ComboBox.create(charsets);
    charset.setTextRenderer(value -> value == null ? LocalizeValue.empty() : LocalizeValue.of(value));

    return new RunConfigurationFragmentEditor<>() {
      @RequiredUIAccess
      @Override
      public void addRows(RunConfigurationFragmentLayout layout) {
        layout.addLabeled(labelText(PerlBundle.message("perl.run.option.output.encoding")), charset);
      }

      @RequiredUIAccess
      @Override
      public void reset(T target) {
        String value = target.getConsoleCharset();
        charset.setValue(StringUtil.isEmpty(value) ? Charset.defaultCharset().name() : value, false);
      }

      @RequiredUIAccess
      @Override
      public void apply(T target) {
        target.setConsoleCharset(charset.getValue());
      }
    };
  }
}
