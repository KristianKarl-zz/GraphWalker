package org.graphwalker.maven.plugin;

import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "test-validate"
        , defaultPhase = LifecyclePhase.PROCESS_TEST_CLASSES
        , requiresDependencyResolution = ResolutionScope.TEST)
@Execute(goal = "test-generate")
public class TestValidateMojo extends AbstractValidateMojo {

    @Override
    public void executeMojo() {
        int i = 0;
    }
}
