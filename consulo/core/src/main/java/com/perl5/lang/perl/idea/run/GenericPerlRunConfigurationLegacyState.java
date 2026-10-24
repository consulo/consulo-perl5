package com.perl5.lang.perl.idea.run;

import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The settings a perl configuration wrote with the bean serializer before the fragment model.
 */
public class GenericPerlRunConfigurationLegacyState {
    public @Nullable String scriptPath;
    public @Nullable String programParameters;
    public @Nullable String perlArguments = "";
    public @Nullable String workingDirectory;
    public Map<String, String> envs = new LinkedHashMap<>();
    public boolean passParentEnvs = true;
    public @Nullable String consoleCharset;
    public boolean useAlternativeSdk;
    public @Nullable String alternativeSdkName;
    public @Nullable String requiredModules = "";
    public @Nullable String scriptCharset = "utf8";
    public @Nullable String startMode = "RUN";
    public boolean nonInteractiveModeEnabled;
    public boolean compileTimeBreakpointsEnabled;
    public @Nullable String initCode = "";
}
