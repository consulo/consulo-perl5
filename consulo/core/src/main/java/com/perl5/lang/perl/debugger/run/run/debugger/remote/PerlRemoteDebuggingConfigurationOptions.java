package com.perl5.lang.perl.debugger.run.run.debugger.remote;

import com.dslplatform.json.CompiledJson;
import consulo.execution.configuration.RunConfigurationOptions;
import consulo.util.lang.StringUtil;
import consulo.util.xml.serializer.annotation.Attribute;
import org.jspecify.annotations.Nullable;

/**
 * The settings of a remote perl debugging configuration; {@code null} values mean the defaults of the configuration.
 */
@CompiledJson
public class PerlRemoteDebuggingConfigurationOptions extends RunConfigurationOptions {
    private @Nullable String myDebugHost;
    private int myDebugPort = 12345;
    private @Nullable String myRemoteProjectRoot;
    private @Nullable String myLocalProjectRoot;
    private @Nullable String myScriptCharset;
    private @Nullable String myPerlRole;
    private @Nullable String myStartMode;
    private boolean myNonInteractiveModeEnabled;
    private boolean myCompileTimeBreakpointsEnabled;
    private @Nullable String myInitCode;
    private boolean myReconnect;

    @Attribute("debugHost")
    public @Nullable String getDebugHost() {
        return myDebugHost;
    }

    public void setDebugHost(@Nullable String debugHost) {
        myDebugHost = StringUtil.nullize(debugHost, true);
    }

    @Attribute("debugPort")
    public int getDebugPort() {
        return myDebugPort;
    }

    public void setDebugPort(int debugPort) {
        myDebugPort = debugPort;
    }

    @Attribute("remoteProjectRoot")
    public @Nullable String getRemoteProjectRoot() {
        return myRemoteProjectRoot;
    }

    public void setRemoteProjectRoot(@Nullable String remoteProjectRoot) {
        myRemoteProjectRoot = StringUtil.nullize(remoteProjectRoot, true);
    }

    @Attribute("localProjectRoot")
    public @Nullable String getLocalProjectRoot() {
        return myLocalProjectRoot;
    }

    public void setLocalProjectRoot(@Nullable String localProjectRoot) {
        myLocalProjectRoot = StringUtil.nullize(localProjectRoot, true);
    }

    @Attribute("scriptCharset")
    public @Nullable String getScriptCharset() {
        return myScriptCharset;
    }

    public void setScriptCharset(@Nullable String scriptCharset) {
        myScriptCharset = StringUtil.nullize(scriptCharset, true);
    }

    @Attribute("perlRole")
    public @Nullable String getPerlRole() {
        return myPerlRole;
    }

    public void setPerlRole(@Nullable String perlRole) {
        myPerlRole = StringUtil.nullize(perlRole, true);
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

    @Attribute("reconnect")
    public boolean isReconnect() {
        return myReconnect;
    }

    public void setReconnect(boolean reconnect) {
        myReconnect = reconnect;
    }
}
