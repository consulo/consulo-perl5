package com.perl5.lang.perl.idea.run;

import com.perl5.PerlBundle;
import com.perl5.lang.perl.fileTypes.PerlFileTypeScript;
import com.perl5.lang.perl.idea.run.debugger.PerlDebugOptions;
import com.perl5.lang.perl.idea.run.debugger.PerlDebugOptionsSets;
import consulo.document.Document;
import consulo.language.editor.ui.EditorBox;
import consulo.language.editor.ui.EditorBoxBuilderFactory;
import consulo.language.psi.PsiDocumentManager;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiFileFactory;
import consulo.localize.LocalizeValue;
import consulo.project.Project;
import consulo.ui.CheckBox;
import consulo.ui.ComboBox;
import consulo.ui.Component;
import consulo.ui.Label;
import consulo.ui.PseudoComponent;
import consulo.ui.TextBox;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.layout.DockLayout;
import consulo.ui.layout.VerticalLayout;
import consulo.ui.util.FormBuilder;
import consulo.util.lang.StringUtil;

import static com.perl5.lang.perl.idea.run.PerlConfigurationUIUtil.labelText;

/**
 * Unified UI version of the "Debugging" part of upstream {@link PerlConfigurationEditorBase}: script encoding, debugger startup mode,
 * debugger flags and the initialization code. Subclasses may add own rows and options with {@link #addRows(FormBuilder)} and
 * {@link #addOptions(VerticalLayout)}.
 */
public class PerlDebugOptionsLayout<Settings extends PerlDebugOptions> implements PseudoComponent {
  protected final Project myProject;

  private final TextBox myScriptCharset;
  private final ComboBox<String> myStartMode;
  private final CheckBox myIsNonInteractiveModeEnabled;
  private final CheckBox myIsCompileTimeBreakpointsEnabled;
  private final EditorBox myInitCode;

  private Component myComponent;

  @RequiredUIAccess
  public PerlDebugOptionsLayout(Project project) {
    myProject = project;

    myScriptCharset = TextBox.create();

    myStartMode = ComboBox.create(PerlConfigurationUIUtil.orderedKeys(
      PerlDebugOptionsSets.STARTUP_OPTIONS.keySet(),
      PerlDebugOptionsSets.DEBUGGER_STARTUP_COMPILE,
      PerlDebugOptionsSets.DEBUGGER_STARTUP_RUN,
      PerlDebugOptionsSets.DEBUGGER_STARTUP_BREAKPOINT));
    myStartMode.setTextRenderer(mode -> mode == null ? LocalizeValue.empty() : LocalizeValue.of(PerlDebugOptionsSets.STARTUP_OPTIONS.get(mode)));

    myIsNonInteractiveModeEnabled = CheckBox.create(LocalizeValue.of(PerlBundle.message("perl.run.option.debugger.noninteractive.mode")));
    myIsCompileTimeBreakpointsEnabled =
      CheckBox.create(LocalizeValue.of(PerlBundle.message("perl.run.option.debugger.compile.time.breakpoints")));

    PsiFile fileFromText = PsiFileFactory.getInstance(project).createFileFromText("file.dummy", PerlFileTypeScript.INSTANCE, "", 0, true);
    Document document = PsiDocumentManager.getInstance(project).getDocument(fileFromText);
    assert document != null;
    myInitCode = project.getApplication().getInstance(EditorBoxBuilderFactory.class).create(project)
      .document(document)
      .fileType(PerlFileTypeScript.INSTANCE)
      .multiline()
      .build();
  }

  @RequiredUIAccess
  public void build() {
    FormBuilder builder = FormBuilder.create();
    builder.addLabeled(labelText(PerlBundle.message("perl.run.option.script.encoding")), myScriptCharset);
    builder.addLabeled(labelText(PerlBundle.message("perl.run.option.debugger.startup.mode")), myStartMode);
    addRows(builder);

    VerticalLayout options = VerticalLayout.create();
    options.add(myIsNonInteractiveModeEnabled);
    options.add(myIsCompileTimeBreakpointsEnabled);
    addOptions(options);
    options.add(Label.create(labelText(PerlBundle.message("perl.run.option.debugger.init.code"))));

    builder.setBottom(DockLayout.create().top(options).center(myInitCode));
    myComponent = builder.build();
  }

  /**
   * Adds labeled rows after the common ones
   */
  @RequiredUIAccess
  protected void addRows(FormBuilder builder) {
  }

  /**
   * Adds options (e.g. check boxes) after the common ones, before the initialization code
   */
  @RequiredUIAccess
  protected void addOptions(VerticalLayout options) {
  }

  @RequiredUIAccess
  @Override
  public Component getComponent() {
    return myComponent;
  }

  @RequiredUIAccess
  public void reset(Settings settings) {
    myScriptCharset.setValue(StringUtil.notNullize(settings.getScriptCharset()));
    myIsCompileTimeBreakpointsEnabled.setValue(settings.isCompileTimeBreakpointsEnabled());
    myIsNonInteractiveModeEnabled.setValue(settings.isNonInteractiveModeEnabled());
    myInitCode.setValue(StringUtil.notNullize(settings.getInitCode()));
    myStartMode.setValue(settings.getStartMode());
  }

  @RequiredUIAccess
  public void apply(Settings settings) {
    settings.setScriptCharset(myScriptCharset.getValue());
    String startMode = myStartMode.getValue();
    if (startMode != null) {
      settings.setStartMode(startMode);
    }
    settings.setNonInteractiveModeEnabled(Boolean.TRUE.equals(myIsNonInteractiveModeEnabled.getValue()));
    settings.setCompileTimeBreakpointsEnabled(Boolean.TRUE.equals(myIsCompileTimeBreakpointsEnabled.getValue()));
    settings.setInitCode(myInitCode.getValue());
  }
}
