package org.graphwalker.jenkins.plugin;

import hudson.model.AbstractBuild;
import hudson.plugins.analysis.core.AbstractResultAction;
import hudson.plugins.analysis.core.HealthDescriptor;
import hudson.plugins.analysis.core.PluginDescriptor;

public class GraphWalkerResultAction extends AbstractResultAction<GraphWalkerResult> {

    public GraphWalkerResultAction(final AbstractBuild<?, ?> build, final HealthDescriptor descriptor, final GraphWalkerResult result) {
        super(build, new GraphWalkerHealthDescriptor(descriptor), result);
    }

    @Override
    protected PluginDescriptor getDescriptor() {
        return new GraphWalkerDescriptor();
    }

    public String getDisplayName() {
        return GraphWalkerPlugin.DISPLAY_NAME;
    }
}
