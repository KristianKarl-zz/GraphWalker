package org.graphwalker.jenkins.plugin;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.plugins.analysis.core.PluginDescriptor;

@Extension(ordinal = 1)
public final class GraphWalkerDescriptor extends PluginDescriptor {

    public GraphWalkerDescriptor() {
        super(GraphWalkerPublisher.class);
    }

    @Override
    public String getPluginName() {
        return GraphWalkerPlugin.DISPLAY_NAME;
    }

    @Override
    public String getIconUrl() {
        return GraphWalkerPlugin.ICON_FILE_NAME;
    }

    @Override
    public boolean isApplicable(Class<? extends AbstractProject> aClass) {
        return true;
    }

    @Override
    public String getDisplayName() {
        return GraphWalkerPlugin.DISPLAY_NAME;
    }
}

