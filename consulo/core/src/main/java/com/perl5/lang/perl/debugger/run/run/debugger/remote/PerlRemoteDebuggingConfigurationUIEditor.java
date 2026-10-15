package com.perl5.lang.perl.debugger.run.run.debugger.remote;

import com.perl5.lang.perl.debugger.PerlDebuggerBundle;
import com.perl5.lang.perl.idea.run.PerlConfigurationUIUtil;
import com.perl5.lang.perl.idea.run.PerlDebugOptionsLayout;
import com.perl5.lang.perl.idea.run.debugger.PerlDebugOptions;
import com.perl5.lang.perl.idea.run.debugger.PerlDebugOptionsSets;
import consulo.configurable.ConfigurationException;
import consulo.disposer.Disposable;
import consulo.execution.configuration.ui.SettingsEditor;
import consulo.fileChooser.FileChooserDescriptorFactory;
import consulo.fileChooser.FileChooserTextBoxBuilder;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.ui.CheckBox;
import consulo.ui.ComboBox;
import consulo.ui.Component;
import consulo.ui.IntBox;
import consulo.ui.TextBox;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.layout.VerticalLayout;
import consulo.ui.util.FormBuilder;
import consulo.util.lang.StringUtil;
import org.jspecify.annotations.Nullable;

import static com.perl5.lang.perl.debugger.run.run.debugger.PerlDebugProfileState.*;
import static com.perl5.lang.perl.idea.run.PerlConfigurationUIUtil.labelText;

/**
 * Unified UI version of upstream {@link PerlRemoteDebuggingConfigurationEditor}
 */
class PerlRemoteDebuggingConfigurationUIEditor extends SettingsEditor<PerlRemoteDebuggingConfiguration> {
  private static final int MAX_PORT = 65535;

  private final Project myProject;
  private @Nullable RemoteDebuggingLayout myLayout;

  PerlRemoteDebuggingConfigurationUIEditor(Project project) {
    myProject = project;
  }

  @RequiredUIAccess
  @Override
  protected Component createUIComponent() {
    RemoteDebuggingLayout layout = new RemoteDebuggingLayout(myProject, this);
    layout.build();
    myLayout = layout;
    return layout.getComponent();
  }

  @RequiredUIAccess
  @Override
  protected void resetEditorFrom(PerlRemoteDebuggingConfiguration configuration) {
    RemoteDebuggingLayout layout = myLayout;
    if (layout != null) {
      layout.reset(configuration);
    }
  }

  @RequiredUIAccess
  @Override
  protected void applyEditorTo(PerlRemoteDebuggingConfiguration configuration) throws ConfigurationException {
    RemoteDebuggingLayout layout = myLayout;
    if (layout != null) {
      layout.apply(configuration);
    }
  }

  private static class RemoteDebuggingLayout extends PerlDebugOptionsLayout<PerlRemoteDebuggingConfiguration> {
    private final FileChooserTextBoxBuilder.Controller myLocalProjectRoot;
    private final TextBox myRemoteProjectRoot;
    private final ComboBox<String> myPerlRole;
    private final TextBox myDebuggingHost;
    private final IntBox myDebuggingPort;
    private final CheckBox myIsReconnect;
    private final TextBox myGeneratedCommandLine;

    @RequiredUIAccess
    RemoteDebuggingLayout(Project project, Disposable uiDisposable) {
      super(project);

      myLocalProjectRoot = FileChooserTextBoxBuilder.create(project)
        .fileChooserDescriptor(FileChooserDescriptorFactory.createSingleFolderDescriptor())
        .dialogTitle(LocalizeValue.of(PerlDebuggerBundle.message("perl.run.option.select.local.root")))
        .uiDisposable(uiDisposable)
        .build();

      myRemoteProjectRoot = TextBox.create();

      myPerlRole = ComboBox.create(PerlConfigurationUIUtil.orderedKeys(
        PerlDebugOptionsSets.ROLE_OPTIONS.keySet(), PerlDebugOptions.ROLE_SERVER, PerlDebugOptions.ROLE_CLIENT));
      myPerlRole.setTextRenderer(role -> role == null ? LocalizeValue.empty() : LocalizeValue.of(PerlDebugOptionsSets.ROLE_OPTIONS.get(role)));
      myPerlRole.addValueListener(event -> updateCommandLine());

      myDebuggingHost = TextBox.create();
      myDebuggingHost.addValueListener(event -> updateCommandLine());

      myDebuggingPort = IntBox.create();
      myDebuggingPort.setRange(0, MAX_PORT);
      myDebuggingPort.addValueListener(event -> updateCommandLine());

      myIsReconnect = CheckBox.create(LocalizeValue.of(PerlDebuggerBundle.message("perl.run.option.debugger.reconnect")));

      myGeneratedCommandLine = TextBox.create();
      myGeneratedCommandLine.setEditable(false);
      myGeneratedCommandLine.addFocusListener(event -> myGeneratedCommandLine.selectAll());
    }

    @RequiredUIAccess
    @Override
    protected void addRows(FormBuilder builder) {
      builder.addLabeled(labelText(PerlDebuggerBundle.message("perl.run.option.local.root")), myLocalProjectRoot.getComponent());
      builder.addLabeled(labelText(PerlDebuggerBundle.message("perl.run.option.remote.root")), myRemoteProjectRoot);
      builder.addLabeled(labelText(PerlDebuggerBundle.message("perl.run.option.debugger.connection.mode")), myPerlRole);
      builder.addLabeled(labelText(PerlDebuggerBundle.message("perl.run.option.debugger.host")), myDebuggingHost);
      builder.addLabeled(labelText(PerlDebuggerBundle.message("perl.run.option.debugger.port")), myDebuggingPort);
      builder.addLabeled(labelText(PerlDebuggerBundle.message("perl.run.option.debugger.command.line")), myGeneratedCommandLine);
    }

    @RequiredUIAccess
    @Override
    protected void addOptions(VerticalLayout options) {
      options.add(myIsReconnect);
    }

    /**
     * Generates and sets command line according to selected settings
     */
    @RequiredUIAccess
    private void updateCommandLine() {
      Integer port = myDebuggingPort.getValue();
      String commandLine = PERL5_DEBUG_HOST + "=" + StringUtil.notNullize(myDebuggingHost.getValue()) +
                           " " + PERL5_DEBUG_PORT + "=" + (port == null ? "" : port) +
                           " " + PERL5_DEBUG_ROLE + "=" + StringUtil.notNullize(myPerlRole.getValue()) +
                           " perl " + DEBUG_ARGUMENT + " ./your_script.pl";
      myGeneratedCommandLine.setValue(commandLine);
    }

    @RequiredUIAccess
    @Override
    public void reset(PerlRemoteDebuggingConfiguration configuration) {
      myRemoteProjectRoot.setValue(StringUtil.notNullize(configuration.getRemoteProjectRoot()));
      myLocalProjectRoot.setValue(StringUtil.notNullize(configuration.getLocalProjectRoot()));
      myPerlRole.setValue(configuration.getPerlRole());
      myDebuggingHost.setValue(StringUtil.notNullize(configuration.getHostToBind()));
      myDebuggingPort.setValue(configuration.getDebugPort());
      myIsReconnect.setValue(configuration.isReconnect());
      updateCommandLine();
      super.reset(configuration);
    }

    @RequiredUIAccess
    @Override
    public void apply(PerlRemoteDebuggingConfiguration configuration) {
      configuration.setRemoteProjectRoot(myRemoteProjectRoot.getValue());
      configuration.setLocalProjectRoot(myLocalProjectRoot.getValue());
      String perlRole = myPerlRole.getValue();
      if (perlRole != null) {
        configuration.setPerlRole(perlRole);
      }
      configuration.setDebugHost(myDebuggingHost.getValue());
      Integer port = myDebuggingPort.getValue();
      if (port != null) {
        configuration.setDebugPort(port);
      }
      configuration.setReconnect(Boolean.TRUE.equals(myIsReconnect.getValue()));
      super.apply(configuration);
    }
  }
}
