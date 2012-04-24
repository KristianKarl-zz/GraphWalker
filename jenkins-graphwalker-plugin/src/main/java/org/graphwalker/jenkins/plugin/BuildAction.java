package org.graphwalker.jenkins.plugin;

import hudson.model.AbstractBuild;
import hudson.model.Action;

public class BuildAction implements Action {

    private final AbstractBuild<?, ?> myBuild;

    public BuildAction(AbstractBuild<?, ?> build) {
        myBuild = build;
    }

    public synchronized AbstractBuild<?, ?> getBuild() {
        return myBuild;
    }

    public String getIconFileName() {
        return PluginImpl.ICON_FILE_NAME;
    }

    public String getDisplayName() {
        return PluginImpl.DISPLAY_NAME;
    }

    public String getUrlName() {
        return PluginImpl.URL_NAME;
    }
}
