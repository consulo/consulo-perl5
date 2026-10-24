package com.perl5.lang.perl.idea.run;

import com.dslplatform.json.CompiledJson;
import consulo.execution.configuration.RunConfigurationOptions;
import consulo.util.lang.StringUtil;
import consulo.util.xml.serializer.annotation.Attribute;
import org.jspecify.annotations.Nullable;

/**
 * The settings of a perl configuration; a {@code null} script charset or start mode means the default one.
 */
@CompiledJson
public class GenericPerlRunConfigurationOptions extends RunConfigurationOptions {
    private @Nullable String myScriptPath;
    private @Nullable String myPerlArguments;
    private @Nullable String myConsoleCharset;
    private boolean myUseAlternativeSdk;
    private @Nullable String myAlternativeSdkName;
    private @Nullable String myRequiredModules;
    private @Nullable String myScriptCharset;
    private @Nullable String myStartMode;
    private boolean myNonInteractiveModeEnabled;
    private boolean myCompileTimeBreakpointsEnabled;
    private @Nullable String myInitCode;

    @Attribute("scriptPath")
    public @Nullable String getScriptPath() {
        return myScriptPath;
    }

    public void setScriptPath(@Nullable String scriptPath) {
        myScriptPath = StringUtil.nullize(scriptPath, true);
    }

    @Attribute("perlArguments")
    public @Nullable String getPerlArguments() {
        return myPerlArguments;
    }

    public void setPerlArguments(@Nullable String perlArguments) {
        myPerlArguments = StringUtil.nullize(perlArguments, true);
    }

    @Attribute("consoleCharset")
    public @Nullable String getConsoleCharset() {
        return myConsoleCharset;
    }

    public void setConsoleCharset(@Nullable String consoleCharset) {
        myConsoleCharset = StringUtil.nullize(consoleCharset, true);
    }

    @Attribute("useAlternativeSdk")
    public boolean isUseAlternativeSdk() {
        return myUseAlternativeSdk;
    }

    public void setUseAlternativeSdk(boolean useAlternativeSdk) {
        myUseAlternativeSdk = useAlternativeSdk;
    }

    @Attribute("alternativeSdkName")
    public @Nullable String getAlternativeSdkName() {
        return myAlternativeSdkName;
    }

    public void setAlternativeSdkName(@Nullable String alternativeSdkName) {
        myAlternativeSdkName = StringUtil.nullize(alternativeSdkName, true);
    }

    @Attribute("requiredModules")
    public @Nullable String getRequiredModules() {
        return myRequiredModules;
    }

    public void setRequiredModules(@Nullable String requiredModules) {
        myRequiredModules = StringUtil.nullize(requiredModules, true);
    }

    @Attribute("scriptCharset")
    public @Nullable String getScriptCharset() {
        return myScriptCharset;
    }

    public void setScriptCharset(@Nullable String scriptCharset) {
        myScriptCharset = StringUtil.nullize(scriptCharset, true);
    }

    @Attribute("startMode")
    public @Nullable String getStartMode() {
        return myStartMode;
    }

    public void setStartMode(@Nullable String startMode) {
        myStartMode = StringUtil.nullize(startMode, true);
    }

    @Attribute("nonInteractiveModeEnabled")
    public boolean isNonInteractiveModeEnabled() {
        return myNonInteractiveModeEnabled;
    }

    public void setNonInteractiveModeEnabled(boolean nonInteractiveModeEnabled) {
        myNonInteractiveModeEnabled = nonInteractiveModeEnabled;
    }

    @Attribute("compileTimeBreakpointsEnabled")
    public boolean isCompileTimeBreakpointsEnabled() {
        return myCompileTimeBreakpointsEnabled;
    }

    public void setCompileTimeBreakpointsEnabled(boolean compileTimeBreakpointsEnabled) {
        myCompileTimeBreakpointsEnabled = compileTimeBreakpointsEnabled;
    }

    @Attribute("initCode")
    public @Nullable String getInitCode() {
        return myInitCode;
    }

    public void setInitCode(@Nullable String initCode) {
        myInitCode = StringUtil.nullize(initCode, true);
    }
}
