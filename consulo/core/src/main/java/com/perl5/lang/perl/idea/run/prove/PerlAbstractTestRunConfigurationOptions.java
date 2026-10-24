package com.perl5.lang.perl.idea.run.prove;

import com.dslplatform.json.CompiledJson;
import com.perl5.lang.perl.idea.run.GenericPerlRunConfigurationOptions;
import consulo.util.lang.StringUtil;
import consulo.util.xml.serializer.annotation.Attribute;
import org.jspecify.annotations.Nullable;

@CompiledJson
public class PerlAbstractTestRunConfigurationOptions extends GenericPerlRunConfigurationOptions {
    private int myJobsNumber = 1;
    private @Nullable String myTestScriptParameters;

    @Attribute("jobsNumber")
    public int getJobsNumber() {
        return myJobsNumber;
    }

    public void setJobsNumber(int jobsNumber) {
        myJobsNumber = jobsNumber;
    }

    @Attribute("testScriptParameters")
    public @Nullable String getTestScriptParameters() {
        return myTestScriptParameters;
    }

    public void setTestScriptParameters(@Nullable String testScriptParameters) {
        myTestScriptParameters = StringUtil.nullize(testScriptParameters, true);
    }
}
