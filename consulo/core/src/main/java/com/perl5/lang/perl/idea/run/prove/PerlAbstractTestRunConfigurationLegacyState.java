package com.perl5.lang.perl.idea.run.prove;

import consulo.util.xml.serializer.annotation.Tag;
import org.jspecify.annotations.Nullable;

/**
 * The settings a perl test configuration wrote with the bean serializer before the fragment model.
 */
public class PerlAbstractTestRunConfigurationLegacyState {
    @Tag("JOBS_NUMBER")
    public int jobsNumber = 1;
    @Tag("TEST_SCRIPT_PARAMETERS")
    public @Nullable String testScriptParameters = "";
}
