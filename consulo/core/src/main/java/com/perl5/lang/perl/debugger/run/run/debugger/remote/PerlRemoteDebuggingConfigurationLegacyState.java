package com.perl5.lang.perl.debugger.run.run.debugger.remote;

import consulo.util.xml.serializer.annotation.Tag;
import org.jspecify.annotations.Nullable;

/**
 * The settings a remote perl debugging configuration wrote with the bean serializer before the fragment model.
 */
public class PerlRemoteDebuggingConfigurationLegacyState {
    public @Nullable String debugHost = "localhost";
    public int debugPort = 12345;
    public @Nullable String remoteProjectRoot = "/home/";
    public @Nullable String localProjectRoot = "";
    public @Nullable String scriptCharset = "utf8";
    public @Nullable String perlRole = "client";
    public @Nullable String startMode = "RUN";
    public boolean isNonInteractiveModeEnabled;
    public boolean isCompileTimeBreakpointsEnabled;
    public @Nullable String initCode = "";
    @Tag("auto-reconnect")
    public boolean reconnect;
}
